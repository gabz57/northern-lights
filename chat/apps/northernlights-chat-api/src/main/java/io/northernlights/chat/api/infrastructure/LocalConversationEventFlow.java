package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.api.domain.client.ConversationEventSubscriber;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.event.ConversationEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class LocalConversationEventFlow implements ConversationEventPublisher, ConversationEventSubscriber {

    private final Sinks.Many<ConversationEvent> conversationEventSink = Sinks.many().unicast().onBackpressureBuffer();

    public Mono<Void> publish(ConversationEvent conversationEvent) {
        conversationEventSink.tryEmitNext(conversationEvent);
        return Mono.empty();
    }

    public Flux<ConversationEvent> subscribe() {
        return conversationEventSink.asFlux();
    }
}
