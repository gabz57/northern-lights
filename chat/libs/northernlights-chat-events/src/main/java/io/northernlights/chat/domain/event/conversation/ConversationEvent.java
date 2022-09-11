package io.northernlights.chat.domain.event.conversation;

import io.northernlights.chat.domain.event.ChatEvent;

import java.time.OffsetDateTime;

public interface ConversationEvent extends ChatEvent {
    ConversationEventType getType();

    String getConversationId();

    String getConversationDataId();

    OffsetDateTime getTimestamp();

    enum ConversationEventType implements ChatEventType {
        CONVERSATION_CREATED,
        CONVERSATION_MESSAGE,
        CONVERSATION_MARKED_AS_READ,
        //        CONVERSATION_MESSAGE_UPDATED,
        //        CONVERSATION_CHATTER_TYPING,
        //
        //        CONVERSATION_INVITED,
        //        CONVERSATION_INVITE_CLOSED,
        CONVERSATION_JOINED,
        //        CONVERSATION_LEFT,
        //
        //        CONVERSATION_ARCHIVED,
        //        CONVERSATION_UNARCHIVED,
    }
}
