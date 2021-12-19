package io.northernlights.chat.domain.model.chatter;

import lombok.Value;

import java.util.UUID;

@Value
public class ChatterId {
    UUID id;

    private ChatterId(UUID id) {
        this.id = id;
    }

    public static ChatterId of(String id) {
        return new ChatterId(UUID.fromString(id));
    }

    public static ChatterId of(UUID uuid) {
        return new ChatterId(uuid);
    }
}
