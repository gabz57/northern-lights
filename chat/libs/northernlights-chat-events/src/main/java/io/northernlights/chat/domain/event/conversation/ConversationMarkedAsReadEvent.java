package io.northernlights.chat.domain.event.conversation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMarkedAsReadEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String markedBy;

    @JsonCreator
    public ConversationMarkedAsReadEvent(
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("conversation_data_id") String markedConversationDataId,
        @JsonProperty("marked_by") String markedBy,
        @JsonProperty("timestamp") OffsetDateTime timestamp) {
        super(ConversationEventType.CONVERSATION_MARKED_AS_READ, conversationId, markedConversationDataId, timestamp);
        this.markedBy = markedBy;
    }
}
