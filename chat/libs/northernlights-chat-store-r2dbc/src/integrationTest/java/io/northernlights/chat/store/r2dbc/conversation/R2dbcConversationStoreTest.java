package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.model.conversation.data.ConversationDataRef;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.readmarker.ConversationReadMarker;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
    @Autowired
    private ChatterConversationRepository chatterConversationRepository;

    @Test
    public void databaseClientExists() {
        assertThat(client).isNotNull();
    }

    @Nested
    public class create {

        @Test
        void should_write_conversation_data() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // Then
            conversationCreated.flatMap(event -> conversationDataRepository.findById(event.getConversationId(), event.getConversationDataId()))
                .as(StepVerifier::create)
                .expectNextMatches(cm -> cm.getConversationDataType().equals(CREATION))
                .verifyComplete();
        }

        @Test
        void should_add_entry_for_all_participants() {
            // Given
            // When
            Mono<ConversationDataRef> conversationCreated = createConversation();

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
    public class dynamicTables {

        @Test
        void should_create_dynamic_tables_for_conversation() {
            AtomicReference<ConversationId> conversationId = new AtomicReference<>();
            conversationStore.create(OffsetDateTime.now(), ChatterId.of(UUID.randomUUID()), "General", List.of(ChatterId.of(UUID.randomUUID()), ChatterId.of(UUID.randomUUID())), true)
                .doOnNext(t -> conversationId.set(t.getConversationId()))
                .doOnError(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
            conversationStore.appendMessage(conversationId.get(), OffsetDateTime.now(), ChatterId.of(UUID.randomUUID()), new Message("Hello"))
                .doOnError(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
            conversationStore.appendMessage(conversationId.get(), OffsetDateTime.now(), ChatterId.of(UUID.randomUUID()), new Message("Coucou"))
                .doOnError(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

            conversationDataRepository.findAllByConversationId(conversationId.get())
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
        }
    }

    @Nested
    public class participants {
        @Test
        void should_list_participants_of_given_conversationId() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<List<ChatterId>> participants = conversationCreated
                .flatMap(created -> conversationStore.participants(created.getConversationId()));

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
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<Conversation> conversation = conversationCreated
                .flatMap(created -> conversationStore.appendMessage(created.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, new Message("Hello !")))
                .flatMap(message -> conversationStore.conversation(message.getConversationId(), null));

            // Then
            conversation.as(StepVerifier::create)
                .expectNextMatches(ls -> ls.size() == 2)
                .verifyComplete();
        }

        @Test
        void should_fetch_all_conversation_data_after_sinceConversationDataId() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<Conversation> conversation = conversationCreated
                .flatMap(created -> conversationStore.appendMessage(created.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, new Message("Hello !"))
                    .flatMap(messageSent -> conversationStore.conversation(messageSent.getConversationId(), created.getConversationDataId())));

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
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationData> conversationData = conversationCreated
                .flatMap(ev -> conversationStore.appendMessage(ev.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, new Message("Hello !")))
                .flatMapMany(event -> conversationStore.conversationData(event.getConversationId(), null));

            // Then
            conversationData.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
        }

        @Test
        void should_fetch_all_conversation_data_after_sinceConversationDataId() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationData> conversationData = conversationCreated
                .flatMapMany(createdEvent -> conversationStore.appendMessage(createdEvent.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, new Message("Hello !"))
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
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<ConversationCreation> conversationCreation = conversationCreated
                .flatMap(event -> conversationStore.conversationDetails(event.getConversationId()));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
        }
    }

    @Nested
    public class appendMessage {
        @Test
        void should_write_conversation_data() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<ConversationDataRef> conversationMessageSent = conversationCreated
                .flatMap(ev -> conversationStore.appendMessage(ev.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, new Message("Hello !")));

            // Then
            conversationMessageSent.flatMap(event -> conversationDataRepository.findById(event.getConversationId(), event.getConversationDataId()))
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
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<ConversationDataRef> chatterJoined = conversationCreated
                .flatMap(created -> conversationStore.addChatter(created.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_3));

            // Then
            chatterJoined.flatMap(joined -> conversationDataRepository.findById(joined.getConversationId(), joined.getConversationDataId()))
                .as(StepVerifier::create)
                .expectNextMatches(cm -> cm.getConversationDataType().equals(CHATTER_ADD))
                .verifyComplete();
        }

        @Test
        void should_bind_chatter_to_conversation() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Mono<ConversationDataRef> chatterJoined = conversationCreated
                .flatMap(created -> conversationStore.addChatter(created.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_3));

            // Then
            chatterJoined
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
    public class readMarkers {
        @Test
        void should_be_empty_after_creation() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationReadMarker> conversationCreation = conversationCreated
                .flatMapMany(created -> conversationStore.readMarkers(created.getConversationId()));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
        }

        @Test
        void should_create_marker_after_author_marks() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationReadMarker> conversationCreation = conversationCreated
                .flatMap(creation -> conversationStore.appendMessage(creation.getConversationId(), MESSAGE_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, new Message("Hello !")))
                .flatMap(message -> conversationStore.markEventAsRead(message.getConversationId(), MESSAGE_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, message.getConversationDataId()))
                .flatMapMany(ref -> conversationStore.readMarkers(ref.getConversationId()));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        }

        @Test
        void should_create_markers_after_author_and_invitee_mark() {
            // Given
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationReadMarker> conversationCreation = conversationCreated
                .flatMap(creation -> conversationStore.appendMessage(creation.getConversationId(), MESSAGE_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, new Message("Hello !")))
                .flatMap(message -> conversationStore.markEventAsRead(message.getConversationId(), MESSAGE_DATE_TIME, INVITED_CHATTER_ID_1, message.getConversationDataId()))
                .flatMap(mark -> conversationStore.markEventAsRead(mark.getConversationId(), MESSAGE_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, mark.getConversationDataId()))
                .flatMapMany(ref -> conversationStore.readMarkers(ref.getConversationId()));

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
            Mono<ConversationDataRef> conversationCreated = createConversation();

            // When
            Flux<ConversationReadMarker> conversationCreation = conversationCreated
                .flatMapMany(creation -> conversationStore.markEventAsRead(creation.getConversationId(), MESSAGE_READ_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, creation.getConversationDataId())
                    .then(conversationStore.markEventAsRead(creation.getConversationId(), MESSAGE_READ_DATE_TIME, INVITED_CHATTER_ID_1, creation.getConversationDataId()))
                    .flatMapMany(markedAsRead -> conversationStore.readMarkers(markedAsRead.getConversationId())));

            // Then
            conversationCreation.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
        }
    }

    @Nested
    public class listConversationIds {

        @Test
        void should_find_ConversationIds_of_given_chatter() {
            // Given
            AtomicReference<ConversationId> conversationId1 = new AtomicReference<>();
            AtomicReference<ConversationId> conversationId2 = new AtomicReference<>();
            AtomicReference<ConversationId> conversationId3 = new AtomicReference<>();

            Mono<ConversationDataRef> conversationsCreation =
                conversationStore.create(CREATION_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, "conv-name",
                        List.of(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_2), false)
                    .doOnSuccess(ref -> conversationId1.set(ref.getConversationId()))
                    .then(conversationStore.create(CREATION_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, "conv-name",
                        List.of(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_3), false))
                    .doOnSuccess(ref -> conversationId2.set(ref.getConversationId()))
                    .then(conversationStore.create(CREATION_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, "conv-name",
                        List.of(INVITED_CHATTER_ID_2, INVITED_CHATTER_ID_3), false))
                    .doOnSuccess(ref -> conversationId3.set(ref.getConversationId()));

            // When
            Mono<List<ConversationId>> conversationJoined = conversationsCreation
                .then(conversationStore.listConversationIds(INVITED_CHATTER_ID_2));

            // Then
            conversationJoined
                .as(StepVerifier::create)
                .expectNextMatches(lst -> (lst.size() == 2
                    && lst.contains(conversationId1.get())
                    && lst.contains(conversationId3.get())))
                .verifyComplete();
        }
    }

    private Mono<ConversationDataRef> createConversation() {
        return conversationStore.create(CREATION_DATE_TIME, CONVERSATION_CREATOR_CHATTER_ID, "conv-name", List.of(
            CONVERSATION_CREATOR_CHATTER_ID,
            INVITED_CHATTER_ID_1,
            INVITED_CHATTER_ID_2
        ), false);
    }
}
