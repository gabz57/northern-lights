package io.northernlights.chat.store.r2dbc.event;

import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class R2dbcEventStoreTest extends ChatStoreIntegrationTestBase {
    @Autowired
    private ChatEventStore chatEventStore;
    @Autowired
    private ChatEventRepository chatEventRepository;

    @Nested
    public class publish {
        @Test
        void should_persist_event_in_chat_outbox() {
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
            Mono<Void> publication = chatEventStore.publish(creation);

            // Then
            publication.then(chatEventRepository.findAll().collectList())
                .as(StepVerifier::create)
                .expectNextMatches(ls -> ls.size() == 1)
                .verifyComplete();
        }
    }
}
