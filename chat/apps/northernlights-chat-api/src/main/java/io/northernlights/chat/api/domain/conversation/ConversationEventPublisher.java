package io.northernlights.chat.api.domain.conversation;

import io.northernlights.chat.domain.event.ConversationEvent;
import reactor.core.publisher.Mono;

public interface ConversationEventPublisher {
    Mono<Void> publish(ConversationEvent conversationEvent);
}
