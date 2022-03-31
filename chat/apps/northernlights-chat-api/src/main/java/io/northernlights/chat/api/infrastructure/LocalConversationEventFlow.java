package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.api.domain.client.ConversationEventSource;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.event.ConversationEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public class LocalConversationEventFlow implements ConversationEventPublisher, ConversationEventSource {

    private final Sinks.Many<ConversationEvent> conversationEventSink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<Void> publish(ConversationEvent conversationEvent) {
        log.info("EMIT:> {}", conversationEvent);
        conversationEventSink.tryEmitNext(conversationEvent);
        return Mono.empty();
    }

    public Flux<ConversationEvent> subscribe() {
        return conversationEventSink.asFlux()
            .doOnNext(n -> log.info("RECEIVED:> {}", n))
            .doOnTerminate(() -> log.info("subscribe:> TERMINATE"))
            .doOnCancel(() -> log.warn("subscribe:> CANCEL"))
            .doOnError(e -> log.error("subscribe:> ERROR"))
            .doOnComplete(() -> log.info("subscribe:> COMPLETE"))
            .doAfterTerminate(() -> log.info("subscribe:> AFTER TERMINATE"));
    }
}
