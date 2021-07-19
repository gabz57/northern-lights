package io.northernlights.chat.store.chatter.domain;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.conversation.event.ConversationEvent;

import java.util.List;

public interface ChatterStore {
    void writeConversationCreated(ConversationCreatedEvent conversationEvent);

    void writeConversationUpdate(ConversationEvent conversationEvent, List<ChatterId> participants);
}
