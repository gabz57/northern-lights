package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.api.domain.client.ChatEventSource;
import io.northernlights.chat.api.domain.conversation.ChatEventPublisher;
import io.northernlights.chat.domain.event.ChatEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public class LocalChatEventFlow implements ChatEventPublisher, ChatEventSource {

    private final Sinks.Many<ChatEvent> chatEventSink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<Void> publish(ChatEvent conversationEvent) {
        log.info("EMIT:> {}", conversationEvent);
        chatEventSink.tryEmitNext(conversationEvent);
        return Mono.empty();
    }

    public Flux<ChatEvent> subscribe() {
        return chatEventSink.asFlux()
            .doOnNext(n -> log.info("RECEIVED:> {}", n))
            .doOnTerminate(() -> log.info("subscribe:> TERMINATE"))
            .doOnCancel(() -> log.warn("subscribe:> CANCEL"))
            .doOnError(e -> log.error("subscribe:> ERROR"))
            .doOnComplete(() -> log.info("subscribe:> COMPLETE"))
            .doAfterTerminate(() -> log.info("subscribe:> AFTER TERMINATE"));
    }
}
