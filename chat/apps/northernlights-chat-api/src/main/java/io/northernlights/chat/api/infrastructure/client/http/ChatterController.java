package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.ChatClientID;
import io.northernlights.chat.api.domain.client.ChatClientProvider;
import io.northernlights.chat.api.domain.client.ChatData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class ChatterController {

    private static final long KEEP_ALIVE_DELAY_IN_SECONDS = 15;
    private static final Flux<ServerSentEvent<SseChatPayload>> KEEP_ALIVE_FLUX = Flux.interval(Duration.ofSeconds(KEEP_ALIVE_DELAY_IN_SECONDS))
        .map(delay -> ServerSentEvent.<SseChatPayload>builder().comment("keep alive").build());

    private final ChatClientProvider sseContainer;
    private final SseChatDataAdapter sseChatDataAdapter;

    /**
     * example to test:
     * <PRE>
     * curl -X GET -H 'Content-Type: text/event-stream' http://localhost:8081/sse --data-binary '{"msg":"Hello", "onBehalfOf":"me"}'
     * </PRE>
     */
    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SseChatPayload>> subscribeSseApplicationEventFlow(
        Principal principal,
        @RequestHeader(name = "last-event-id", required = false) String lastEventId
    ) {
        final ChatClientID chatClientID = new ChatClientID(principal.getName());
        return subscribeChatterFlowSince(chatClientID, lastEventId)
            .map(this::chatDataToServerSentEvent)
            .mergeWith(KEEP_ALIVE_FLUX)
            // when client leaves
            .doOnCancel(() -> sseContainer.stopClient(chatClientID))
            // when container stops
            .doOnTerminate(() -> sseContainer.stopClient(chatClientID));
    }

    private Flux<ChatData> subscribeChatterFlowSince(ChatClientID chatClientID, String lastEventId) {
        return sseContainer.getOrCreateClient(chatClientID)
            .connect(lastEventId);
    }

    private ServerSentEvent<SseChatPayload> chatDataToServerSentEvent(ChatData chatData) {
        final SseChatData sseChatData = sseChatDataAdapter.mapToSseChatData(chatData);
        return ServerSentEvent.<SseChatPayload>builder()
            .id(sseChatData.getId())
            .event(sseChatData.getEvent())
            .data(sseChatData.getPayload())
            .build();
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public String asyncTimeout(AsyncRequestTimeoutException e) {
        return null; // "SSE timeout..OK";
    }

}
