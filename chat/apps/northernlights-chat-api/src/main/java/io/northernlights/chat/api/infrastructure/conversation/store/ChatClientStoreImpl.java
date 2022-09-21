package io.northernlights.chat.api.infrastructure.conversation.store;

import io.northernlights.chat.api.domain.client.ChatClientStore;
import io.northernlights.chat.api.domain.client.ChatEventDataAdapter;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.api.domain.client.model.ChatDataChatters;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class ChatClientStoreImpl implements ChatClientStore {
    private final ConversationStore conversationStore;
    private final ChatterStore chatterStore;
    private final SseKeyStore sseKeyStore;
    private final ChatEventDataAdapter chatEventDataAdapter;

    @Transactional
    public Flux<ChatData> loadPreviousData(ChatClientID chatClientId, SseChatKey sseChatKey) {
        return sseKeyStore.useConversationStatusesBySseChatKey(sseChatKey)
            // when empty, let Mono.empty() to skip previous data
            .zipWith(conversationStore.listConversationIds(chatClientId.getChatterId()))
            .flatMapMany(t -> loadConversationsUsingMarkers(t.getT2(), t.getT1()));
    }

    private Flux<ChatData> loadConversationsUsingMarkers(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
        return Flux.concat(
                installChatters(),
                conversationStatuses.isEmpty()
                    ? installWorkspace(conversationIds) // new client
                    : updateWorkspace(conversationIds, conversationStatuses))
            .doFirst(() -> log.info("Loading install data for conversations : {}", conversationIds)); // client with data
    }

    private Flux<ChatData> installChatters() {
        return chatterStore.listChatters()
            .map(lst -> ChatDataChatters.builder().chatters(lst).build())
            .cast(ChatData.class)
            .flux();
    }

    private Flux<ChatData> installWorkspace(List<ConversationId> conversationIds) {
        return Flux.fromStream(conversationIds.stream())
            .flatMap(this::installConversation);
    }

    @Transactional(readOnly = true)
    public Mono<ChatData> installConversation(ConversationId conversationId) {
        return conversationStore.participants(conversationId)
            .flatMap(chatterIds -> Mono
                    .zip(
                        // FIXME: use conversation (3rd param) instead of querying twice conversation
                        conversationStore.conversationDetails(conversationId),
                        Mono.just(chatterIds),
//                    conversationStore.conversationData(conversationId, null).collectList().map(Conversation::new),
                        conversationStore.conversation(conversationId, null), // TODO: limit to 100 messages ? (don't fetch all history)
                        conversationStore.readMarkers(conversationId).collectList())
                    .map(tuple -> chatEventDataAdapter.adaptConversationInstall(
                        conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()))
                    .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                        + ofNullable(o).map(Object::toString).orElse(""), e))
            ).doFirst(() -> log.info("Loading full data for conversation : {}", conversationId.getId()));
    }

    private Flux<ChatData> updateWorkspace(List<ConversationId> conversationIds, Map<ConversationId, ConversationDataId> conversationStatuses) {
        return Flux.fromStream(conversationIds.stream())
            .flatMap(conversationId -> completeConversation(conversationId, conversationStatuses));
    }

    private Mono<ChatData> completeConversation(ConversationId conversationId, Map<ConversationId, ConversationDataId> conversationStatuses) {
        return conversationStore.participants(conversationId)
            .flatMap(chatterIds -> Mono
                .zip(
                    Mono.just(chatterIds),
                    conversationStore.conversation(conversationId, conversationStatuses.get(conversationId)),
                    conversationStore.readMarkers(conversationId).collectList())
                .map(tuple -> chatEventDataAdapter.adaptConversationUpdate(
                    conversationId, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .onErrorContinue((e, o) -> log.error("Failed to load chat install data content with object : "
                    + ofNullable(o).map(Object::toString).orElse(""), e))
            ).doFirst(() -> log.info("Loading partial data"));
    }

    @Transactional(readOnly = true)
    public Mono<List<ConversationId>> loadConversationIds(ChatClientID chatClientId) {
        return conversationStore.listConversationIds(chatClientId.getChatterId());
    }

    @Transactional(readOnly = true)
    public Mono<ChatterId> authenticate(SseChatKey sseChatKey) {
        return sseKeyStore.findChatterIdBySseChatKey(sseChatKey);
    }

    @Transactional
    public void revoke(SseChatKey sseChatKey) {
        sseKeyStore.revoke(sseChatKey)
            .doFirst(() -> log.info("Revoking sseChatKey {}", sseChatKey)).subscribe();
    }
}
