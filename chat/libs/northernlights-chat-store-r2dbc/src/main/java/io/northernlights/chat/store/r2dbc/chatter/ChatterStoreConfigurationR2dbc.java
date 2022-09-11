package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.domain.store.chatter.ChatterStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatterStoreConfigurationR2dbc {

    @Bean
    public ChatterStore chatterStore(ChattersRepository chattersRepository) {
        return new R2dbcChatterStore(chattersRepository);
    }
}
