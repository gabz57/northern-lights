package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationJoinedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMessageSentEvent;
import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.northernlights.chat.domain.model.conversation.readmarker.ConversationReadMarker;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.model.ChatterConversationModel;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataAdapter;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.*;

import static io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.ConversationDataType;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class R2dbcConversationStore implements ConversationStore {
    private final ConversationIdGenerator conversationIdGenerator;
    private final ConversationsRepository conversationsRepository;
    private final ConversationDataRepository conversationDataRepository;
    private final ConversationDataAdapter conversationDataAdapter;
    private final ConversationDataReadMarkerRepository conversationDataReadMarkerRepository;
    private final ChatterConversationRepository chatterConversationRepository;
    private final ChatEventStore chatEventStore;

    @Transactional(readOnly = true)
    public Mono<List<ChatterId>> participants(ConversationId conversationId) {
        // TODO: perform this search using the correct table...
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
            .map(conversationDataId -> conversationDataRepository.findAllByConversationIdAndConversationDataIdGreaterThan(conversationId, conversationDataId))
            .orElseGet(() -> conversationDataRepository.findAllByConversationId(conversationId))
            .map(conversationDataAdapter::toConversationData);
    }

    @Transactional(readOnly = true)
    public Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
        return conversationData(conversationId, sinceConversationDataId)
            .collectList()
            .map(Conversation::new);
    }

    @Transactional(readOnly = true)
    public Mono<ConversationCreation> conversationDetails(ConversationId conversationId) {
        return conversationDataRepository.findFirstByConversationIdAndConversationDataType(conversationId, ConversationDataType.CREATION)
            .map(conversationDataAdapter::toConversationData)
            .cast(ConversationCreation.class);
    }

    @Transactional(readOnly = true)
    public Flux<ConversationReadMarker> readMarkers(ConversationId conversationId) {
        return conversationDataReadMarkerRepository.findAllByConversationId(conversationId)
            .map(readMarkerModel -> ConversationReadMarker.of(
                conversationId,
                ChatterId.of(readMarkerModel.getChatterId()),
                ConversationDataId.of(readMarkerModel.getConversationDataId())
            ));
    }

    @Transactional
    public Mono<ConversationDataRef> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants, Boolean isPrivate) {
        ConversationId conversationId = conversationIdGenerator.generate();
        ConversationCreation conversationData = new ConversationCreation(null, author, conversationName, participants, dateTime, isPrivate);

        return createConversation(conversationId, conversationName, !isPrivate)
            .then(conversationDataRepository.writeConversationData(conversationId, dateTime, author, conversationData, ConversationDataType.CREATION))
            .flatMap(convDataModel -> bindConversationToChatters(participants, conversationId, dateTime)
                .thenReturn(convDataModel))
            .map(conversationDataModel -> ConversationDataId.of(conversationDataModel.getConversationDataId()))
            .flatMap(conversationDataId -> chatEventStore.publish(new ConversationCreatedEvent(
                    conversationId.getId().toString(),
                    conversationDataId.getId(),
                    author.getId().toString(),
                    conversationName,
                    participants.stream().map(ChatterId::getId)
                        .map(UUID::toString).toList(), // expected to contain creator
                    dateTime,
                    isPrivate
                ))
                .thenReturn(ConversationDataRef.of(conversationId, conversationDataId)));
    }

    private Mono<Void> createConversation(ConversationId conversationId, String name, boolean isPublic) {
        return persistConversationId(conversationId, name, isPublic)
            .then(conversationDataRepository.createConversationTable(conversationId))
            .then(conversationDataReadMarkerRepository.createConversationMarkersTable(conversationId));
    }

    private Mono<ConversationModel> persistConversationId(ConversationId conversationId, String name, boolean isPublic) {
        return conversationsRepository.save(ConversationModel.builder()
            .id(conversationId.getId())
            .name(name)
            .isPublic(isPublic)
            .isNew(true)
            .build());
    }

    private Mono<Void> bindConversationToChatters(Collection<ChatterId> chatterIds, ConversationId conversationId, OffsetDateTime dateTime) {
        return Flux.fromIterable(chatterIds)
            .flatMap(chatterId -> bindConversationToChatter(chatterId, conversationId, dateTime))
            .then();
    }

    private Mono<Void> bindConversationToChatter(ChatterId chatterId, ConversationId conversationId, OffsetDateTime dateTime) {
        return chatterConversationRepository.save(ChatterConversationModel.of(chatterId, conversationId, dateTime))
            .then();
    }

    @Transactional
    public Mono<ConversationDataRef> appendMessage(ConversationId conversationId, OffsetDateTime dateTime, ChatterId author, Message message) {
        ConversationMessage conversationMessage = new ConversationMessage(null, author, dateTime, message);
        return conversationDataRepository.writeConversationData(conversationId, dateTime, author, conversationMessage, ConversationDataType.MESSAGE)
            .flatMap(conversationDataModel -> chatEventStore.publish(new ConversationMessageSentEvent(
                    conversationId.getId().toString(),
                    conversationDataModel.getConversationDataId().toString(),
                    new ConversationMessageSentEvent.Message(message.getContent()),
                    author.getId().toString(),
                    dateTime))
                .then(markEventAsRead(conversationId, dateTime, author, ConversationDataId.of(conversationDataModel.getConversationDataId())))
                .thenReturn(ConversationDataRef.of(conversationId, ConversationDataId.of(conversationDataModel.getConversationDataId()))));

    }

    @Transactional
    public Mono<ConversationDataRef> addChatter(ConversationId conversationId, OffsetDateTime dateTime, ChatterId invitedByChatterId, ChatterId invitedChatterId) {
        ConversationChatter conversationChatter = new ConversationChatter(null, invitedByChatterId, dateTime, invitedChatterId);
        return conversationDataRepository.writeConversationData(conversationId, dateTime, invitedByChatterId, conversationChatter, ConversationDataType.CHATTER_ADD)
            .flatMap(conversationDataModel -> bindConversationToChatter(invitedChatterId, conversationId, dateTime)
                .then(chatEventStore.publish(new ConversationJoinedEvent(
                    conversationId.getId().toString(),
                    conversationDataModel.getConversationDataId().toString(),
                    invitedByChatterId.getId().toString(),
                    invitedChatterId.getId().toString(),
                    dateTime)))
                .thenReturn(ConversationDataRef.of(conversationId, ConversationDataId.of(conversationDataModel.getConversationDataId()))));
    }

    @Transactional
    public Mono<ConversationDataRef> markEventAsRead(ConversationId conversationId, OffsetDateTime dateTime, ChatterId markedBy, ConversationDataId markedConversationDataId) {
        return conversationDataReadMarkerRepository.markAsRead(dateTime, markedBy, conversationId, markedConversationDataId)
            .flatMap(ref -> chatEventStore.publish(new ConversationMarkedAsReadEvent(
                    conversationId.getId().toString(),
                    markedConversationDataId.getId(),
                    markedBy.getId().toString(),
                    dateTime))
                .thenReturn(ref));
    }

    @Transactional(readOnly = true)
    public Mono<List<ConversationId>> listConversationIds(ChatterId chatterId) {
        return chatterConversationRepository.findAllByChatterId(chatterId.getId())
            .map(ChatterConversationModel::getConversationId)
            .map(ConversationId::of)
            .collectList();
        // TODO: complete ids with PUBLIC conversations
    }
}
