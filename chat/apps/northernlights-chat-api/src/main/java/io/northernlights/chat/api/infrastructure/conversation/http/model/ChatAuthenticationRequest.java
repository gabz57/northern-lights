package io.northernlights.chat.api.infrastructure.conversation.http.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAuthenticationRequest {
    private Map<String, String> conversationStatuses;
}
