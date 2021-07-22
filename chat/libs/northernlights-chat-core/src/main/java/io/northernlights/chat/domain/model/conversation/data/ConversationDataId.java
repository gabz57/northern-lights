package io.northernlights.chat.domain.model.conversation.data;

import lombok.Value;

@Value
public class ConversationDataId implements Comparable<ConversationDataId> {
    String id;

    @Override
    public int compareTo(ConversationDataId o) {
        return id.compareTo(o.id);
    }
}
