package io.northernlights.chat.domain.event.store;

import io.northernlights.chat.domain.event.ChatEvent;
import reactor.core.publisher.Mono;

public interface ChatEventStore {

    Mono<Void> publish(ChatEvent event);
}
