package io.northernlights.chat.api.domain.client;


import org.reactivestreams.Publisher;

public interface ChatterEventProvider {
    Publisher<ChatData> chatterFlow(ChatClientID chatClientId);
}
