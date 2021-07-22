package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import lombok.Builder;
import lombok.Data;

@Data
public class ChatClientID {
    private final ChatterId chatterId;
    private final String device;

    public ChatClientID(ChatterId chatterId, String device) {
        this.chatterId = chatterId;
        this.device = device;
    }
}
