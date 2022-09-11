package io.northernlights.chat.domain.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class ChatEventObjectMapper {
    public static ObjectMapper chatEventObjectMapper() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
            .failOnEmptyBeans(false)
            .failOnUnknownProperties(false)
            .indentOutput(true)
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .modules(
                // Optional
                new Jdk8Module(),
                // Dates/Times
                new JavaTimeModule()
            )
            .featuresToDisable(
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS,
                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
            )
            .build();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }
}
