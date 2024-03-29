package io.northernlights.chat.domain.store.chatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatterStoreConfigurationInMemory {

    @Bean
    public ChatterStore chatterStore() {
        return new InMemoryChatterStore();
    }
}
