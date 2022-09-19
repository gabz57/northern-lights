package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.domain.model.chatter.Chatter;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChatDataChatters implements ChatData {
    List<Chatter> chatters;

    public ChatDataType getChatDataType() {
        return ChatDataType.CHATTERS;
    }

}
