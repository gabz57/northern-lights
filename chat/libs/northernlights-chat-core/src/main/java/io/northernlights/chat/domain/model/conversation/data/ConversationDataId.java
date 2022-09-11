package io.northernlights.chat.domain.model.conversation.data;

import lombok.Value;

@Value
public class ConversationDataId implements Comparable<ConversationDataId> {
    String id;

    public static ConversationDataId of(Long id) {
        return new ConversationDataId(id.toString());
    }
    public static ConversationDataId of(String id) {
        return new ConversationDataId(id);
    }

    @Override
    public int compareTo(ConversationDataId o) {
        return id.compareTo(o.id);
    }
}
