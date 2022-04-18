package io.northernlights.chat.api.infrastructure.user.http;

import io.northernlights.chat.api.application.user.UserCommands;
import io.northernlights.chat.api.infrastructure.user.http.model.SubscribeUserResponse;
import io.northernlights.chat.api.infrastructure.user.http.model.UserInfoResponse;
import io.northernlights.security.NorthernLightsAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
public class UserHandler {

    private final UserCommands userCommands;
    private final UserApiAdapter userApiAdapter;

    public Mono<ServerResponse> subscribeUser(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .map(userApiAdapter::adaptSubscribeUser)
            .flatMap(userCommands.subscribeUser()::execute)
            .map(userApiAdapter::adapt)
            .flatMap(withJsonResponseBody(ok(), SubscribeUserResponse.class));
    }

    public Mono<ServerResponse> userInfo(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .map(userApiAdapter::adaptUserInfo)
            .flatMap(userCommands.userInfo()::execute)
            .map(userApiAdapter::adapt)
            .flatMap(withJsonResponseBody(ok(), UserInfoResponse.class));
    }

    private <T> Function<T, Mono<ServerResponse>> withJsonResponseBody(ServerResponse.BodyBuilder responseBuilder, Class<T> elementClass) {
        return response -> responseBuilder
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(response);
    }
}
