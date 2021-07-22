package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

public interface ConversationData {
    ConversationDataType getConversationDataType();

    ConversationId getConversationId();

    ConversationDataId getConversationDataId();

    ChatterId getChatterId();

    enum ConversationDataType {
        CREATION,
        MESSAGE,
        READ_MARKER
    }

    @Getter
    abstract class AbstractConversationData {
        private final ConversationDataType conversationDataType;
        private final ConversationId conversationId;
        private final ConversationDataId conversationDataId;
        private final ChatterId chatterId;

        protected AbstractConversationData(ConversationDataType conversationDataType, ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId) {
            this.conversationDataType = conversationDataType;
            this.conversationId = conversationId;
            this.conversationDataId = conversationDataId;
            this.chatterId = chatterId;
        }
    }
}
