package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatClient;
import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public void stopClient(ChatClientID chatClientID) {
        chatClientsById.remove(chatClientID)
            .stop();
    }

    public void stopClientAndRevokeKey(ChatClientID chatClientID, String sseChatKey) {
        stopClient(chatClientID);
        chatClientStore.revoke(sseChatKey);
    }

    private ChatClient createClient(ChatClientID chatClientID) {
        return new ChatClient(chatClientID, chatDataProvider, chatClientStore);
    }

    public Mono<ChatterId> authenticate(String sseChatKey) {
        return chatClientStore.authenticate(sseChatKey);
    }
}
