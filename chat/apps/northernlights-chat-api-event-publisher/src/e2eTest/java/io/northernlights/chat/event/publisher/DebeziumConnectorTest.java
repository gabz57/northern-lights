package io.northernlights.chat.event.publisher;

import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.proxy.AwaitilityClassProxy;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
class DebeziumConnectorTest extends ChatApiEventPublisherIntegrationTestBase {

    @Order(0)
    @Test
    void contextLoads() {
        boolean actual = true;
        assertThat(actual).isTrue();
    }

    @Order(1)
    @Test
    void should_forward_ChatEvent_to_redis_topic_on_chat_outbox_inserts() {
        // Given
        ChatEvent creation = new ConversationCreatedEvent(
            UUID.randomUUID().toString(),
            "2",
            "p-1",
            "conversation-name",
            List.of("p-1", "p-2"),
            OffsetDateTime.now(),
            true
        );

        // When
        chatEventStore.publish(creation).subscribe();

        // Then
        await().atMost(5, TimeUnit.SECONDS)
            .untilCall(AwaitilityClassProxy.to(receiver).getCount(), equalTo(1));
    }
}
