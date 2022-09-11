package io.northernlights.chat.domain.store.ssekey;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SseKeyStore {

    // TODO: create ConversationStatus containing a Map<ConversationId, ConversationDataId> readMarkers;
    Mono<SseChatKey> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus);

    Mono<ChatterId> findChatterIdBySseChatKey(SseChatKey sseChatKey);

    // TODO: ConversationStatus
    Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(SseChatKey sseChatKey);

    Mono<Void> revoke(SseChatKey sseChatKey);
}
