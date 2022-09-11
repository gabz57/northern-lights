package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.domain.event.ChatEvent;
import reactor.core.publisher.Flux;

public interface ChatEventSource {
    Flux<ChatEvent> subscribe();
}
