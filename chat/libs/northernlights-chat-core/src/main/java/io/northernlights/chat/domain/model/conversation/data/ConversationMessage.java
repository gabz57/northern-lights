package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import lombok.Getter;

@Getter
public class ConversationMessage extends ConversationData.AbstractConversationData implements ConversationData {
    private final Message content;

    public ConversationMessage(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, Message content) {
        super(conversationId, conversationDataId, chatterId);
        this.content = content;
    }

}
