package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

@Getter
public class ConversationMessageSentEvent extends AbstractConversationEvent implements ConversationEvent {
    private final Message message;
    private final ChatterId author;

    public ConversationMessageSentEvent(ConversationId conversationId, ConversationDataId conversationDataId, Message message, ChatterId author) {
        super(ConversationEventType.CONVERSATION_MESSAGE, conversationId, conversationDataId);
        this.message = message;
        this.author = author;
    }
}
