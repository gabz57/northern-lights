package io.northernlights.chat.event.publisher;

import io.northernlights.chat.events.redis.RedisChatEventConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@Import(RedisChatEventConfiguration.class)
@SpringBootApplication
public class ChatApiEventPublisherApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ChatApiEventPublisherApplication.class)
            .web(WebApplicationType.NONE)
            .run(args);
    }
}
