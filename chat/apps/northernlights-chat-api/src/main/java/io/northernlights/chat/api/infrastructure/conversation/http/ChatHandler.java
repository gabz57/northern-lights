package io.northernlights.chat.api.infrastructure.conversation.http;

import io.northernlights.chat.api.application.conversation.ChatCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
public class ChatHandler {
    private final ChatCommands chatCommands;
    //    @Operation(
//        summary = "Count custom products of a cooperative",
//        tags = {"Custom products"},
//        parameters = {
//            @Parameter(in = PATH, name = PARTNER_ID, description = "Cooperative ID")
//        },
//        responses = {
//            @ApiResponse(responseCode = "200", description = "successful operation",
//                content = @Content(schema = @Schema(implementation = Long.class)))
//        },
//        security = @SecurityRequirement(name = OAUTH_2_KEY, scopes = {PRODUCTS_READ_COOPERATIVE_VALUE})
//    )
    public Mono<ServerResponse> countChat(ServerRequest request) {

        return ok().bodyValue("");
//        return toCountChatInput(request)
//            .flatMap(this.chatQueryFactory.countChatQuery()::execute)
//            .map(toCountChatResponse())
//            .flatMap(response -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(response));
    }

    public Mono<ServerResponse> createConversation(ServerRequest serverRequest) {
        return null;
    }

    public Mono<ServerResponse> markAsRead(ServerRequest serverRequest) {
        return null;
    }

    public Mono<ServerResponse> sendMessage(ServerRequest serverRequest) {
        return null;
    }
}
