package io.northernlights.chat.api.infrastructure.conversation.http;

import io.northernlights.chat.api.application.conversation.ChatCommands;
import io.northernlights.chat.api.infrastructure.conversation.http.model.*;
import io.northernlights.security.NorthernLightsAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
public class ChatHandler {

    private final ChatCommands chatCommands;
    private final ChatApiAdapter chatApiAdapter;

    public Mono<ServerResponse> initializeSseChat(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .flatMap(authentication -> serverRequest.bodyToMono(InitializeSseChatRequest.class)
                .map(request -> chatApiAdapter.adapt(request, authentication))
                .flatMap(chatCommands.authenticate()::execute)
                .map(chatApiAdapter::adapt)
                .flatMap(withJsonResponseBody(ok(), InitializeSseChatResponse.class)));
    }

    public Mono<ServerResponse> createConversation(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .flatMap(authentication -> serverRequest.bodyToMono(CreateConversationRequest.class)
                .map(request -> chatApiAdapter.adapt(request, authentication))
                .flatMap(chatCommands.createConversation()::execute)
                .map(chatApiAdapter::adapt)
                .flatMap(withJsonResponseBody(ok(), CreateConversationResponse.class)));
    }

    public Mono<ServerResponse> sendMessage(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .flatMap(authentication -> serverRequest.bodyToMono(SendMessageRequest.class)
                .map(request -> chatApiAdapter.adapt(request, authentication))
                .flatMap(chatCommands.sendMessage()::execute)
                .map(chatApiAdapter::adapt)
                .flatMap(withJsonResponseBody(ok(), SendMessageResponse.class)));
    }

    public Mono<ServerResponse> markAsRead(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .flatMap(authentication -> serverRequest.bodyToMono(MarkAsReadRequest.class)
                .map(request -> chatApiAdapter.adapt(request, authentication))
                .flatMap(chatCommands.markAsRead()::execute)
                .map(chatApiAdapter::adapt)
                .flatMap(withJsonResponseBody(ok(), MarkAsReadResponse.class)));
    }

    public Mono<ServerResponse> inviteChatter(ServerRequest serverRequest) {
        return serverRequest.principal().cast(NorthernLightsAuthentication.class)
            .flatMap(authentication -> serverRequest.bodyToMono(InviteChatterRequest.class)
                .map(request -> chatApiAdapter.adapt(request, authentication))
                .flatMap(chatCommands.inviteChatter()::execute)
                .map(chatApiAdapter::adapt)
                .flatMap(withJsonResponseBody(ok(), InviteChatterResponse.class)));
    }

    private <T> Function<T, Mono<ServerResponse>> withJsonResponseBody(ServerResponse.BodyBuilder responseBuilder, Class<T> elementClass) {
        return response -> responseBuilder
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(response);
    }
}
