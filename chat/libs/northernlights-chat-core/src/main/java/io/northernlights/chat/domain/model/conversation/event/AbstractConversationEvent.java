package io.northernlights.chat.domain.model.conversation.event;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

@Getter
public abstract class AbstractConversationEvent {
    private final ConversationId conversationId;
    private final ConversationDataId conversationDataId;

    protected AbstractConversationEvent(ConversationId conversationId, ConversationDataId conversationDataId) {
        this.conversationId = conversationId;
        this.conversationDataId = conversationDataId;
    }
}
