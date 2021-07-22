package io.northernlights.chat.api.domain.client.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatDataHello implements ChatData {
    String sseChatKey;

    @Override
    public ChatDataType getChatDataType() {
        return ChatDataType.HELLO;
    }
}
