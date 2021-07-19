package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

@Getter
public class ConversationReadMarker extends ConversationData.AbstractConversationData implements ConversationData {
    private final ConversationDataId markedConversationDataID;

    public ConversationReadMarker(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, ConversationDataId markedConversationDataID) {
        super(conversationId, conversationDataId, chatterId);
        this.markedConversationDataID = markedConversationDataID;
    }
}
