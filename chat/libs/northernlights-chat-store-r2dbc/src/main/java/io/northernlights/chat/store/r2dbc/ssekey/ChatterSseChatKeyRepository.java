package io.northernlights.chat.store.r2dbc.ssekey;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.r2dbc.ssekey.model.SseChatKeyModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChatterSseChatKeyRepository extends R2dbcRepository<SseChatKeyModel, UUID> {

}
