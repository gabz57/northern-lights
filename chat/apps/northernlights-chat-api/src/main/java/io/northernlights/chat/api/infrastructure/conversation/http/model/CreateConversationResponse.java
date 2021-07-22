package io.northernlights.chat.api.infrastructure.conversation.http.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationResponse {
    private String conversationId;
}
