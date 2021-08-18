package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;

import java.time.OffsetDateTime;

public interface ConversationEvent {
    ConversationEventType getConversationEventType();

    ConversationId getConversationId();

    ConversationDataId getConversationDataId();

    OffsetDateTime getDateTime();

    enum ConversationEventType {
        CONVERSATION_CREATED,
        CONVERSATION_MESSAGE,
        CONVERSATION_MARKED_AS_READ,
        CHATTER_JOINED
    }
}
