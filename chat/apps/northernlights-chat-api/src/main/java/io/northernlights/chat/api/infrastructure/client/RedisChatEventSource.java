package io.northernlights.chat.api.infrastructure.client;

import io.northernlights.chat.api.domain.client.ChatEventSource;
import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.ChatEventWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class RedisChatEventSource implements ChatEventSource {
    private final ReactiveRedisTemplate<String, ChatEventWrapper> reactiveRedisTemplate;

    @Value("${chat.redis.workspace}")
    private String workspace;

    @Override
    public Flux<ChatEvent> subscribe() {
        log.info("Subscribing to redis");
        return this.reactiveRedisTemplate
            .listenTo(ChannelTopic.of("nl-chat-events:" + workspace))
            .map(ReactiveSubscription.Message::getMessage)
            .map(ChatEventWrapper::getEvent)
            .doOnSubscribe((e) -> log.info("Listening to Redis topic '{}'", "nl-chat-events:" + workspace));
    }
}
