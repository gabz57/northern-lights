package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.ChatClientProvider;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofSeconds;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatterController {

    private static final long KEEP_ALIVE_DELAY_IN_SECONDS = 15;
    private static final Flux<ServerSentEvent<SseChatPayload>> KEEP_ALIVE_FLUX = Flux
        .interval(ofSeconds(KEEP_ALIVE_DELAY_IN_SECONDS))
        .map(delay -> ServerSentEvent.<SseChatPayload>builder().comment("keep alive").build());

    private final ChatClientProvider chatClientProvider;
    private final SseChatDataAdapter sseChatDataAdapter;

    /**
     * example to test:
     * <PRE>
     * curl -X GET -H 'Content-Type: text/event-stream' -H 'sse-chat-key: 927dc5d2-fa27-4929-82e0-e64f863f7b18' http://localhost:8080/v1/chat/api/sse
     * </PRE>
     */
    @ResponseBody
    @GetMapping(path = "/v1/chat/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SseChatPayload>> sseChatDataFlow(
        @RequestHeader(name = "sse-chat-key") String sseChatKeyParameter,
        ServerHttpResponse response
    ) {
        response.getHeaders().add(HttpHeaders.CONNECTION, "keep-alive");
        SseChatKey sseChatKey = SseChatKey.of(sseChatKeyParameter);
        return chatClientProvider.authenticate(sseChatKey)
            .map(chatterId -> new ChatClientID(chatterId, "laptop"))
            .flatMapMany(chatClientId -> subscribeChatterFlowSince(chatClientId, sseChatKey)
                .flatMap(this::chatDataToServerSentEvent)
                .mergeWith(KEEP_ALIVE_FLUX)
                .doOnCancel(() -> chatClientProvider.stopClient(chatClientId, sseChatKey)));
//            .onErrorResume(e -> Mono.just(throwableToSse(e)));
    }

//    private ServerSentEvent<SseChatPayload> throwableToSse(Throwable throwable) {
//        return ServerSentEvent.<SseChatPayload>builder()
//            .event("ERROR")
//            .comment(throwable.getMessage())
//            .build();
//    }

    private Flux<ChatData> subscribeChatterFlowSince(ChatClientID chatClientID, SseChatKey sseChatKey) {
        return chatClientProvider.getOrCreateClient(sseChatKey, chatClientID)
            .connect(sseChatKey);
    }

    private Flux<ServerSentEvent<SseChatPayload>> chatDataToServerSentEvent(ChatData chatData) {
        return sseChatDataAdapter.mapToSseChatData(chatData)
            .map(sseChatData -> ServerSentEvent.<SseChatPayload>builder()
                .id(sseChatData.getId())
                .event(sseChatData.getEvent())
                .data(sseChatData.getPayload())
                .build());
    }

//    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
//    public String asyncTimeout(AsyncRequestTimeoutException e) {
//        return null; // "SSE timeout..OK";
//    }
}
