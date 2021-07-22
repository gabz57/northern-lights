package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.ChatClientProvider;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatterController {

    private static final long KEEP_ALIVE_DELAY_IN_SECONDS = 15;
    private static final Flux<ServerSentEvent<SseChatPayload>> KEEP_ALIVE_FLUX = Flux
        .interval(Duration.ofSeconds(KEEP_ALIVE_DELAY_IN_SECONDS))
        .map(delay -> ServerSentEvent.<SseChatPayload>builder().comment("keep alive").build());

    private final ChatClientProvider chatClientProvider;
    private final SseChatDataAdapter sseChatDataAdapter;

    /**
     * example to test:
     * <PRE>
     * curl -X GET -H 'Content-Type: text/event-stream' http://localhost:8081/v1/chat/api/sse --data-binary '{"msg":"Hello", "onBehalfOf":"me"}'
     * </PRE>
     */
    @ResponseBody
    @GetMapping(path = "/v1/chat/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SseChatPayload>> sseChatDataFlow(
//        @RequestHeader(name = "last-event-id", required = false) String lastEventId,
        @RequestHeader(name = "sse-chat-key") String sseChatKey
    ) {
        return chatClientProvider.authenticate(sseChatKey)
            .map(chatterId -> new ChatClientID(chatterId, "laptop"))
            .flatMapMany(chatClientId -> subscribeChatterFlowSince(chatClientId, sseChatKey)
                .flatMap(this::chatDataToServerSentEvent)
                .mergeWith(KEEP_ALIVE_FLUX)
                // when client leaves
                .doOnCancel(() -> chatClientProvider.stopClientAndRevokeKey(chatClientId, sseChatKey))
                // when container stops
                .doOnTerminate(() -> chatClientProvider.stopClient(chatClientId)));
//            .doOnNext(s -> log.info("SSE NEXT: {}", s));
//            .onErrorResume(e -> Mono.just(throwableToSse(e)));
    }

    private ServerSentEvent<SseChatPayload> throwableToSse(Throwable throwable) {
        return ServerSentEvent.<SseChatPayload>builder()
            .event("ERROR")
            .comment(throwable.getMessage())
            .build();
    }

    private Flux<ChatData> subscribeChatterFlowSince(ChatClientID chatClientID, String sseChatKey) {
        return chatClientProvider.getOrCreateClient(chatClientID)
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

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public String asyncTimeout(AsyncRequestTimeoutException e) {
        return null; // "SSE timeout..OK";
    }

}
