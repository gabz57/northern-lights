package io.northernlights.chat.domain.event.conversation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public abstract class AbstractConversationEvent {
    private final ConversationEvent.ConversationEventType type;
    private final String conversationId;
    private final String conversationDataId;
    private final OffsetDateTime timestamp;

    @JsonCreator
    protected AbstractConversationEvent(
        @JsonProperty("type") ConversationEvent.ConversationEventType conversationEventType,
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("conversation_data_id") String conversationDataId,
        @JsonProperty("timestamp") OffsetDateTime timestamp) {
        this.type = conversationEventType;
        this.conversationId = conversationId;
        this.conversationDataId = conversationDataId;
        this.timestamp = timestamp;
    }
}
