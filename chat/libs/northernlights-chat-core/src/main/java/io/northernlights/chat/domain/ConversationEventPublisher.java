package io.northernlights.chat.domain;

import io.northernlights.chat.domain.model.conversation.event.ConversationEvent;

public interface ConversationEventPublisher {
    void publish(ConversationEvent conversationEvent);
}
