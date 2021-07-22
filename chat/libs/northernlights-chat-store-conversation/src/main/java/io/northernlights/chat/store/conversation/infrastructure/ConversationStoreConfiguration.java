package io.northernlights.chat.store.conversation.infrastructure;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataIdGenerator;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import io.northernlights.commons.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ConversationStoreConfiguration {

    @Bean
    public ConversationIdGenerator conversationIdGenerator() {
        return () -> new ConversationId(UUID.randomUUID().toString());
    }

    @Bean
    public ConversationDataIdGenerator conversationDataIdGenerator(TimeService timeService) {
        return () -> new ConversationDataId(timeService.now().toInstant().toEpochMilli() + "-" + UUID.randomUUID());
    }

    @Bean
    public ConversationStore conversationStore(ConversationIdGenerator conversationIdGenerator, ConversationDataIdGenerator conversationDataIdGenerator) {
        return new InMemoryConversationStore(conversationIdGenerator, conversationDataIdGenerator);
    }
}
