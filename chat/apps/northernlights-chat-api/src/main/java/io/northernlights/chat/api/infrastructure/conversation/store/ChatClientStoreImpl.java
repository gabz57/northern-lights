package io.northernlights.chat.api.infrastructure.conversation.store;

import io.northernlights.chat.api.domain.client.ChatClientStore;
import io.northernlights.chat.api.domain.client.ChatDataAdapter;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class ChatClientStoreImpl implements ChatClientStore {

    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;
    private final ChatDataAdapter chatDataAdapter;

    public Flux<ChatData> loadPreviousData(ChatClientID chatClientId, String sseChatKey) {
        return chatterStore.useConversationStatusesBySseChatKey(sseChatKey)
            // when empty, let Mono.empty() to skip previous data
            .zipWith(chatterStore.listConversationIds(chatClientId.getChatterId()))
            .flatMapMany(t -> toChatInstallDatas(t.getT2(), t.getT1()));
    }

    private Flux<ChatData> toChatInstallDatas(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
        log.info("Loading install data for conversations : {}", conversationIds);
        return conversationStatuses.isEmpty()
            ? getFullDatas(conversationIds) // new client
            : getPartialDatas(conversationIds, conversationStatuses); // client with data
    }

    private Flux<ChatData> getFullDatas(List<ConversationId> conversationIds) {
        return Flux.fromStream(conversationIds.stream()).flatMap(this::getFullData);
    }

    private Mono<ChatData> getFullData(ConversationId conversationId) {
        log.info("Loading full data for conversation : {}", conversationId.getId());
        return conversationStore.participants(conversationId)
            .flatMap(chatterIds -> Mono
                .zip(
                    conversationStore.conversationCreationData(conversationId),
                    chatterStore.listChatters(chatterIds),
                    conversationStore.conversation(conversationId, null),
                    conversationStore.readMarkers(conversationId))
                .map(tuple -> chatDataAdapter.adaptFullColdData(
                    conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()))
                .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                    + ofNullable(o).map(Object::toString).orElse(""), e))
            );
    }

    private Flux<ChatData> getPartialDatas(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
        return Flux.fromStream(conversationIds.stream())
            .flatMap(conversationId -> getPartialData(conversationId, conversationStatuses));
    }

    private Mono<ChatData> getPartialData(ConversationId conversationId, Map<ConversationId, ConversationDataId> conversationStatuses) {
        log.info("Loading partial data");
        return conversationStore.participants(conversationId)
            .flatMap(chatterIds -> Mono
                .zip(
                    chatterStore.listChatters(chatterIds),
                    conversationStore.conversation(conversationId, conversationStatuses.get(conversationId)),
                    conversationStore.readMarkers(conversationId))
                .map(tuple -> chatDataAdapter.adaptPartialColdData(
                    conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                    + ofNullable(o).map(Object::toString).orElse(""), e))
            );
    }

    public Mono<List<ConversationId>> loadConversationIds(ChatClientID chatClientId) {
        return chatterStore.listConversationIds(chatClientId.getChatterId());
    }

    public Mono<ChatterId> authenticate(String sseChatKey) {
        return chatterStore.findChatterIdBySseChatKey(sseChatKey);
    }

    public void revoke(String sseChatKey) {
        log.info("Revoking sseChatKey {}", sseChatKey);
        chatterStore.revoke(sseChatKey);
    }
}
