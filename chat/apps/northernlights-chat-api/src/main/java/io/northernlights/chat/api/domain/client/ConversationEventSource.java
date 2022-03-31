package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.domain.event.ConversationEvent;
import reactor.core.publisher.Flux;

public interface ConversationEventSource {

    Flux<ConversationEvent> subscribe();
}
