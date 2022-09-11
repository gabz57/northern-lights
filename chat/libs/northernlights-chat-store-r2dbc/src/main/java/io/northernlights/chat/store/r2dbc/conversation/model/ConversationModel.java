package io.northernlights.chat.store.r2dbc.conversation.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("conversations")
public class ConversationModel implements Persistable<UUID> {
    @Transient
    private boolean isNew;

    @Id
    @Column("id")
    private UUID id;
    private String name;
    @Column("is_public")
    private Boolean isPublic;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public boolean isNew() {
        return isNew;
    }

}
