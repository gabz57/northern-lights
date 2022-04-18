package io.northernlights.chat.store.user;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.domain.model.user.NorthernLightsUser;
import reactor.core.publisher.Mono;

public interface UserStore {
    Mono<NorthernLightsUser> createWithExternalId(ChatterId chatterID, AuthOrigin origin, String uid);

    Mono<NorthernLightsUser> findByExternalId(AuthOrigin origin, String uid);
}
