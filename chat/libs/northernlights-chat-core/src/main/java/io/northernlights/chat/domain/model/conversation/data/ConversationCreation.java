package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

import java.util.List;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.CREATION;

@Getter
public class ConversationCreation extends ConversationData.AbstractConversationData implements ConversationData {
    private final String name;
    private final List<ChatterId> participants;

    public ConversationCreation(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, String name, List<ChatterId> participants) {
        super(CREATION, conversationId, conversationDataId, chatterId);
        this.name = name;
        this.participants = participants;
    }
}
