package io.northernlights.chat.events.redis;

import io.northernlights.chat.domain.event.ChatEventObjectMapper;
import io.northernlights.chat.domain.event.ChatEventWrapper;
import io.northernlights.store.redis.RedisConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Import(RedisConfiguration.class)
@Configuration
public class RedisChatEventConfiguration {
    @Bean
    public ReactiveRedisTemplate<String, ChatEventWrapper> reactiveChatEventRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<ChatEventWrapper> serializer = new Jackson2JsonRedisSerializer<>(ChatEventWrapper.class);
        serializer.setObjectMapper(ChatEventObjectMapper.chatEventObjectMapper());
        return new ReactiveRedisTemplate<>(
            factory,
            RedisSerializationContext.<String, ChatEventWrapper>newSerializationContext(serializer).build()
        );
    }

    @Bean
    public RedisTemplate<String, ChatEventWrapper> chatEventRedisTemplate(RedisConnectionFactory connectionFactory) {

        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer
            = new GenericJackson2JsonRedisSerializer(ChatEventObjectMapper.chatEventObjectMapper());

        RedisTemplate<String, ChatEventWrapper> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        //redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
