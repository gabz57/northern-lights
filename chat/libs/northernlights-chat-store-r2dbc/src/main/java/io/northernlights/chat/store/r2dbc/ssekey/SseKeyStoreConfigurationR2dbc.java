package io.northernlights.chat.store.r2dbc.ssekey;

import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import io.northernlights.store.r2dbc.converter.R2dbcConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseKeyStoreConfigurationR2dbc {

    @Bean
    public SseKeyStore sseKeyStore(ChatterSseChatKeyRepository chatterSseChatKeyRepository) {
        return new R2dbcSseKeyStore(R2dbcConverters.R2DBC_OBJECT_MAPPER, chatterSseChatKeyRepository);
    }
}
