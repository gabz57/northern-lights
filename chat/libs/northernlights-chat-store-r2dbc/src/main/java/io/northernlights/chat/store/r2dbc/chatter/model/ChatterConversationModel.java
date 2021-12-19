package io.northernlights.chat.store.r2dbc.chatter.model;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
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
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link db.changelog.features.chatters}.0002_create-table-chatters-conversations.xml
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chatter_conversations")
public class ChatterConversationModel {
    @Id
    @Column("id")
    private UUID id;
    @Column("chatter_id")
    private UUID chatterId;
    @Column("conversation_id")
    private UUID conversationId;
    @Column("is_creator")
    private Boolean creator;
    @Column("joined_at")
    private LocalDateTime joinedAt;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static ChatterConversationModel of(ChatterId chatterId, boolean isCreator, ConversationId conversationId, OffsetDateTime dateTime) {
        return ChatterConversationModel.builder()
            .conversationId(conversationId.getId())
            .chatterId(chatterId.getId())
            .creator(isCreator)
            .joinedAt(dateTime.toLocalDateTime())
            .build();
    }
}
