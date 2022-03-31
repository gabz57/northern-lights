package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.*;
import io.northernlights.chat.api.infrastructure.LocalConversationEventFlow;
import io.northernlights.chat.api.infrastructure.client.http.SseChatDataAdapter;
import io.northernlights.chat.api.infrastructure.conversation.store.ChatClientStoreImpl;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.ssekey.SseKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    @Bean
    public ChatDataAdapter chatDataAdapter(ChatterStore chatterStore) {
        return new ChatDataAdapter(chatterStore);
    }

    @Bean
    public SseChatDataAdapter sseChatDataAdapter() {
        return new SseChatDataAdapter();
    }

    @Bean
    public ChatDataProvider chatDataProvider(ConversationEventSource conversationEventSource, ChatDataAdapter chatDataAdapter) {
        return new ChatDataDispatcher(conversationEventSource, chatDataAdapter);
    }

    @Bean
    public ChatClientStore chatClientStore(ChatterStore chatterStore, ConversationStore conversationStore, SseKeyStore sseKeyStore, ChatDataAdapter chatDataAdapter) {
        return new ChatClientStoreImpl(chatterStore, conversationStore, sseKeyStore, chatDataAdapter);
    }

    @Bean
    public ChatClientProvider chatClientProvider(ChatDataProvider chatDataProvider, ChatClientStore chatClientStore) {
        return new ChatClientProvider(chatDataProvider, chatClientStore);
    }
}
