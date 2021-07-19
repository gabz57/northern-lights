package io.northernlights.chat.api.domain.client;

import reactor.core.publisher.Flux;

public interface ChatterDataStore {
    Flux<ChatData> loadPreviousData(ChatClientID chatClientId, String lastChatterEventId);
}
