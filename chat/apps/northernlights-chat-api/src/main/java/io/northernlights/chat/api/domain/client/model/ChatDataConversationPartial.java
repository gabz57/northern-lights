package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class ChatDataConversationPartial implements ChatData {
    ConversationId conversationId;
    Conversation conversationData;
    List<Chatter> chatters;
    Map<ChatterId, ConversationDataId> readMarkers;

    public ChatDataType getChatDataType() {
        return ChatDataType.PARTIAL;
    }
}
