package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import lombok.Getter;

import java.time.OffsetDateTime;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.MESSAGE;

@Getter
public class ConversationMessage extends ConversationData.AbstractConversationData implements ConversationData {
    private final Message message;

    public ConversationMessage(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, Message message, OffsetDateTime dateTime) {
        super(MESSAGE, conversationId, conversationDataId, chatterId, dateTime);
        this.message = message;
    }

}
