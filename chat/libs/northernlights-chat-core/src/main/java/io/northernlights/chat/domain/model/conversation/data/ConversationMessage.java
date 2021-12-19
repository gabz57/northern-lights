package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.MESSAGE;

@Getter
public class ConversationMessage extends ConversationData.AbstractConversationData implements ConversationData {
    @NonNull
    private final Message message;

    @Builder
    public ConversationMessage(
        ConversationId conversationId,
        ConversationDataId conversationDataId,
        ChatterId chatterId,
        OffsetDateTime dateTime,
        @NonNull Message message) {
        super(MESSAGE, conversationId, conversationDataId, chatterId, dateTime);
        this.message = message;
    }

}
