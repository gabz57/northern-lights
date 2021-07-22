package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;

public interface ConversationEvent {
    ConversationEventType getConversationEventType();

    ConversationId getConversationId();

    ConversationDataId getConversationDataId();

    enum ConversationEventType {
        CONVERSATION_CREATED,
        CONVERSATION_MESSAGE,
        CONVERSATION_MARKED_AS_READ
    }
}
