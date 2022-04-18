package io.northernlights.chat.store.r2dbc.user;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.domain.model.user.NorthernLightsUser;
import io.northernlights.chat.store.r2dbc.user.model.UserModel;
import io.northernlights.chat.store.user.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class R2dbcUserStore implements UserStore {

    private final UserRepository userRepository;

    @Transactional
    public Mono<NorthernLightsUser> createWithExternalId(ChatterId chatterID, AuthOrigin origin, String uid) {
        return userRepository.save(UserModel.builder()
                .externalOrigin(origin.getOriginKey())
                .externalUid(uid)
                .chatterId(chatterID.getId().toString())
                .isNew(true)
                .build())
            .map(this::toNorthernLightsUser);
    }

    @Transactional(readOnly = true)
    public Mono<NorthernLightsUser> findByExternalId(AuthOrigin origin, String uid) {
        return userRepository.findByExternalOriginAndExternalUid(origin.getOriginKey(), uid)
            .map(this::toNorthernLightsUser);
    }

    private NorthernLightsUser toNorthernLightsUser(UserModel user) {
        return NorthernLightsUser.builder()
            .chatterId(ChatterId.of(user.getChatterId()))
            .externalOrigin(AuthOrigin.valueFromKey(user.getExternalOrigin()))
            .externalUid(user.getExternalUid())
            .build();
    }
}
