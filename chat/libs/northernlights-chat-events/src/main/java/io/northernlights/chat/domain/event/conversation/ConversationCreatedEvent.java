package io.northernlights.chat.domain.event.conversation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@ToString
@Getter
public class ConversationCreatedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String name;
    private final String createdBy;
    private final List<String> participants;
    private final Boolean isPrivate;

    @JsonCreator
    public ConversationCreatedEvent(
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("conversation_data_id") String conversationDataId,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("name") String name,
        @JsonProperty("participants") List<String> participants,
        @JsonProperty("datetime") OffsetDateTime dateTime,
        @JsonProperty("is_private") Boolean isPrivate) {
        super(ConversationEventType.CONVERSATION_CREATED, conversationId, conversationDataId, dateTime);
        this.name = name;
        this.createdBy = createdBy;
        this.participants = participants;
        this.isPrivate = isPrivate;
    }
}
