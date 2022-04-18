package io.northernlights.chat.api.application.user;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.store.user.UserStore;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.api.application.user.UserInfoQuery.*;

@RequiredArgsConstructor
public class UserInfoQuery implements UseCase<UserInfoQueryInput, UserInfoQueryResult>  {

    private final UserStore userStore;

    public Mono<UserInfoQueryResult> execute(UserInfoQueryInput request) {
        return userStore.findByExternalId(request.getOrigin(), request.getUid())
            .map(user -> new UserInfoQueryResult(user.getChatterId()));
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class UserInfoQueryInput {
        AuthOrigin origin;
        String uid;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class UserInfoQueryResult {
        ChatterId chatterId;
    }
}
