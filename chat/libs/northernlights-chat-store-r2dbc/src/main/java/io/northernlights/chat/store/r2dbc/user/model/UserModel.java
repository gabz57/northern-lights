package io.northernlights.chat.store.r2dbc.user.model;

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
import java.util.UUID;

/**
 * {@link db.changelog.features.northernlightsusers}.0001_create-table-users.xml
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserModel implements Persistable<UUID> {
    @Transient
    private boolean isNew;
    @Id
    private UUID id;
    @Column("external_origin")
    private String externalOrigin;
    @Column("external_uid")
    private String externalUid;
    @Column("chatter_id")
    private String chatterId;

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
}
