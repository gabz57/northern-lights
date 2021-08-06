package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class ConversationCreatedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final String name;
    private final ChatterId createdBy;
    private final List<ChatterId> participants;

    public ConversationCreatedEvent(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId createdBy, String name, List<ChatterId> participants, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_CREATED, conversationId, conversationDataId, dateTime);
        this.name = name;
        this.createdBy = createdBy;
        this.participants = participants;
    }
}
