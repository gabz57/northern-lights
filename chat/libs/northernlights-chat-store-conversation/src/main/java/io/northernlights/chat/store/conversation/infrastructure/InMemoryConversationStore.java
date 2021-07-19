package io.northernlights.chat.store.conversation.infrastructure;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.northernlights.chat.domain.model.conversation.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.conversation.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.model.conversation.event.ConversationMessageSentEvent;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import reactor.core.publisher.Mono;

import java.util.*;

public class InMemoryConversationStore implements ConversationStore {

    private final Map<ConversationId, Conversation> conversations = new HashMap<>();
    private final Map<ConversationId, Map<ChatterId, ConversationDataId>> conversationsReadStatus = new HashMap<>();

    private final ConversationIdGenerator conversationIdGenerator = () -> new ConversationId(UUID.randomUUID().toString());
    private final ConversationDataIdGenerator conversationDataIdGenerator = () -> new ConversationDataId(UUID.randomUUID().toString());

    public Mono<ConversationCreatedEvent> create(ChatterId author, String conversationName, List<ChatterId> participants) {
        ConversationId conversationID = conversationIdGenerator.generate();
        Conversation conversationDataList = new Conversation();
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        conversationDataList.add(new ConversationCreation(conversationID, conversationDataId, author, participants));
        conversations.put(conversationID, conversationDataList);
        conversationsReadStatus.put(conversationID, new HashMap<>());

        return Mono.just(new ConversationCreatedEvent(conversationID, conversationDataId, author, participants));
    }

    public Mono<ConversationMessageSentEvent> appendMessage(ConversationId conversationID, ChatterId author, Message message) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationMessage conversationMessage = new ConversationMessage(conversationID, conversationDataId, author, message);
        conversations.get(conversationID).add(conversationMessage);

        return Mono.just(new ConversationMessageSentEvent(conversationID, conversationDataId, message, author));
    }

    public Mono<ConversationMarkedAsReadEvent> markEventAsRead(ConversationId conversationID, ChatterId markedBy, ConversationDataId conversationDataID) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationReadMarker conversationReadMarker = new ConversationReadMarker(conversationID, conversationDataId, markedBy, conversationDataID);
        conversations.get(conversationID).add(conversationReadMarker);
        conversationsReadStatus.get(conversationID).put(markedBy, conversationDataID);

        return Mono.just(new ConversationMarkedAsReadEvent(conversationID, conversationDataId, conversationDataID, markedBy));
    }

    public Mono<List<ChatterId>> participants(ConversationId conversationID) {
        return Mono.just(conversations.get(conversationID).stream()
            .reduce(
                new ArrayList<>(),
                (lst, conversationEvent) -> {

                    if (conversationEvent instanceof ConversationCreation conversationCreation) {
                        lst.addAll(conversationCreation.getParticipants());
                    }
                    lst.addAll(new ArrayList<>());
                    return lst;
                },
                (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }));
    }
}
