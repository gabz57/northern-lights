package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Data;

@Data
public class ConversationDataRef {
    private final ConversationId conversationId;
    private final ConversationDataId conversationDataId;

    public static ConversationDataRef of(ConversationId conversationId, ConversationDataId conversationDataId) {
        return new ConversationDataRef(conversationId, conversationDataId);
    }
}
