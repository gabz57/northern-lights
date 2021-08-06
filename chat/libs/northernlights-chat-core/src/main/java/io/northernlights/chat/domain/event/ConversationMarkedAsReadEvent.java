package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMarkedAsReadEvent extends AbstractConversationEvent implements ConversationEvent {
    private final ConversationDataId markedConversationDataId;
    private final ChatterId markedBy;

    public ConversationMarkedAsReadEvent(ConversationId conversationId, ConversationDataId conversationDataId, ConversationDataId markedConversationDataId, ChatterId markedBy, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_MARKED_AS_READ, conversationId, conversationDataId, dateTime);
        this.markedConversationDataId = markedConversationDataId;
        this.markedBy = markedBy;
    }
}
