package io.northernlights.chat.event.publisher;

import io.northernlights.chat.domain.event.ChatEventWrapper;
import io.northernlights.chat.domain.event.conversation.ConversationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisChatEventPublisher {
    private final RedisTemplate<String, ChatEventWrapper> chatEventRedisTemplate;

    void publish(ChatEventWrapper chatEvent, String workspace) {
        log.info("Sending <" + chatEvent + ">");

        chatEventRedisTemplate.convertAndSend("nl-chat-events:" + workspace, chatEvent);
    }

}
