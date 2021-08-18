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
        private String creator;
        private Long createdAt;
        private String from;
        private String to;
        private Boolean dialogue;
        private List<String> participants;
        private List<? extends SseChatConversationData> data;
        private Map<String, String> readMarkers;// ChatterId.id <> ConversationDataId.id
    }

    public interface SseChatConversationData {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseChatConversationMessageData implements SseChatConversationData {
        @Builder.Default
        private String type = "MESSAGE";
        private String id;
        private Long dateTime;
        private String from;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseChatConversationChatterData implements SseChatConversationData {
        @Builder.Default
        private String type = "CHATTER";
        private String id;
        private Long dateTime;
        private String from;
        private String chatterId;
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
