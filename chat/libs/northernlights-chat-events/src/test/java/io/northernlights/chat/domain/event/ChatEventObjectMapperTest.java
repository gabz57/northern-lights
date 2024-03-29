package io.northernlights.chat.domain.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationJoinedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMessageSentEvent;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatEventObjectMapperTest {

    private final ObjectMapper objectMapper = ChatEventObjectMapper.chatEventObjectMapper();

    @Test
    void can_read_and_write_json_with_ConversationCreatedEvent() throws JsonProcessingException {
        ChatEvent chatEvent = new ConversationCreatedEvent(
            UUID.randomUUID().toString(),
            "2",
            "p-1",
            "conversation-name",
            List.of("p-1", "p-2"),
            OffsetDateTime.now(),
            true
        );

        String s = objectMapper.writeValueAsString(chatEvent);

        ChatEvent chatEvent1 = objectMapper.readValue(s, ChatEvent.class);

        assertThat(chatEvent1).usingRecursiveComparison()
            .isEqualTo(chatEvent);
    }
    @Test
    void can_read_and_write_json_with_ConversationJoinedEvent() throws JsonProcessingException {
        ChatEvent chatEvent = new ConversationJoinedEvent(
            UUID.randomUUID().toString(),
            "2",
            "p-1",
            "p-2",
            OffsetDateTime.now()
        );

        String s = objectMapper.writeValueAsString(chatEvent);

        ChatEvent chatEvent1 = objectMapper.readValue(s, ChatEvent.class);

        assertThat(chatEvent1).usingRecursiveComparison()
            .isEqualTo(chatEvent);
    }

    @Test
    void can_read_and_write_json_with_ConversationMarkedAsReadEvent() throws JsonProcessingException {
        ChatEvent chatEvent = new ConversationMarkedAsReadEvent(
            UUID.randomUUID().toString(),
            "2",
            "p-1",
            OffsetDateTime.now()
        );

        String s = objectMapper.writeValueAsString(chatEvent);

        ChatEvent chatEvent1 = objectMapper.readValue(s, ChatEvent.class);

        assertThat(chatEvent1).usingRecursiveComparison()
            .isEqualTo(chatEvent);
    }

    @Test
    void can_read_and_write_json_with_ConversationMessageSentEvent() throws JsonProcessingException {
        ChatEvent chatEvent = new ConversationMessageSentEvent(
            UUID.randomUUID().toString(),
            "2",
            new ConversationMessageSentEvent.Message("hello"),
            "p-1",
            OffsetDateTime.now()
        );

        String s = objectMapper.writeValueAsString(chatEvent);

        ChatEvent chatEvent1 = objectMapper.readValue(s, ChatEvent.class);

        assertThat(chatEvent1).usingRecursiveComparison()
            .isEqualTo(chatEvent);
    }

}
