package io.northernlights.chat.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.northernlights.chat.domain.event.conversation.*;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
//    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
//    , defaultImpl = MyInterfaceImpl.class
)
@JsonSubTypes({
    // conversation
    @JsonSubTypes.Type(value = ConversationCreatedEvent.class, name = "CONVERSATION_CREATED"),
    @JsonSubTypes.Type(value = ConversationMessageSentEvent.class, name = "CONVERSATION_MESSAGE"),
    @JsonSubTypes.Type(value = ConversationMarkedAsReadEvent.class, name = "CONVERSATION_MARKED_AS_READ"),
    @JsonSubTypes.Type(value = ConversationJoinedEvent.class, name = "CONVERSATION_JOINED")
    // file
    // team
    // user
})
public interface ChatEvent {

    ChatEventType getType();
    interface ChatEventType {
        String name();
    }
}
