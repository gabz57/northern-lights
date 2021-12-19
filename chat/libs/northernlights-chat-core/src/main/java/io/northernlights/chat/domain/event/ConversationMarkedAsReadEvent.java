package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMarkedAsReadEvent extends AbstractConversationEvent implements ConversationEvent {
    private final ChatterId markedBy;

    public ConversationMarkedAsReadEvent(ConversationId conversationId, ConversationDataId markedConversationDataId, ChatterId markedBy, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_MARKED_AS_READ, conversationId, markedConversationDataId, dateTime);
        this.markedBy = markedBy;
    }
}
