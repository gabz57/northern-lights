package io.northernlights.chat.domain.model.conversation.readmarker;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Data;

@Data
public class ConversationReadMarker {
    private final ConversationId conversationId;
    private final ChatterId chatterId;
    private final ConversationDataId conversationDataId;

    public static ConversationReadMarker of(ConversationId conversationId, ChatterId chatterId, ConversationDataId conversationDataId) {
        return new ConversationReadMarker(conversationId, chatterId, conversationDataId);
    }
}
