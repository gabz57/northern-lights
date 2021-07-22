package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatClient;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChatClientProvider {

    private final Map<ChatClientID, ChatClient> chatClientsById = new ConcurrentHashMap<>();
    private final ChatDataProvider chatDataProvider;
    private final ChatClientStore chatClientStore;

    public ChatClientProvider(ChatDataProvider chatDataProvider, ChatClientStore chatClientStore) {
        this.chatDataProvider = chatDataProvider;
        this.chatClientStore = chatClientStore;
    }

    public ChatClient getOrCreateClient(ChatClientID chatClientID) {
        return chatClientsById.computeIfAbsent(
            chatClientID,
            this::createClient);
    }

    public void stopClient(ChatClientID chatClientID, String sseChatKey) {
        log.info("Stopping client {}", chatClientID);
        chatClientsById.remove(chatClientID)
            .stop();
        chatClientStore.revoke(sseChatKey);
    }

    private ChatClient createClient(ChatClientID chatClientID) {
        return new ChatClient(chatClientID, chatDataProvider, chatClientStore);
    }

    public Mono<ChatterId> authenticate(String sseChatKey) {
        return chatClientStore.authenticate(sseChatKey);
    }
}
