package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.*;
import io.northernlights.chat.api.infrastructure.LocalConversationEventFlow;
import io.northernlights.chat.api.infrastructure.client.http.SseChatDataAdapter;
import io.northernlights.chat.api.infrastructure.conversation.store.ChatClientStoreImpl;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    @Bean
    public ConversationEventSubscriber conversationEventSubscriber(LocalConversationEventFlow localConversationEventFlow) {
        return localConversationEventFlow;
    }

    @Bean
    public ChatDataAdapter chatDataAdapter(ChatterStore chatterStore) {
        return new ChatDataAdapter(chatterStore);
    }

    @Bean
    public SseChatDataAdapter sseChatDataAdapter() {
        return new SseChatDataAdapter();
    }

    @Bean
    public ChatDataProvider chatDataProvider(ConversationEventSubscriber conversationEventSubscriber, ChatDataAdapter chatDataAdapter) {
        return new ChatDataDispatcher(conversationEventSubscriber, chatDataAdapter);
    }

    @Bean
    public ChatClientStore chatClientStore(ChatterStore chatterStore, ConversationStore conversationStore, ChatDataAdapter chatDataAdapter) {
        return new ChatClientStoreImpl(chatterStore, conversationStore, chatDataAdapter);
    }

    @Bean
    public ChatClientProvider chatClientProvider(ChatDataProvider chatDataProvider, ChatClientStore chatClientStore) {
        return new ChatClientProvider(chatDataProvider, chatClientStore);
    }
}
