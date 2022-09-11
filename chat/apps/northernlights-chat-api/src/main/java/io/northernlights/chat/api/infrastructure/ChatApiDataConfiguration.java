package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.store.r2dbc.R2dbcChatStoreConfiguration;
import io.northernlights.chat.store.r2dbc.chatter.ChatterStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.conversation.ConversationStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.event.ChatEventStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.ssekey.SseKeyStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.user.UserStoreConfigurationR2dbc;
import io.northernlights.commons.TimeConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Import({
    TimeConfiguration.class,
    R2dbcAuditAwareConfiguration.class,
    R2dbcChatStoreConfiguration.class,
    UserStoreConfigurationR2dbc.class,
    ChatterStoreConfigurationR2dbc.class,
    ConversationStoreConfigurationR2dbc.class,
    SseKeyStoreConfigurationR2dbc.class,
    ChatEventStoreConfigurationR2dbc.class
})
@Configuration
public class ChatApiDataConfiguration {

    @Profile("local & !test")
    @Bean
    public LocalStartupListener localStartupListener(ChatterStore chatterStore, ConversationStore conversationStore) {
        return new LocalStartupListener(chatterStore, conversationStore);
    }

}
