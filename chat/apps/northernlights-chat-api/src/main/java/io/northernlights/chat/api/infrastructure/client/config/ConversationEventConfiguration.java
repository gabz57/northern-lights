package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.ConversationEventSource;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.api.infrastructure.LocalConversationEventFlow;
import org.springframework.context.annotation.*;

@Configuration
public class ConversationEventConfiguration {

    @Configuration
    @Profile("!local")
    public static class ExternalConversationEventConfiguration {

    }

    @Configuration
    @Profile("local")
    public static class LocalConversationEventConfiguration {

        private static final LocalConversationEventFlow localConversationEventFlow = new LocalConversationEventFlow();

        @Bean
        public ConversationEventSource conversationEventSubscriber() {
            return localConversationEventFlow;
        }

        @Bean
        public ConversationEventPublisher conversationEventPublisher() {
            return localConversationEventFlow;
        }
    }
}
