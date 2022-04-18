package io.northernlights.chat.api;

import io.northernlights.chat.store.r2dbc.StoreCleaner;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChattersRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataReadMarkerRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataRepository;
import io.northernlights.chat.store.r2dbc.ssekey.ChatterSseChatKeyRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestableStoreConfiguration {

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
