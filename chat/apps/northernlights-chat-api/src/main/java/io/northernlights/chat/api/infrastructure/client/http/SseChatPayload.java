package io.northernlights.chat.api.infrastructure.client.http;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class SseChatPayload {
    private final String id;

    private final String event;

    private final Duration retry;

    private final String comment;

    private final Object data;
}
