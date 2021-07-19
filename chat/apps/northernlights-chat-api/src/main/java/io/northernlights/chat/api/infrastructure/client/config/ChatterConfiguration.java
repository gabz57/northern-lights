package io.northernlights.chat.api.infrastructure.client.config;

import io.northernlights.chat.api.domain.client.ChatClientProvider;
import io.northernlights.chat.api.domain.client.ChatterDataStore;
import io.northernlights.chat.api.domain.client.ChatterEventProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatterConfiguration {

    @Bean
    public ChatClientProvider sseChatterContainer(ChatterEventProvider chatterEventProvider, ChatterDataStore chatterDataStore) {
        return new ChatClientProvider(chatterEventProvider, chatterDataStore);
    }
}
