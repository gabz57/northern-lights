package io.northernlights.chat.domain.model.conversation.event;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;

public interface ConversationEvent {
    ConversationId getConversationId();

    ConversationDataId getConversationDataId();
}
