package io.northernlights.chat.store.ssekey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseKeyStoreConfigurationInMemory {

    @Bean
    public SseKeyStore sseKeyStore() {
        return new InMemorySseKeyStore();
    }
}
