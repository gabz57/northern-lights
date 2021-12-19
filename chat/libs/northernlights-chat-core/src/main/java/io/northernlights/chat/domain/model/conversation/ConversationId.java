package io.northernlights.chat.domain.model.conversation;

import lombok.Value;

import java.util.UUID;

@Value
public class ConversationId {
    UUID id;

    public static ConversationId of(String id) {
        return new ConversationId(UUID.fromString(id));
    }

    public static ConversationId of(UUID uuid) {
        return new ConversationId(uuid);
    }
}
