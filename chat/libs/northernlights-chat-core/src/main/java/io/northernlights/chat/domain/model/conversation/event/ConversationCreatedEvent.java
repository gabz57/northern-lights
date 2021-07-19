package io.northernlights.chat.domain.model.conversation.event;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.Getter;

import java.util.List;

@Getter
public class ConversationCreatedEvent extends AbstractConversationEvent implements ConversationEvent {
    private final ChatterId createdBy;
    private final List<ChatterId> participants;

    public ConversationCreatedEvent(ConversationId conversationId, ConversationDataId conversationDataId, ChatterId createdBy, List<ChatterId> participants) {
        super(conversationId, conversationDataId);
        this.createdBy = createdBy;
        this.participants = participants;
    }
}
