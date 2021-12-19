package io.northernlights.chat.store.chatter;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatterStore {
    //    // COLD
    //@table: CHATTERS key ChatterId (uuid)
    //  Map<ChatterId, Chatter> chattersByChatterId = new HashMap<>();

    //@table: CHATTERS_CONVERSATIONS key ChatterId (uuid)
    //  Map<ChatterId, Set<ConversationId>> conversationsIdByChatterId = new HashMap<>();

    Mono<Void> writeConversationCreated(ConversationCreatedEvent conversationEvent);

    Mono<Void> writeChatterJoined(ChatterJoinedEvent conversationEvent);

    Mono<List<ConversationId>> listConversationIds(ChatterId chatterId);

    Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds);

    Mono<Chatter> insertChatter(Chatter creator);
}
