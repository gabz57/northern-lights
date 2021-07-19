package io.northernlights.chat.api.domain.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientProvider {

    private final Map<ChatClientID, ChatClient> chatClientsById = new ConcurrentHashMap<>();
    private final ChatterEventProvider chatterEventProvider;
    private final ChatterDataStore chatterDataStore;

    public ChatClientProvider(ChatterEventProvider chatterEventProvider, ChatterDataStore chatterDataStore) {
        this.chatterEventProvider = chatterEventProvider;
        this.chatterDataStore = chatterDataStore;
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

    private ChatClient createClient(ChatClientID chatClientID) {
        return new ChatClient(chatClientID, chatterEventProvider, chatterDataStore);
    }

}
