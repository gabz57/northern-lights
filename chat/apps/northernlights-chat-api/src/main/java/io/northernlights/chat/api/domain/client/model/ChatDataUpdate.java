package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class ChatDataUpdate implements ChatData {
    @NonNull
    ConversationId conversationId;

    MarkedAsReadValue markedAsRead;
    MessageValue message;
    ChatterAddValue chatterAdd;
//    ConversationCreationValue conversationCreation;

    public ChatDataType getChatDataType() {
        return ChatDataType.LIVE_UPDATE;
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
        ChatterId from;
        Message message;
        OffsetDateTime dateTime;
    }

    @Value
    @Builder
    public static class ChatterAddValue {
        ConversationDataId conversationDataId;
        ChatterId from;
        ChatterId chatterId;
        OffsetDateTime dateTime;
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
