package io.northernlights.chat.api.infrastructure.client.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SseChatPayload {
    private SseChatConversation conversation;
    private SseChatChatter chatter;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SseChatConversation {
        private String id;
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
        private String id;
        private String name;
    }
}
