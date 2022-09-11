package io.northernlights.chat.event.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@TestConfiguration
public class TestableEventStoreConfiguration {

    @Value("${chat.redis.workspace}")
    private String workspace;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListener messageListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, new ChannelTopic("nl-chat-events:" + workspace));

        return container;
    }

    @Bean
    public MessageListener messageListener(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveString");
    }

    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    public static class Receiver {

        private final AtomicInteger counter = new AtomicInteger();

        public void receiveString(String message, String channel) {
            log.info("Received on channel " + channel + "\n" + "<" + message + ">");
            counter.incrementAndGet();
        }

        public int getCount() {
            return counter.get();
        }

        public void reset() {
            counter.set(0);
        }
    }
}
