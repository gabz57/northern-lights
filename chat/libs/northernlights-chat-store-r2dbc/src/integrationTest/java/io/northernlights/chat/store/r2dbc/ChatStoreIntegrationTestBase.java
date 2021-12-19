package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChatterStoreConfigurationR2bdc;
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
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Import({
    R2dbcChatStoreConfiguration.class,
    ChatterStoreConfigurationR2bdc.class,
    ConversationStoreConfigurationR2dbc.class,
    SseKeyStoreConfigurationR2dbc.class
})
public class ChatStoreIntegrationTestBase extends StoreIntegrationTestBase {

    @Autowired
    private ChattersRepository chattersRepository;
    @Autowired
    private ChatterConversationRepository chatterConversationRepository;
    @Autowired
    private ChatterSseChatKeyRepository chatterSseChatKeyRepository;
    @Autowired
    private ConversationDataReadMarkerRepository conversationDataReadMarkerRepository;
    @Autowired
    private ConversationDataRepository conversationDataRepository;

    @BeforeEach
    void clearDatabaseBefore() {
        clearDatabase();
    }

    private void clearDatabase() {
        chatterSseChatKeyRepository.deleteAll()
            .thenMany(conversationDataReadMarkerRepository.deleteAll())
            .thenMany(conversationDataRepository.deleteAll())
            .thenMany(chatterConversationRepository.deleteAll())
            .thenMany(chattersRepository.deleteAll())
            .as(StepVerifier::create)
            .verifyComplete();
    }

    protected Mono<Chatter> createChatter(ChatterId conversationCreatorChatterId) {
        return chattersRepository.save(ChatterModel.of(new Chatter(conversationCreatorChatterId, conversationCreatorChatterId.getId().toString()), true))
            .map(ChatterModel::toChatter);
    }
}
