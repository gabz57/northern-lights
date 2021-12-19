package io.northernlights.chat.domain.model.ssekey;

import lombok.Value;

import java.util.UUID;

@Value
public class SseChatKey {
    UUID id;

    public static SseChatKey of(String id) {
        return new SseChatKey(UUID.fromString(id));
    }

    public static SseChatKey of(UUID uuid) {
        return new SseChatKey(uuid);
    }
}
