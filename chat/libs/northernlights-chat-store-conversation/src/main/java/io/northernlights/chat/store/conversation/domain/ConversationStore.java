package io.northernlights.chat.store.conversation.domain;

import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface ConversationStore {

    Mono<List<ChatterId>> participants(ConversationId conversationId);

    Mono<Conversation> conversation(ConversationId conversationId, ConversationDataId sinceConversationDataId);

    Mono<ConversationCreation> conversationCreationData(ConversationId conversationId);

    Mono<Map<ChatterId, ConversationDataId>> readMarkers(ConversationId conversationId);

    Mono<ConversationCreatedEvent> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants);

    Mono<ConversationMessageSentEvent> appendMessage(OffsetDateTime dateTime, ConversationId conversationId, ChatterId author, Message message);

    Mono<ConversationMarkedAsReadEvent> markEventAsRead(OffsetDateTime dateTime, ConversationId conversationId, ChatterId chatterId, ConversationDataId markedConversationDataId);

}
