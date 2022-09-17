package io.northernlights.chat.domain.event.conversation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationJoinedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String invited;
    private final String invitedBy;

    @JsonCreator
    public ConversationJoinedEvent(
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("conversation_data_id") String conversationDataId,
        @JsonProperty("invited_by") String invitedBy,
        @JsonProperty("invited") String invited,
        @JsonProperty("timestamp") OffsetDateTime timestamp) {
        super(ConversationEventType.CONVERSATION_JOINED, conversationId, conversationDataId, timestamp);
        this.invitedBy = invitedBy;
        this.invited = invited;
    }
}
