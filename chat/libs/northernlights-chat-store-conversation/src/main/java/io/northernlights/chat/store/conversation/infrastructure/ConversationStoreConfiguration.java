package io.northernlights.chat.store.conversation.infrastructure;

import io.northernlights.chat.store.conversation.domain.ConversationStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationStoreConfiguration {
    @Bean
    public ConversationStore conversationStore() {
        return new InMemoryConversationStore();
    }
}
