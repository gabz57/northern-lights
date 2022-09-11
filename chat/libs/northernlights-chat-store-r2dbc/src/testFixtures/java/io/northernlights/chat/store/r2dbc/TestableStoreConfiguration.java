package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChattersRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataReadMarkerRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationsRepository;
import io.northernlights.chat.store.r2dbc.event.ChatEventRepository;
import io.northernlights.chat.store.r2dbc.ssekey.ChatterSseChatKeyRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestableStoreConfiguration {

    @Bean
    public StoreCleaner storeCleaner(
        ChattersRepository chattersRepository,
        ConversationsRepository conversationsRepository,
        ChatterConversationRepository chatterConversationRepository,
        ChatterSseChatKeyRepository chatterSseChatKeyRepository,
        ConversationDataReadMarkerRepository conversationDataReadMarkerRepository,
        ConversationDataRepository conversationDataRepository,
        ChatEventRepository chatEventRepository
    ) {
        return new StoreCleaner(
            chattersRepository,
            conversationsRepository,
            chatterConversationRepository,
            chatterSseChatKeyRepository,
            conversationDataReadMarkerRepository,
            conversationDataRepository,
            chatEventRepository
        );
    }
}
