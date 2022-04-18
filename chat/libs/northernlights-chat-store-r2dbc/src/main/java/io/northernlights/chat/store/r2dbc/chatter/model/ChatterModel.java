package io.northernlights.chat.store.r2dbc.chatter.model;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link db.changelog.features.chatters}.0001_create-table-chatter.xml
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chatters")
public class ChatterModel implements Persistable<UUID> {
    @Transient
    private boolean isNew;
    @Id
    private UUID id;
    private String name;
    private String picture;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Nullable
    public UUID getId() {
        return id;
    }

    @Transient
    public boolean isNew() {
        return isNew;
    }

    public Chatter toChatter() {
        return new Chatter(ChatterId.of(id), name, picture);
    }
    public static ChatterModel of(Chatter chatter, boolean isNew) {
        return ChatterModel.builder()
            .name(chatter.getName())
            .picture(chatter.getPicture())
            .id(chatter.getChatterID().getId())
            .isNew(isNew)
            .build();
    }
}
