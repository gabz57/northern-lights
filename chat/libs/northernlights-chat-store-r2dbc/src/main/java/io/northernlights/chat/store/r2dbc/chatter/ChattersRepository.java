package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.store.r2dbc.chatter.model.ChatterModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChattersRepository extends R2dbcRepository<ChatterModel, UUID> {
    @Override
    Mono<ChatterModel> findById(UUID uuid);

}
