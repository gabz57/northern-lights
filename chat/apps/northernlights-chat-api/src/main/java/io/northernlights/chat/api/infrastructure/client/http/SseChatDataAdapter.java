package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.ChatData;

public class SseChatDataAdapter {

    public SseChatData mapToSseChatData(ChatData chatData) {
        return SseChatData.builder()
            // TODO: map ChatData to SseChatData
//            .id()
//            .event()
//            .payload()
            .build();
    }

}
