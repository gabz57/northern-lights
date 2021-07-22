package io.northernlights.chat.domain.event;

import io.northernlights.chat.domain.event.ConversationEvent.ConversationEventType;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

@Getter
public abstract class AbstractConversationEvent {
    private final ConversationId conversationId;
    private final ConversationDataId conversationDataId;
    private final ConversationEventType conversationEventType;

    protected AbstractConversationEvent(ConversationEventType conversationEventType, ConversationId conversationId, ConversationDataId conversationDataId) {
        this.conversationId = conversationId;
        this.conversationDataId = conversationDataId;
        this.conversationEventType = conversationEventType;
    }
}
