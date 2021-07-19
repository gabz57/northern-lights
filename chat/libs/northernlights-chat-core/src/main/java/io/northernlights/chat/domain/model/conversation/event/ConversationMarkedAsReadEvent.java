package io.northernlights.chat.domain.model.conversation.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

@Getter
public class ConversationMarkedAsReadEvent extends AbstractConversationEvent implements ConversationEvent {
    private final ConversationDataId markedConversationDataId;
    private final ChatterId markedBy;

    public ConversationMarkedAsReadEvent(ConversationId conversationId, ConversationDataId conversationDataId, ConversationDataId markedConversationDataId, ChatterId markedBy) {
        super(conversationId, conversationDataId);
        this.markedConversationDataId = markedConversationDataId;
        this.markedBy = markedBy;
    }
}
