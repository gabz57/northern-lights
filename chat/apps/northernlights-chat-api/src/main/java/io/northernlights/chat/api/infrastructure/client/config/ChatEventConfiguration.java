package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.ChatEventSource;
import io.northernlights.chat.api.domain.conversation.ChatEventPublisher;
import io.northernlights.chat.api.infrastructure.LocalChatEventFlow;
import io.northernlights.chat.api.infrastructure.client.RedisChatEventSource;
import io.northernlights.chat.domain.event.ChatEventWrapper;
import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.events.redis.RedisChatEventConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Configuration
public class ChatEventConfiguration {

    @Profile("!local")
    @Configuration
    @Import(RedisChatEventConfiguration.class)
    public static class ExternalChatEventConfiguration {

        @Bean
        public ChatEventSource chatEventSubscriber(ReactiveRedisTemplate<String, ChatEventWrapper> reactiveChatEventRedisTemplate) {
            return new RedisChatEventSource(reactiveChatEventRedisTemplate);
        }

    }

    @Profile("local")
    @Configuration
    public static class LocalChatEventConfiguration {

        private static final LocalChatEventFlow localChatEventFlow = new LocalChatEventFlow();

        @Bean
        public ChatEventSource chatEventSubscriber() {
            return localChatEventFlow;
        }

        @Bean
        public ChatEventPublisher chatEventPublisher() {
            return localChatEventFlow;
        }

        @Primary
        @Bean
        public ChatEventStore localChatEventStore(ChatEventPublisher chatEventPublisher) {
            return chatEventPublisher::publish;
        }
    }
}
