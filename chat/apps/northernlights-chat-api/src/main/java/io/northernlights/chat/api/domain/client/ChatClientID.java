package io.northernlights.chat.api.domain.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatClientID {
    private final String name;

    public ChatClientID(String name) {
        this.name = name;
    }
}
