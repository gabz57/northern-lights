package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.store.r2dbc.chatter.model.ChatterConversationModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ChatterConversationRepository extends R2dbcRepository<ChatterConversationModel, UUID> {
    Flux<ChatterConversationModel> findAllByChatterId(UUID chatterId);
}
