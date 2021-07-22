package io.northernlights.chat.api.infrastructure.conversation.http.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequest {
    private String conversationId;
    private String conversationDataId;
}
