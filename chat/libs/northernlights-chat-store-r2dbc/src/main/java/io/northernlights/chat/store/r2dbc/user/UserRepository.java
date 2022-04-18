package io.northernlights.chat.store.r2dbc.user;

import io.northernlights.chat.store.r2dbc.user.model.UserModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<UserModel, UUID> {

    Mono<UserModel> findByExternalOriginAndExternalUid(String originKey, String uid);
}
