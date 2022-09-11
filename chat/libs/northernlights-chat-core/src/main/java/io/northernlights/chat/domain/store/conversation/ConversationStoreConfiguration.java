package io.northernlights.chat.domain.store.conversation;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ConversationStoreConfiguration {

    @Bean
    public ConversationIdGenerator conversationIdGenerator() {
        return () -> new ConversationId(UUID.randomUUID());
    }

}
