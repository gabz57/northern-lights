package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.ConversationDataType.*;
import static io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.ConversationDataType.CREATION;
import static org.assertj.core.api.Assertions.assertThat;

class R2dbcConversationStoreTest extends ChatStoreIntegrationTestBase {

    private static final OffsetDateTime CREATION_DATE_TIME = OffsetDateTime.of(2020, 3, 4, 13, 45, 25, 0, ZoneOffset.UTC);
    private static final OffsetDateTime MESSAGE_DATE_TIME = CREATION_DATE_TIME.plus(10, ChronoUnit.SECONDS);
    private static final OffsetDateTime MESSAGE_READ_DATE_TIME = MESSAGE_DATE_TIME.plus(10, ChronoUnit.SECONDS);
    private static final ChatterId CONVERSATION_CREATOR_CHATTER_ID = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_1 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_2 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_3 = ChatterId.of(UUID.randomUUID());

    @Autowired
    private DatabaseClient client;
    @Autowired
    private ConversationStore conversationStore;
    @Autowired
    private ConversationDataRepository conversationDataRepository;

    @Test
    public void databaseClientExists() {
        assertThat(client).isNotNull();
    }

    @Nested
    public class participants {
        @Test
        void should_list_participants_of_given_conversationId() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEventMono = createConversation();

            // When
            Mono<List<ChatterId>> participants = conversationCreatedEventMono
                .flatMap(conversationCreatedEvent -> conversationStore.participants(conversationCreatedEvent.getConversationId()));

            // Then
            participants.as(StepVerifier::create)
                .expectNextMatches(ls -> ls.size() == 3)
                .verifyComplete();
        }
    }

    @Nested
    public class conversation {
        @Test
        void should_fetch_all_conversation_data() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Mono<Conversation> conversation = conversationCreatedEvent
                .flatMap(ev -> conversationStore.appendMessage(MESSAGE_DATE_TIME, ev.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !")))
                .flatMap(event -> conversationStore.conversation(event.getConversationId(), null));

            // Then
            conversation.as(StepVerifier::create)
                .expectNextMatches(ls -> ls.size() == 2)
                .verifyComplete();
        }

        @Test
        void should_fetch_all_conversation_data_after_sinceConversationDataId() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Mono<Conversation> conversation = conversationCreatedEvent
                .flatMap(createdEvent -> conversationStore.appendMessage(MESSAGE_DATE_TIME, createdEvent.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !"))
                    .flatMap(messageSentEvent -> conversationStore.conversation(messageSentEvent.getConversationId(), createdEvent.getConversationDataId())));

            // Then
            conversation.as(StepVerifier::create)
                .expectNextMatches(ls -> ls.size() == 1)
                .verifyComplete();
        }
    }

    @Nested
    public class conversationData {
        @Test
        void should_fetch_all_conversation_data() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Flux<ConversationData> conversationData = conversationCreatedEvent
                .flatMap(ev -> conversationStore.appendMessage(MESSAGE_DATE_TIME, ev.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !")))
                .flatMapMany(event -> conversationStore.conversationData(event.getConversationId(), null));

            // Then
            conversationData.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
        }

        @Test
        void should_fetch_all_conversation_data_after_sinceConversationDataId() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Flux<ConversationData> conversationData = conversationCreatedEvent
                .flatMapMany(createdEvent -> conversationStore.appendMessage(MESSAGE_DATE_TIME, createdEvent.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !"))
                    .flatMapMany(messageSentEvent -> conversationStore.conversationData(messageSentEvent.getConversationId(), createdEvent.getConversationDataId())));

            // Then
            conversationData.as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        }
    }

    @Nested
    public class conversationCreationData {
        @Test
        void should_fetch_conversation_creation() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Mono<ConversationCreation> conversationCreation = conversationCreatedEvent
                .flatMap(event -> conversationStore.conversationCreationData(event.getConversationId()));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
        }
    }

    @Nested
    public class create {

        @Test
        void should_write_conversation_data() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // Then
            conversationCreatedEvent.flatMap(event -> conversationDataRepository.findById(event.getConversationDataId().getId()))
                .as(StepVerifier::create)
                .expectNextMatches(cm -> cm.getConversationDataType().equals(CREATION))
                .verifyComplete();
        }
    }

    @Nested
    public class appendMessage {
        @Test
        void should_write_conversation_data() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Mono<ConversationMessageSentEvent> conversationMessageSentEvent = conversationCreatedEvent
                .flatMap(ev -> conversationStore.appendMessage(MESSAGE_DATE_TIME, ev.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !")));

            // Then
            conversationMessageSentEvent.flatMap(event -> conversationDataRepository.findById(event.getConversationDataId().getId()))
                .as(StepVerifier::create)
                .expectNextMatches(cm -> cm.getConversationDataType().equals(MESSAGE))
                .verifyComplete();
        }
    }

    @Nested
    public class addChatter {
        @Test
        void should_write_conversation_data() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Mono<ChatterJoinedEvent> chatterJoinedEvent = conversationCreatedEvent
                .flatMap(ev -> conversationStore.addChatter(MESSAGE_DATE_TIME, ev.getConversationId(), INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_3));

            // Then
            chatterJoinedEvent.flatMap(event -> conversationDataRepository.findById(event.getConversationDataId().getId()))
                .as(StepVerifier::create)
                .expectNextMatches(cm -> cm.getConversationDataType().equals(CHATTER_ADD))
                .verifyComplete();
        }
    }

    @Nested
    public class readMarkers {
        @Test
        void should_return_first_marker_after_creation() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Flux<Tuple2<ChatterId, ConversationDataId>> conversationCreation = conversationCreatedEvent
                .flatMapMany(event -> conversationStore.readMarkers(event.getConversationId()));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        }

        @Test
        void should_create_marker_after_invitee_reply() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Flux<Tuple2<ChatterId, ConversationDataId>> conversationCreation = conversationCreatedEvent
                .flatMapMany(createdEvent -> conversationStore.appendMessage(MESSAGE_DATE_TIME, createdEvent.getConversationId(), INVITED_CHATTER_ID_1, new Message("Hello !"))
                    .flatMapMany(event -> conversationStore.readMarkers(event.getConversationId())));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
        }
    }

    @Nested
    public class markEventAsRead {

        @Test
        void should_update_creator_marker_after_creator_mark_as_read() {
            // Given
            Mono<ConversationCreatedEvent> conversationCreatedEvent = createConversation();

            // When
            Flux<Tuple2<ChatterId, ConversationDataId>> conversationCreation = conversationCreatedEvent
                    .flatMapMany(messageSentEvent -> conversationStore.markEventAsRead(MESSAGE_READ_DATE_TIME, messageSentEvent.getConversationId(), INVITED_CHATTER_ID_1, messageSentEvent.getConversationDataId())
                        .flatMapMany(event -> conversationStore.readMarkers(event.getConversationId())));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
        }
    }

    private Mono<ConversationCreatedEvent> createConversation() {
        return conversationStore.create(
            CREATION_DATE_TIME,
            CONVERSATION_CREATOR_CHATTER_ID,
            "conv-name",
            List.of(
                INVITED_CHATTER_ID_1,
                INVITED_CHATTER_ID_2
            ),
            false);
    }
}
