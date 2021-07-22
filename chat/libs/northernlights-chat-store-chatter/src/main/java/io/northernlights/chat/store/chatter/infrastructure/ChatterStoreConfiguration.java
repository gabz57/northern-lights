package io.northernlights.chat.store.chatter.infrastructure;

import io.northernlights.chat.store.chatter.domain.ChatterStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatterStoreConfiguration {
    @Bean
    public ChatterStore chatterStore() {
        return new InMemoryChatterStore();
    }
}
