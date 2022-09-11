package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.conversation.ConversationStoreConfiguration;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataAdapter;
import io.northernlights.commons.TimeService;
import io.northernlights.store.r2dbc.converter.R2dbcConverters;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.time.ZonedDateTime;

@Import(ConversationStoreConfiguration.class)
@Configuration
public class ConversationStoreConfigurationR2dbc {

    @ConditionalOnMissingBean
    @Bean
    public TimeService timeService() {
        return ZonedDateTime::now;
    }

    @Bean
    public ConversationDataAdapter conversationDataAdapter() {
        return new ConversationDataAdapter(R2dbcConverters.R2DBC_OBJECT_MAPPER);
    }

    @Bean
    public ConversationDataRepository conversationDataRepository(R2dbcEntityTemplate r2dbcEntityTemplate, ConversationDataAdapter conversationDataAdapter) {
        return new ConversationDataRepository(r2dbcEntityTemplate, conversationDataAdapter);
    }

    @Bean
    public ConversationDataReadMarkerRepository conversationDataReadMarkerRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        return new ConversationDataReadMarkerRepository(r2dbcEntityTemplate);
    }

    @Bean
    public ConversationStore conversationStore(
        ConversationIdGenerator conversationIdGenerator,
        ConversationsRepository conversationsRepository,
        ConversationDataRepository conversationDataRepository,
        ConversationDataAdapter conversationDataAdapter,
        ConversationDataReadMarkerRepository conversationDataReadMarkerRepository,
        ChatterConversationRepository chatterConversationRepository,
        ChatEventStore chatEventStore
    ) {
        return new R2dbcConversationStore(conversationIdGenerator,
            conversationsRepository,
            conversationDataRepository,
            conversationDataAdapter,
            conversationDataReadMarkerRepository,
            chatterConversationRepository,
            chatEventStore);
    }
}
