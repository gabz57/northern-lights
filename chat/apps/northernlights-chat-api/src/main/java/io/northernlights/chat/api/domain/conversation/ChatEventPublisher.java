package io.northernlights.chat.api.domain.conversation;

import io.northernlights.chat.domain.event.ChatEvent;
import reactor.core.publisher.Mono;

/**
 * Useful to mock custom Debezium instance (which only forwards to Redis pub/sub), see {@link io.northernlights.chat.api.infrastructure.LocalChatEventFlow}
 */
public interface ChatEventPublisher {
    Mono<Void> publish(ChatEvent chatEvent);
}
