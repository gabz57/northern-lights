package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

import java.util.List;

@Getter
public class ConversationCreation extends ConversationData.AbstractConversationData implements ConversationData {
    private final List<ChatterId> participants;

    public ConversationCreation(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, List<ChatterId> participants) {
        super(conversationId, conversationDataId, chatterId);
        this.participants = participants;
    }
}
