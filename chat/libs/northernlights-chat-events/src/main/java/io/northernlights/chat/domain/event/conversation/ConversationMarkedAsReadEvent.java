package io.northernlights.chat.domain.event.conversation;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMarkedAsReadEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String markedBy;

    public ConversationMarkedAsReadEvent(String conversationId, String markedConversationDataId, String markedBy, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_MARKED_AS_READ, conversationId, markedConversationDataId, dateTime);
        this.markedBy = markedBy;
    }
}
