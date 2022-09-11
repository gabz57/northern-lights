package io.northernlights.chat.api.domain.conversation;

import io.northernlights.chat.domain.event.ChatEvent;
import reactor.core.publisher.Mono;

public interface ChatEventPublisher {
    Mono<Void> publish(ChatEvent chatEvent);
}
