package io.northernlights.chat.store.r2dbc.conversation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataReadMarkerModel;
import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

import static io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.*;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class R2dbcConversationStore implements ConversationStore {
    private final ObjectMapper r2dbcObjectMapper;
    private final ConversationIdGenerator conversationIdGenerator;
    private final ConversationDataIdGenerator conversationDataIdGenerator;
    private final ConversationDataRepository conversationDataRepository;
    private final ConversationDataReadMarkerRepository conversationDataReadMarkerRepository;

    @Transactional(readOnly = true)
    public Mono<List<ChatterId>> participants(ConversationId conversationId) {
        // TODO: perform this search using 2 requests (filter by type)
        return conversationData(conversationId, null)
            .flatMap(cd -> switch (cd.getConversationDataType()) {
                case CREATION -> Flux.fromIterable(((ConversationCreation) cd).getParticipants());
                case CHATTER_ADD -> Flux.just(((ConversationChatter) cd).getInvitedChatterId());
                case MESSAGE -> Flux.empty();
            })
            .distinct()
            .collectList();
    }

    @Transactional(readOnly = true)
    public Flux<ConversationData> conversationData(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
        return ofNullable(sinceConversationDataId)
            .map(conversationDataId -> conversationDataRepository.findAllByConversationIdAndConversationDataIdGreaterThan(conversationId.getId(), conversationDataId.getId()))
            .orElseGet(() -> conversationDataRepository.findAllByConversationId(conversationId.getId()))
            .map(this::toConversationData);
    }

    @Transactional(readOnly = true)
    public Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
        return conversationData(conversationId, sinceConversationDataId)
            .collectList()
            .map(Conversation::new);
    }

    @Transactional(readOnly = true)
    public Mono<ConversationCreation> conversationDetails(ConversationId conversationId) {
        return conversationDataRepository.findFirstByConversationIdAndConversationDataType(conversationId.getId(), ConversationDataType.CREATION)
            .map(this::toConversationData)
            .cast(ConversationCreation.class);
    }

    @Transactional(readOnly = true)
    public Flux<Tuple2<ChatterId, ConversationDataId>> readMarkers(ConversationId conversationId) {
        return conversationDataReadMarkerRepository.findAllByConversationId(conversationId.getId())
            .map(readMarkerModel -> Tuples.of(
                ChatterId.of(readMarkerModel.getChatterId()),
                ConversationDataId.of(readMarkerModel.getConversationDataId())
            ));
    }

    @Transactional
    public Mono<ConversationCreatedEvent> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants, Boolean dialogue) {
        List<ChatterId> allParticipants = uniqueChatterIdsWithAuthor(author, participants);
        ConversationId conversationId = conversationIdGenerator.generate();
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationCreation conversationData = new ConversationCreation(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime, dialogue);
        return writeConversationData(dateTime, author, conversationId, conversationDataId, conversationData, ConversationDataType.CREATION)
            .then(markAsRead(dateTime, author, conversationId, conversationDataId))
            .thenReturn(new ConversationCreatedEvent(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime, dialogue));
    }

    @Transactional
    public Mono<ConversationMessageSentEvent> appendMessage(OffsetDateTime dateTime, ConversationId conversationId, ChatterId author, Message message) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationMessage conversationMessage = new ConversationMessage(conversationId, conversationDataId, author, dateTime, message);

        return writeConversationData(dateTime, author, conversationId, conversationDataId, conversationMessage, ConversationDataType.MESSAGE)
            .then(markAsRead(dateTime, author, conversationId, conversationDataId))
            .thenReturn(new ConversationMessageSentEvent(conversationId, conversationDataId, message, author, dateTime));
    }

    @Transactional
    public Mono<ChatterJoinedEvent> addChatter(OffsetDateTime dateTime, ConversationId conversationId, ChatterId invitedByChatterId, ChatterId invitedChatterId) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationChatter conversationChatter = new ConversationChatter(conversationId, conversationDataId, invitedByChatterId, dateTime, invitedChatterId);

        return writeConversationData(dateTime, invitedByChatterId, conversationId, conversationDataId, conversationChatter, ConversationDataType.CHATTER_ADD)
            .thenReturn(new ChatterJoinedEvent(conversationId, conversationDataId, invitedByChatterId, invitedChatterId, dateTime));
    }

    @Transactional
    public Mono<ConversationMarkedAsReadEvent> markEventAsRead(OffsetDateTime dateTime, ConversationId conversationId, ChatterId markedBy, ConversationDataId markedConversationDataId) {
        return markAsRead(dateTime, markedBy, conversationId, markedConversationDataId)
            .thenReturn(new ConversationMarkedAsReadEvent(conversationId, markedConversationDataId, markedBy, dateTime));
    }

    private Mono<ConversationDataModel> writeConversationData(OffsetDateTime dateTime, ChatterId author, ConversationId conversationId, ConversationDataId conversationDataId, ConversationData conversationData, ConversationDataType conversationDataType) {
        return conversationDataRepository.save(of(dateTime, author, conversationId, conversationDataId, conversationData, conversationDataType));
    }

    private ConversationDataModel of(OffsetDateTime dateTime, ChatterId author, ConversationId conversationId, ConversationDataId conversationDataId, ConversationData conversationData, ConversationDataType conversationDataType) {
        return builder()
            .conversationDataId(conversationDataId.getId())
            .conversationId(conversationId.getId())
            .conversationDataType(conversationDataType)
            .chatterId(author.getId())
            .dateTime(dateTime.toLocalDateTime())
            .data(toJson(conversationData))
            .isNew(true)
            .build();
    }

    private Mono<ConversationDataReadMarkerModel> markAsRead(OffsetDateTime dateTime, ChatterId author, ConversationId conversationId, ConversationDataId conversationDataId) {
        return conversationDataReadMarkerRepository.markAsRead(dateTime, author, conversationId, conversationDataId);
//        return conversationDataReadMarkerRepository.save(ConversationDataReadMarkerModel.builder()
//            .conversationId(conversationId.getId())
//            .conversationDataId(conversationDataId.getId())
//            .chatterId(author.getId())
//            .createdAt(dateTime)
//            .build());
    }

    private List<ChatterId> uniqueChatterIdsWithAuthor(ChatterId author, List<ChatterId> participants) {
        List<ChatterId> allParticipants = new ArrayList<>(participants.stream().distinct().toList());
        if (!allParticipants.contains(author)) {
            allParticipants.add(author);
        }
        return allParticipants;
    }

    private Json toJson(ConversationData conversationData) {
        try {
            return Json.of(r2dbcObjectMapper.writeValueAsString(switch (conversationData.getConversationDataType()) {
                case CREATION -> CreationData.toJsonValue((ConversationCreation) conversationData);
                case MESSAGE -> MessageData.toJsonValue((ConversationMessage) conversationData);
                case CHATTER_ADD -> ChatterAddedData.toJsonValue((ConversationChatter) conversationData);
            }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ConversationData toConversationData(ConversationDataModel model) {
        Json data = model.getData();
        String json = data.asString();
        try {
            return switch (model.getConversationDataType()) {
                case CREATION -> r2dbcObjectMapper.readValue(json, CreationData.class).fromJsonValue(model);
                case MESSAGE -> r2dbcObjectMapper.readValue(json, MessageData.class).fromJsonValue(model);
                case CHATTER_ADD -> r2dbcObjectMapper.readValue(json, ChatterAddedData.class).fromJsonValue(model);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
