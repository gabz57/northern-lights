package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.r2dbc.chatter.model.ChatterConversationModel;
import io.northernlights.chat.store.r2dbc.chatter.model.ChatterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class R2dbcChatterStore implements ChatterStore {

    private final ChattersRepository chattersRepository;
    private final ChatterConversationRepository chatterConversationRepository;

    @Transactional
    public Mono<Void> writeConversationCreated(ConversationCreatedEvent conversationEvent) {
        log.info("writeConversationCreated {}", conversationEvent);
        return chatterConversationRepository.save(ChatterConversationModel.of(conversationEvent.getCreatedBy(), true, conversationEvent.getConversationId(), conversationEvent.getDateTime()))
            .thenMany(chatterConversationRepository.saveAll(conversationEvent.getParticipants().stream()
                .map(participant -> ChatterConversationModel.of(participant, false, conversationEvent.getConversationId(), conversationEvent.getDateTime()))
                .toList()))
            .then();
    }

    @Transactional
    public Mono<Void> writeChatterJoined(ChatterJoinedEvent conversationEvent) {
        return chatterConversationRepository.save(ChatterConversationModel.of(conversationEvent.getInvited(), false, conversationEvent.getConversationId(), conversationEvent.getDateTime()))
            .then();
    }

    @Transactional(readOnly = true)
    public Mono<List<ConversationId>> listConversationIds(ChatterId chatterId) {
        return chatterConversationRepository.findAllByChatterId(chatterId.getId())
            .map(ChatterConversationModel::getConversationId)
            .map(ConversationId::of)
            .collectList()
            .doOnNext(conversationIds -> log.info("listConversationIds of {}: {}", chatterId, conversationIds));
    }

    @Transactional(readOnly = true)
    public Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds) {
        return chattersRepository.findAllById(chatterIds.stream().map(ChatterId::getId).collect(toList()))
            .map(ChatterModel::toChatter)
            .collectList();
    }

    @Transactional
    public Mono<Chatter> insertChatter(Chatter chatter) {
        return chattersRepository.save(ChatterModel.of(chatter, true))
            .map(ChatterModel::toChatter);
    }

}
