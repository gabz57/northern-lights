package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

public interface ConversationData {
    ConversationId getConversationId();
    ConversationDataId getConversationDataId();
    ChatterId getChatterId();

    @Getter
    abstract class AbstractConversationData {
        private final ConversationId conversationId;
        private final ConversationDataId conversationDataId;
        private final ChatterId chatterId;

        protected AbstractConversationData(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId) {
            this.conversationId = conversationId;
            this.conversationDataId = conversationDataId;
            this.chatterId = chatterId;
        }
    }
}
