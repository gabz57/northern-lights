package io.northernlights.chat.store.r2dbc.ssekey.model;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sse_chat_key")
public class SseChatKeyModel {
    @Id
    @Column("sse_key")
    private UUID sseChatKey;
    @Column("chatter_id")
    private UUID chatterId;
    @Column("conversation_status")
    private Json conversationStatus;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    public static class ConversationStatuses extends HashMap<String, String> {
        public static ConversationStatuses of(Map<ConversationId, ConversationDataId> conversationStatusMap) {
            ConversationStatuses conversationStatuses = new ConversationStatuses();
            conversationStatusMap.forEach((key, value) -> conversationStatuses.put(key.getId().toString(), value.getId()));
            return conversationStatuses;
        }
    }
}
