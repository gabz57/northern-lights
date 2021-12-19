package io.northernlights.chat.store.r2dbc.ssekey;

import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import io.northernlights.chat.store.ssekey.SseKeyStore;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class R2dbcSseKeyStoreTest extends ChatStoreIntegrationTestBase {
    private static final OffsetDateTime CREATION_DATE_TIME = OffsetDateTime.of(2020, 3, 4, 13, 45, 25, 0, ZoneOffset.UTC);
    private static final ChatterId CONVERSATION_CREATOR_CHATTER_ID = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_1 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_2 = ChatterId.of(UUID.randomUUID());

    @Autowired
    private ChatterSseChatKeyRepository chatterSseChatKeyRepository;
    @Autowired
    private SseKeyStore sseKeyStore;
    @Autowired
    private ConversationStore conversationStore;

    private ConversationDataId conversationDataId;
    private ConversationId conversationId;

    @Nested
    public class storeStatusAndGenerateSseChatKey {
        @Test
        void should_return_inserted_line_with_no_convesation() {
            Mono<SseChatKey> sseChatKey = createChatter(CONVERSATION_CREATOR_CHATTER_ID)
                .then(storeStatusAndGenerateSseChatKey(Map.of()));

            StepVerifier.create(sseChatKey)
                .expectNextCount(1L)
                .verifyComplete();
        }

        @Test
        void should_return_inserted_line_with_existing_conversation() {
            // Given
            Mono<ConversationCreatedEvent> conversation = createChatterAndConversation();

            // When
            Mono<SseChatKey> sseChatKeyMono = conversation
                .flatMap(conversationCreatedEvent -> storeStatusAndGenerateSseChatKey(Map.of(
                    conversationCreatedEvent.getConversationId(), conversationCreatedEvent.getConversationDataId()
                )));

            // Then
            sseChatKeyMono.thenMany(chatterSseChatKeyRepository.findAll())
                .as(StepVerifier::create)
                .expectNextMatches(sseChatKeyModel -> sseChatKeyModel.getConversationStatus().asString()
                    .equals("{\"" + conversationId.getId() + "\": \"" + conversationDataId.getId() + "\"}"))
                .verifyComplete();
        }
    }

    @Nested
    public class findChatterIdBySseChatKey {
        @Test
        void should_return_existing_line() {
            // Given
            Mono<SseChatKey> sseChatKeyMono = createChatterAndConversation()
                .flatMap(conversationCreatedEvent -> storeStatusAndGenerateSseChatKey(Map.of(
                    conversationCreatedEvent.getConversationId(), conversationCreatedEvent.getConversationDataId()
                )));

            // When
            Flux<ChatterId> chatterId = sseChatKeyMono.flatMapMany(sseChatKey -> sseKeyStore.findChatterIdBySseChatKey(sseChatKey));

            // Then
            StepVerifier.create(chatterId)
                .expectNext(CONVERSATION_CREATOR_CHATTER_ID)
                .verifyComplete();
        }
    }

    @Nested
    public class useConversationStatusesBySseChatKey {
        @Test
        void should_return_and_delete_existing_line() {
            // Given
            SseChatKey sseChatKey = createChatterAndConversation()
                .flatMap(conversationCreatedEvent -> storeStatusAndGenerateSseChatKey(Map.of(
                    conversationCreatedEvent.getConversationId(), conversationCreatedEvent.getConversationDataId()
                ))).block();

            // When
            Mono<Map<ConversationId, ConversationDataId>> conversationStatuse = sseKeyStore.useConversationStatusesBySseChatKey(sseChatKey);

            // Then
            StepVerifier.create(conversationStatuse)
                .expectNextMatches(e -> e.equals(Map.of(conversationId, conversationDataId)))
                .verifyComplete();
            StepVerifier.create(sseKeyStore.findChatterIdBySseChatKey(sseChatKey))
                .verifyComplete();
        }
    }

    @Nested
    public class revoke {

        @Test
        void should_delete_existing_line() {
            // Given
            SseChatKey sseChatKey = createChatterAndConversation()
                .flatMap(conversationCreatedEvent -> storeStatusAndGenerateSseChatKey(Map.of(
                    conversationCreatedEvent.getConversationId(), conversationCreatedEvent.getConversationDataId()
                ))).block();

            // When
            Mono<Void> revoke = sseKeyStore.revoke(sseChatKey);

            // Then
            StepVerifier.create(revoke)
                .verifyComplete();
            StepVerifier.create(sseKeyStore.findChatterIdBySseChatKey(sseChatKey))
                .verifyComplete();
        }
    }

    private Mono<ConversationCreatedEvent> createChatterAndConversation() {
        return createChatter(CONVERSATION_CREATOR_CHATTER_ID)
            .then(createConversation());
    }

    private Mono<ConversationCreatedEvent> createConversation() {
        conversationDataId = null;
        conversationId = null;
        return conversationStore.create(
                CREATION_DATE_TIME,
                CONVERSATION_CREATOR_CHATTER_ID,
                "conv-name",
                List.of(
                    INVITED_CHATTER_ID_1,
                    INVITED_CHATTER_ID_2
                ),
                false)
            .doOnSuccess(e -> {
                conversationDataId = e.getConversationDataId();
                conversationId = e.getConversationId();
            });
    }

    private Mono<SseChatKey> storeStatusAndGenerateSseChatKey(Map<ConversationId, ConversationDataId> conversationCreatedEvent) {
        return sseKeyStore.storeStatusAndGenerateSseChatKey(CONVERSATION_CREATOR_CHATTER_ID, conversationCreatedEvent);
    }
}
