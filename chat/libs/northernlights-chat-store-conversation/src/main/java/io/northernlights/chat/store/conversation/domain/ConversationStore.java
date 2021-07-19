package io.northernlights.chat.store.conversation.domain;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.conversation.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.model.conversation.event.ConversationMessageSentEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConversationStore {

    Mono<ConversationCreatedEvent> create(ChatterId author, String conversationName, List<ChatterId> participants);

    Mono<ConversationMessageSentEvent> appendMessage(ConversationId conversationID, ChatterId author, Message message);

    Mono<ConversationMarkedAsReadEvent> markEventAsRead(ConversationId conversationID, ChatterId chatterID, ConversationDataId conversationDataID);

    Mono<List<ChatterId>> participants(ConversationId conversationID);
}
