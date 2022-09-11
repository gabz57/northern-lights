package io.northernlights.chat.domain.store.ssekey;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
public class InMemorySseKeyStore implements SseKeyStore {

    // LIVE
    private final Map<SseChatKey, ChatterId> chatterIdBySseChatKey = new HashMap<>();
    private final Map<SseChatKey, Map<ConversationId, ConversationDataId>> conversationStatusesBySseChatKey = new HashMap<>();

    public Mono<SseChatKey> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus) {
        final SseChatKey sseChatKey = SseChatKey.of(UUID.randomUUID());
        chatterIdBySseChatKey.put(sseChatKey, chatterId);
        conversationStatusesBySseChatKey.put(sseChatKey, conversationStatus);
        return Mono.just(sseChatKey);
    }

    public Mono<ChatterId> findChatterIdBySseChatKey(SseChatKey sseChatKey) {
        log.info("findChatterIdBySseChatKey > {}", sseChatKey);
        return Mono.just(chatterIdBySseChatKey.get(sseChatKey));
    }

    public Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(SseChatKey sseChatKey) {
        return Mono.justOrEmpty(conversationStatusesBySseChatKey.remove(sseChatKey));
    }

    public Mono<Void> revoke(SseChatKey sseChatKey) {
        chatterIdBySseChatKey.remove(sseChatKey);
        conversationStatusesBySseChatKey.remove(sseChatKey);
        return Mono.empty();
    }
}
