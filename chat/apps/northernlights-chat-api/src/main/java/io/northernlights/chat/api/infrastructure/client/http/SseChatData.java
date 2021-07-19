package io.northernlights.chat.api.infrastructure.client.http;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SseChatData {
    String id;
    String event;
    SseChatPayload payload;
}
