package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class R2DbcChatterStoreTest extends ChatStoreIntegrationTestBase {

    private static final OffsetDateTime CREATION_DATE_TIME = OffsetDateTime.of(2020, 3, 4, 13, 45, 25, 0, ZoneOffset.UTC);
    public static final OffsetDateTime CREATION_DATE_TIME_2 = CREATION_DATE_TIME.plusHours(1);
    private static final OffsetDateTime CHATTER_JOIN_DATE_TIME = CREATION_DATE_TIME_2;
    private static final ChatterId CONVERSATION_CREATOR_CHATTER_ID = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_1 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_2 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_3 = ChatterId.of(UUID.randomUUID());
    public static final ConversationDataId CONVERSATION_DATA_ID_1 = ConversationDataId.of("conv-id-1");
    public static final ConversationDataId CONVERSATION_DATA_ID_2 = ConversationDataId.of("conv-id-2");
    public static final ConversationId CONVERSATION_ID_1 = ConversationId.of(UUID.randomUUID());
    public static final ConversationId CONVERSATION_ID_2 = ConversationId.of(UUID.randomUUID());

    @Autowired
    private ChatterConversationRepository chatterConversationRepository;
    @Autowired
    private ChatterStore chatterStore;

    @Nested
    public class writeConversationCreated {
        @Test
        void should_add_entry_for_all_participants() {
            // Given
            ConversationCreatedEvent event = new ConversationCreatedEvent(
                CONVERSATION_ID_1,
                CONVERSATION_DATA_ID_1,
                CONVERSATION_CREATOR_CHATTER_ID,
                "conversation-name",
                List.of(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_2),
                CREATION_DATE_TIME,
                false
            );

            // When
            Mono<Void> conversationCreated = chatterStore.writeConversationCreated(event);

            // Then
            StepVerifier.create(conversationCreated.thenMany(chatterConversationRepository.findAll()))
                .expectNextCount(3L)
                .verifyComplete();

            chatterConversationRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(3L)
                .verifyComplete();
        }
    }

    @Nested
    public class writeChatterJoined {

        @Test
        void should_bind_chatter_to_conversation() {
            // Given
            Mono<Void> conversationCreated = chatterStore.writeConversationCreated(new ConversationCreatedEvent(
                CONVERSATION_ID_1,
                CONVERSATION_DATA_ID_1,
                CONVERSATION_CREATOR_CHATTER_ID,
                "conversation-name",
                List.of(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_2),
                CREATION_DATE_TIME,
                false
            ));

            // When
            Mono<Void> conversationJoined = conversationCreated.then(chatterStore.writeChatterJoined(new ChatterJoinedEvent(
                CONVERSATION_ID_1,
                CONVERSATION_DATA_ID_1,
                INVITED_CHATTER_ID_1,
                INVITED_CHATTER_ID_3,
                CHATTER_JOIN_DATE_TIME)));

            // Then
            conversationJoined
                .thenMany(chatterConversationRepository.findAll())
                .as(StepVerifier::create)
                .expectNextCount(4L)
                .verifyComplete();

            chatterConversationRepository.findAllByChatterId(INVITED_CHATTER_ID_3.getId())
                .as(StepVerifier::create)
                .expectNextCount(1L)
                .verifyComplete();
        }
    }

    @Nested
    public class listConversationIds {
        @Test
        void should_find_ConversationIds_of_given_chatter() {
            // Given
            Mono<Void> conversationCreated1 = chatterStore.writeConversationCreated(new ConversationCreatedEvent(
                CONVERSATION_ID_1,
                CONVERSATION_DATA_ID_1,
                CONVERSATION_CREATOR_CHATTER_ID,
                "conversation-name",
                List.of(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_2),
                CREATION_DATE_TIME,
                false
            ));

            Mono<Void> conversationCreated2 = chatterStore.writeConversationCreated(new ConversationCreatedEvent(
                CONVERSATION_ID_2,
                CONVERSATION_DATA_ID_2,
                CONVERSATION_CREATOR_CHATTER_ID,
                "conversation-name",
                List.of(INVITED_CHATTER_ID_2, INVITED_CHATTER_ID_3),
                CREATION_DATE_TIME_2,
                false
            ));

            // When
            Mono<List<ConversationId>> conversationJoined = conversationCreated1
                .then(conversationCreated2)
                .then(chatterStore.listConversationIds(INVITED_CHATTER_ID_2));

            // Then
            conversationJoined
                .as(StepVerifier::create)
                .expectNext(List.of(CONVERSATION_ID_1, CONVERSATION_ID_2))
                .verifyComplete();
        }
    }

    @Nested
    public class listChatters {
        @Test
        void should_find_all_expected_chatters() {
            // Given
            Mono<Chatter> createChatters = createChatter(CONVERSATION_CREATOR_CHATTER_ID)
                .then(createChatter(INVITED_CHATTER_ID_1))
                .then(createChatter(INVITED_CHATTER_ID_2))
                .then(createChatter(INVITED_CHATTER_ID_3));
            // When
            Mono<List<Chatter>> chatters = createChatters.then(chatterStore.listChatters(List.of(
                INVITED_CHATTER_ID_1,
                INVITED_CHATTER_ID_2
            )));

            // Then
            chatters.as(StepVerifier::create)
                .expectNext(List.of(
                    new Chatter(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_1.getId().toString()),
                    new Chatter(INVITED_CHATTER_ID_2, INVITED_CHATTER_ID_2.getId().toString())
                ))
                .verifyComplete();
        }
    }
}
