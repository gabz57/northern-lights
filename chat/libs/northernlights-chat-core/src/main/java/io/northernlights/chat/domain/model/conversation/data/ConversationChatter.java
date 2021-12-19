package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.CHATTER_ADD;

@Getter
public class ConversationChatter extends ConversationData.AbstractConversationData implements ConversationData {
    @NonNull
    private final ChatterId invitedChatterId;

    @Builder
    public ConversationChatter(
        ConversationId conversationId,
        ConversationDataId conversationDataId,
        ChatterId chatterId,
        OffsetDateTime dateTime,
        @NonNull ChatterId invitedChatterId) {
        super(CHATTER_ADD, conversationId, conversationDataId, chatterId, dateTime);
        this.invitedChatterId = invitedChatterId;
    }

}
