package io.northernlights.chat.domain.event.conversation;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationJoinedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String invited;
    private final String invitedBy;

    public ConversationJoinedEvent(String conversationId, String conversationDataId, String invitedBy, String invited, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_JOINED, conversationId, conversationDataId, dateTime);
        this.invitedBy = invitedBy;
        this.invited = invited;
    }
}
