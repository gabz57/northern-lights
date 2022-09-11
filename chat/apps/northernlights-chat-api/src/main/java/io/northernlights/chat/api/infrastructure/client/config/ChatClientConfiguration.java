package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.*;
import io.northernlights.chat.api.infrastructure.client.http.SseChatDataAdapter;
import io.northernlights.chat.api.infrastructure.conversation.store.ChatClientStoreImpl;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    @Bean
    public ChatEventDataAdapter chatDataAdapter() {
        return new ChatEventDataAdapter();
    }

    @Bean
    public SseChatDataAdapter sseChatDataAdapter() {
        return new SseChatDataAdapter();
    }

    @Bean
    public ChatDataProvider chatDataProvider(ChatEventSource chatEventSource, ChatEventDataAdapter chatEventDataAdapter) {
        return new ChatEventDataDispatcher(chatEventSource, chatEventDataAdapter);
    }

    @Bean
    public ChatClientStore chatClientStore(ConversationStore conversationStore, SseKeyStore sseKeyStore, ChatEventDataAdapter chatEventDataAdapter) {
        return new ChatClientStoreImpl(conversationStore, sseKeyStore, chatEventDataAdapter);
    }

    @Bean
    public ChatClientProvider chatClientProvider(ChatDataProvider chatDataProvider, ChatClientStore chatClientStore) {
        return new ChatClientProvider(chatDataProvider, chatClientStore);
    }
}
