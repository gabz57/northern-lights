package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatDataUpdate implements ChatData {
    ConversationId conversationId;
    MarkedAsReadValue markedAsRead;
    MessageValue message;
//    ConversationCreationValue conversationCreation;

    public ChatDataType getChatDataType() {
        return ChatDataType.UPDATE;
    }

    @Value
    @Builder
    public static class MarkedAsReadValue {
        ConversationDataId conversationDataId;
        ChatterId by;
        ConversationDataId at;
    }

    @Value
    @Builder
    public static class MessageValue {
        ConversationDataId conversationDataId;
        ChatterId author;
        Message message;
    }
//
//    @Value
//    @Builder
//    public static class ConversationCreationValue {
//        String name;
//        ConversationDataId conversationDataId;
//        ChatterId createdBy;
//        List<ChatterId> chatters;
//    }
}
