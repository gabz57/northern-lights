package io.northernlights.chat.domain.store.conversation;

import io.northernlights.chat.domain.model.conversation.data.ConversationDataRef;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.readmarker.ConversationReadMarker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.List;

public interface ConversationStore {
    Mono<List<ChatterId>> participants(ConversationId conversationId);

    Mono<List<ConversationId>> listConversationIds(ChatterId chatterId);
    @Deprecated
    Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId);

    Flux<ConversationData> conversationData(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId);
    Mono<ConversationCreation> conversationDetails(ConversationId conversationId);

    Mono<ConversationDataRef> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants, Boolean dialogue);

    Flux<ConversationReadMarker> readMarkers(ConversationId conversationId);

    Mono<ConversationDataRef> appendMessage(ConversationId conversationId, OffsetDateTime dateTime, ChatterId author, Message message);

    Mono<ConversationDataRef> addChatter(ConversationId conversationId, OffsetDateTime dateTime, ChatterId invitedByChatterId, ChatterId invitedChatterId);

    Mono<ConversationDataRef> markEventAsRead(ConversationId conversationId, OffsetDateTime dateTime, ChatterId chatterId, ConversationDataId markedConversationDataId);
}
