package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChatterStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.chatter.ChattersRepository;
import io.northernlights.chat.store.r2dbc.chatter.model.ChatterModel;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataReadMarkerRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.ssekey.ChatterSseChatKeyRepository;
import io.northernlights.chat.store.r2dbc.ssekey.SseKeyStoreConfigurationR2dbc;
import io.northernlights.store.r2dbc.StoreIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Import({
    R2dbcChatStoreConfiguration.class,
    ChatterStoreConfigurationR2dbc.class,
    ConversationStoreConfigurationR2dbc.class,
    SseKeyStoreConfigurationR2dbc.class
})
public class ChatStoreIntegrationTestBase extends StoreIntegrationTestBase {
    @Autowired
    private ChattersRepository chattersRepository;
    @Autowired
    private StoreCleaner storeCleaner;
    @Configuration
    static class Cleaner {

        @Bean
        public StoreCleaner storeCleaner(
            ChattersRepository chattersRepository,
            ChatterConversationRepository chatterConversationRepository,
            ChatterSseChatKeyRepository chatterSseChatKeyRepository,
            ConversationDataReadMarkerRepository conversationDataReadMarkerRepository,
            ConversationDataRepository conversationDataRepository
        ) {
            return new StoreCleaner(
                chattersRepository,
                chatterConversationRepository,
                chatterSseChatKeyRepository,
                conversationDataReadMarkerRepository,
                conversationDataRepository
            );
        }
    }

    @BeforeEach
    void clearDatabaseBefore() {
        storeCleaner.clearDatabase();
    }

    protected Mono<Chatter> createChatter(ChatterId conversationCreatorChatterId) {
        return chattersRepository.save(ChatterModel.of(new Chatter(conversationCreatorChatterId, conversationCreatorChatterId.getId().toString()), true))
            .map(ChatterModel::toChatter);
    }
}
