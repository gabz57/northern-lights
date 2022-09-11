package io.northernlights.chat.store.r2dbc.event;

import io.northernlights.chat.store.r2dbc.event.model.ChatEventModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatEventRepository extends R2dbcRepository<ChatEventModel, UUID> {

}
