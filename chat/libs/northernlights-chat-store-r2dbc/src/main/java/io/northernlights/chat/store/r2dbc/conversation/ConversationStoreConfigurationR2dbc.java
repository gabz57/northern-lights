package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.conversation.ConversationStoreConfiguration;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataIdGenerator;
import io.northernlights.store.r2dbc.converter.R2dbcConverters;
import io.northernlights.commons.TimeService;
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
    public ConversationDataReadMarkerRepository conversationDataReadMarkerRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        return new ConversationDataReadMarkerRepository(r2dbcEntityTemplate);
    }

    @Bean
    public ConversationStore conversationStore(ConversationIdGenerator conversationIdGenerator,
                                               ConversationDataIdGenerator conversationDataIdGenerator,
                                               ConversationDataRepository conversationDataRepository,
                                               ConversationDataReadMarkerRepository conversationDataReadMarkerRepository
    ) {
        return new R2dbcConversationStore(R2dbcConverters.R2DBC_OBJECT_MAPPER, conversationIdGenerator, conversationDataIdGenerator, conversationDataRepository, conversationDataReadMarkerRepository);
    }
}
