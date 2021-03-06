//package io.northernlights.chat.domain.model.conversation.data;
//
//import io.northernlights.chat.domain.model.chatter.ChatterId;
//import io.northernlights.chat.domain.model.conversation.ConversationId;
//import lombok.Getter;
//
//import java.time.OffsetDateTime;
//
//import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.READ_MARKER;
//
//@Getter
//public class ConversationReadMarker extends ConversationData.AbstractConversationData implements ConversationData {
//    private final ConversationDataId markedConversationDataID;
//
//    public ConversationReadMarker(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId chatterId, ConversationDataId markedConversationDataID, OffsetDateTime dateTime) {
//        super(READ_MARKER, conversationId, conversationDataId, chatterId, dateTime);
//        this.markedConversationDataID = markedConversationDataID;
//    }
//}
