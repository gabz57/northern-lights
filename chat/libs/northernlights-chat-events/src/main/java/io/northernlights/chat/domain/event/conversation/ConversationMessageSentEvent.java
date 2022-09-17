package io.northernlights.chat.domain.event.conversation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ConversationMessageSentEvent extends AbstractConversationEvent implements ConversationEvent {
    private final Message message;
    private final String author;

    @JsonCreator
    public ConversationMessageSentEvent(
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("conversation_data_id") String conversationDataId,
        @JsonProperty("message") Message message,
        @JsonProperty("author") String author,
        @JsonProperty("timestamp") OffsetDateTime timestamp) {
        super(ConversationEventType.CONVERSATION_MESSAGE, conversationId, conversationDataId, timestamp);
        this.message = message;
        this.author = author;
        String a = "{" +
            "\"type\": \"CONVERSATION_MESSAGE\", " +
            "\"author\": \"910a1c05-a0d5-44f7-a860-905df67e424e\", " +
            "\"message\": {" +
                "\"text\": \"and ?\"" +
                "}, " +
            "\"timestamp\": \"2022-09-17T13:46:53.182939552Z\", " +
            "\"conversation_id\": \"3761353e-9156-4275-bd24-da1abbb4fcaf\", " +
            "\"conversation_data_id\": \"5\"" +
            "}";
    }

    @Getter
    public static class Message {
        private final String text;

        @JsonCreator
        public Message(
            @JsonProperty("text") String text) {
            this.text = text;
        }
    }
}
