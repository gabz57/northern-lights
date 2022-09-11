package io.northernlights.chat.domain.event.conversation;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMessageSentEvent extends AbstractConversationEvent implements ConversationEvent {
    private final Message message;
    private final String author;

    public ConversationMessageSentEvent(String conversationId, String conversationDataId, Message message, String author, OffsetDateTime dateTime) {
        super(ConversationEventType.CONVERSATION_MESSAGE, conversationId, conversationDataId, dateTime);
        this.message = message;
        this.author = author;
    }

    @Getter
    public static class Message {
        private final String text;

        public Message(String text) {
            this.text = text;
        }
    }
}
