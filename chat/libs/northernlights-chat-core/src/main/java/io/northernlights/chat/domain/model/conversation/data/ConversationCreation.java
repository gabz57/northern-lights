package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.List;

import static io.northernlights.chat.domain.model.conversation.data.ConversationData.ConversationDataType.CREATION;

@Getter
public class ConversationCreation extends ConversationData.AbstractConversationData implements ConversationData {
    @NonNull
    private final String name;
    @NonNull
    private final List<ChatterId> participants;
    @NonNull
    private final Boolean dialogue;

    @Builder
    public ConversationCreation(
        ConversationDataId conversationDataId,
        ChatterId chatterId,
        @NonNull String name,
        @NonNull List<ChatterId> participants,
        @NonNull OffsetDateTime dateTime,
        @NonNull Boolean dialogue) {
        super(CREATION, conversationDataId, chatterId, dateTime);
        this.name = name;
        this.participants = participants;
        this.dialogue = dialogue;
    }
}
