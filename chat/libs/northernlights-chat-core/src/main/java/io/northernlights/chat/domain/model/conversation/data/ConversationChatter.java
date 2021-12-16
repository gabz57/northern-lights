package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Getter;

import java.time.OffsetDateTime;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.CHATTER_ADD;

@Getter
public class ConversationChatter extends ConversationData.AbstractConversationData implements ConversationData {
    private final ChatterId invitedByChatterId;
    private final ChatterId chatterId;

    public ConversationChatter(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId invitedByChatterId, ChatterId chatterId, OffsetDateTime dateTime) {
        super(CHATTER_ADD, conversationId, conversationDataId, chatterId, dateTime);
        this.invitedByChatterId = invitedByChatterId;
        this.chatterId = chatterId;
    }

}
