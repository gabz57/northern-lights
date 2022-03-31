package io.northernlights.chat.store.conversation;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;

import java.time.OffsetDateTime;
import java.util.List;

public interface ConversationStore {

    Mono<List<ChatterId>> participants(ConversationId conversationId);

    @Deprecated
    Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId);
    Flux<ConversationData> conversationData(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId);

    Mono<ConversationCreation> conversationDetails(ConversationId conversationId);

    Flux<Tuple2<ChatterId, ConversationDataId>> readMarkers(ConversationId conversationId);

    Mono<ConversationCreatedEvent> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants, Boolean dialogue);

    Mono<ConversationMessageSentEvent> appendMessage(OffsetDateTime dateTime, ConversationId conversationId, ChatterId author, Message message);

    Mono<ChatterJoinedEvent> addChatter(OffsetDateTime dateTime, ConversationId conversationId, ChatterId invitedByChatterId, ChatterId invitedChatterId);

    Mono<ConversationMarkedAsReadEvent> markEventAsRead(OffsetDateTime dateTime, ConversationId conversationId, ChatterId chatterId, ConversationDataId markedConversationDataId);

}
