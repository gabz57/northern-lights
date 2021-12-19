package io.northernlights.chat.store.conversation;

import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ConversationStoreConfiguration.class)
@Configuration
public class ConversationStoreConfigurationInMemory {

    @Bean
    public ConversationStore conversationStore(ConversationIdGenerator conversationIdGenerator, ConversationDataIdGenerator conversationDataIdGenerator) {
        return new InMemoryConversationStore(conversationIdGenerator, conversationDataIdGenerator);
    }
}
