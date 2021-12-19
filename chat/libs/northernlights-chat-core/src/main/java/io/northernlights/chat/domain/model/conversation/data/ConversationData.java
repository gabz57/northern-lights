package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

public interface ConversationData {
    ConversationDataType getConversationDataType();

    ConversationId getConversationId();

    ConversationDataId getConversationDataId();

    ChatterId getChatterId();

    enum ConversationDataType {
        CREATION,
        MESSAGE,
        CHATTER_ADD
    }

    @Getter
    abstract class AbstractConversationData {
        @NonNull
        private final ConversationDataType conversationDataType;
        @NonNull
        private final ConversationId conversationId;
        @NonNull
        private final ConversationDataId conversationDataId;
        @NonNull
        private final ChatterId chatterId;
        @NonNull
        private final OffsetDateTime dateTime;

        protected AbstractConversationData(
               @NonNull ConversationDataType conversationDataType,
               @NonNull ConversationId conversationId,
               @NonNull ConversationDataId conversationDataId,
               @NonNull ChatterId chatterId,
               @NonNull OffsetDateTime dateTime) {
            this.conversationDataType = conversationDataType;
            this.conversationId = conversationId;
            this.conversationDataId = conversationDataId;
            this.chatterId = chatterId;
            this.dateTime = dateTime;
        }
    }
}
