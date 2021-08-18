package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ChatterJoinedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final ChatterId invited;
    private final ChatterId invitedBy;

    public ChatterJoinedEvent(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId invitedBy, ChatterId invited, OffsetDateTime dateTime) {
        super(ConversationEventType.CHATTER_JOINED, conversationId, conversationDataId, dateTime);
        this.invitedBy = invitedBy;
        this.invited = invited;
    }
}
