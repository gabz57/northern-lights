package io.northernlights.chat.store.chatter.domain;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ChatterStore {
    Mono<Void> writeConversationCreated(ConversationCreatedEvent conversationEvent);

    Mono<Void> writeChatterJoined(ChatterJoinedEvent conversationEvent);

//    Mono<Void> writeConversationUpdate(ConversationEvent conversationEvent, List<ChatterId> participants);

    Mono<List<ConversationId>> listConversationIds(ChatterId chatterId);

    Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds);

    Mono<String> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus);

    Mono<ChatterId> findChatterIdBySseChatKey(String sseChatKey);

    Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(String sseChatKey);

    void revoke(String sseChatKey);
}
