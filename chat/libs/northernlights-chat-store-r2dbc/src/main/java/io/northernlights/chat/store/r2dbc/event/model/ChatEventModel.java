package io.northernlights.chat.store.r2dbc.event.model;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * {@link db.changelog.features.outbox}.0001_create-table-chat_events_outbox.xml
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_outbox")
public class ChatEventModel implements Persistable<UUID> {
    @Transient
    private boolean isNew;

    @Id
    @Column("event_id")
    private UUID id;
    @CreatedDate
    @Column("event_time")
    private LocalDateTime eventTime;

    @Column("aggregate_id")
    private String aggregateId;
    @Column("aggregate_type")
    private String aggregateType;
    @Column("event_type")
    private String eventType;
    @Column("event")
    private Json payload;

    @Transient
    public boolean isNew() {
        return isNew;
    }

}
