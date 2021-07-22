package io.northernlights.chat.api.infrastructure.client.http;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseChatPayload {
    private String id;
    private SseChatConversation conversation;
    private SseChatChatter chatter;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseChatConversation {
        private String name;
        private String from;
        private String to;
        private List<String> participants;
        private List<SseChatConversationData> data;
        // ChatterId.id <> ConversationDataId.id
        private Map<String, String> markedAsRead;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseChatConversationData {
        private String id;
        private String author;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseChatChatter {
        private String name;
    }
}
