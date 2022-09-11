package io.northernlights.chat.domain.application.user;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.user.UserStore;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.northernlights.chat.domain.application.user.SubscribeUserCommand.*;

@RequiredArgsConstructor
public class SubscribeUserCommand implements UseCase<SubscribeUserCommandInput, SubscribeUserCommandResult> {

    private final UserStore userStore;
    private final ChatterStore chatterStore;

    // Currently chatter and user are bound at creation time:
    // a user: an entry for an authenticable account provider (ie: google) and its uid -> pointing to chatterId
    // a chatter: identity for chat application (chatter.id)
    public Mono<SubscribeUserCommandResult> execute(SubscribeUserCommandInput request) {
        return chatterStore.insertChatter(new Chatter(ChatterId.of(UUID.randomUUID()), request.getName(), request.getPicture()))
            .flatMap(chatter -> userStore.createWithExternalId(chatter.getChatterId(), request.getOrigin(), request.getUid())
            .map(user -> new SubscribeUserCommandResult(user.getChatterId())));
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class SubscribeUserCommandInput {
        AuthOrigin origin;
        String uid;
        String name;
        String picture;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class SubscribeUserCommandResult {
        ChatterId chatterId;
    }
}
