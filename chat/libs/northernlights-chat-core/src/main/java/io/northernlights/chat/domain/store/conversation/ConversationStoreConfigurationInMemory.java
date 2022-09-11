package io.northernlights.chat.domain.store.conversation;

import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ConversationStoreConfiguration.class)
@Configuration
public class ConversationStoreConfigurationInMemory {

    @Bean
    public ConversationStore conversationStore(ConversationIdGenerator conversationIdGenerator) {
        return new InMemoryConversationStore(conversationIdGenerator);
    }
}
