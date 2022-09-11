package io.northernlights.chat.store.r2dbc.event;

import io.northernlights.chat.domain.event.ChatEventObjectMapper;
import io.northernlights.chat.domain.event.store.ChatEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatEventStoreConfigurationR2dbc {

    @Bean
    public ChatEventStore chatEventStore(ChatEventRepository chatEventRepository) {
        return new R2dbcChatEventStore(chatEventRepository, ChatEventObjectMapper.chatEventObjectMapper());
    }
}
