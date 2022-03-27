package io.northernlights.chat.api.infrastructure.conversation.store;

import io.northernlights.chat.api.domain.client.ChatClientStore;
import io.northernlights.chat.api.domain.client.ChatDataAdapter;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.ssekey.SseKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class ChatClientStoreImpl implements ChatClientStore {

    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;
    private final SseKeyStore sseKeyStore;
    private final ChatDataAdapter chatDataAdapter;

    public Flux<ChatData> loadPreviousData(ChatClientID chatClientId, SseChatKey sseChatKey) {
        return sseKeyStore.useConversationStatusesBySseChatKey(sseChatKey)
            // when empty, let Mono.empty() to skip previous data
            .zipWith(chatterStore.listConversationIds(chatClientId.getChatterId()))
            .flatMapMany(t -> toChatInstallData(t.getT2(), t.getT1()));
    }

    private Flux<ChatData> toChatInstallData(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
        log.info("Loading install data for conversations : {}", conversationIds);
        return conversationStatuses.isEmpty()
            ? getFullData(conversationIds) // new client
            : getPartialData(conversationIds, conversationStatuses); // client with data
    }

    private Flux<ChatData> getFullData(List<ConversationId> conversationIds) {
        return Flux.fromStream(conversationIds.stream()).flatMap(this::loadConversationInstallData);
    }

    public Mono<ChatData> loadConversationInstallData(ConversationId conversationId) {
        log.info("Loading full data for conversation : {}", conversationId.getId());
        return conversationStore.participants(conversationId)
            .flatMap(chatterIds -> Mono
                .zip(
                    // FIXME: use conversation (3rd param) instead of querying twice conversation
                    conversationStore.conversationCreationData(conversationId),
                    chatterStore.listChatters(chatterIds),
//                    conversationStore.conversationData(conversationId, null).collectList().map(Conversation::new),
                    conversationStore.conversation(conversationId, null),
                    conversationStore.readMarkers(conversationId).collectMap(Tuple2::getT1, Tuple2::getT2))
                .map(tuple -> chatDataAdapter.adaptConversationInstallData(
                    conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()))
                .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                    + ofNullable(o).map(Object::toString).orElse(""), e))
            );
    }

    private Flux<ChatData> getPartialData(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
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
                    conversationStore.readMarkers(conversationId).collectMap(Tuple2::getT1, Tuple2::getT2))
                .map(tuple -> chatDataAdapter.adaptPartialColdData(
                    conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                    + ofNullable(o).map(Object::toString).orElse(""), e))
            );
    }

    public Mono<List<ConversationId>> loadConversationIds(ChatClientID chatClientId) {
        return chatterStore.listConversationIds(chatClientId.getChatterId());
    }

    public Mono<ChatterId> authenticate(SseChatKey sseChatKey) {
        return sseKeyStore.findChatterIdBySseChatKey(sseChatKey);
    }

    public void revoke(SseChatKey sseChatKey) {
        log.info("Revoking sseChatKey {}", sseChatKey);
        sseKeyStore.revoke(sseChatKey);
    }
}
