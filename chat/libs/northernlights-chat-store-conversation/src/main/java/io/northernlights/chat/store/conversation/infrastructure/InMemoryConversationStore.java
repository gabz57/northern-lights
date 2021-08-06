package io.northernlights.chat.store.conversation.infrastructure;

import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class InMemoryConversationStore implements ConversationStore {

    private final Map<ConversationId, Conversation> conversations = new HashMap<>();
    private final Map<ConversationId, Map<ChatterId, ConversationDataId>> conversationsReadStatus = new HashMap<>();

    private final ConversationIdGenerator conversationIdGenerator;
    private final ConversationDataIdGenerator conversationDataIdGenerator;

    public InMemoryConversationStore(ConversationIdGenerator conversationIdGenerator, ConversationDataIdGenerator conversationDataIdGenerator) {
        this.conversationIdGenerator = conversationIdGenerator;
        this.conversationDataIdGenerator = conversationDataIdGenerator;
        final ChatterId chatterId1 = new ChatterId("1");
        final ChatterId chatterId2 = new ChatterId("2");
        final ChatterId chatterId3 = new ChatterId("3");
        final ChatterId chatterId4 = new ChatterId("4");
        final ChatterId chatterId5 = new ChatterId("5");
        final ChatterId chatterId6 = new ChatterId("6");
        createConversation(OffsetDateTime.now(), "1", "Conversation 1", chatterId1, List.of(chatterId1, chatterId2));
        createConversation(OffsetDateTime.now(), "2", "Conversation 2", chatterId1, List.of(chatterId1, chatterId3, chatterId4, chatterId5, chatterId6));
        createConversation(OffsetDateTime.now(), "3", "Conversation 3", chatterId2, List.of(chatterId2, chatterId3));
        createConversation(OffsetDateTime.now(), "4", "Conversation 4", chatterId2, List.of(chatterId2, chatterId4));
    }

    private void createConversation(OffsetDateTime dateTime, String conversationIdValue, String conversationName, ChatterId author, List<ChatterId> allParticipants) {
        ConversationId conversationId = new ConversationId(conversationIdValue);
        Conversation conversationDataList = new Conversation();
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        conversationDataList.add(new ConversationCreation(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime));
        conversations.put(conversationId, conversationDataList);
        conversationsReadStatus.put(conversationId, new HashMap<>());
    }

    public Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
        return sinceConversationDataId != null
            ? Mono.just(new Conversation(conversations.get(conversationId).stream()
            .filter(conversationData -> conversationData.getConversationDataId().compareTo(sinceConversationDataId) > 0)
            .collect(toList())))
            : Mono.just(new Conversation(conversations.get(conversationId)));
    }

    public Mono<ConversationCreation> conversationCreationData(ConversationId conversationId) {
        return Mono.just((ConversationCreation) conversations.get(conversationId).get(0));
    }

    public Mono<Map<ChatterId, ConversationDataId>> readMarkers(ConversationId conversationId) {
        return Mono.just(conversationsReadStatus.get(conversationId));
    }

    public Mono<ConversationCreatedEvent> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants) {
        List<ChatterId> allParticipants = uniqueChatterIdsWithAuthor(author, participants);
        ConversationId conversationId = conversationIdGenerator.generate();
        Conversation conversationDataList = new Conversation();
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        conversationDataList.add(new ConversationCreation(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime));
        conversations.put(conversationId, conversationDataList);
        conversationsReadStatus.put(conversationId, new HashMap<>());

        return Mono.just(new ConversationCreatedEvent(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime));
    }

    private List<ChatterId> uniqueChatterIdsWithAuthor(ChatterId author, List<ChatterId> participants) {
        List<ChatterId> allParticipants = new ArrayList<>(participants.stream().distinct().toList());
        if (!allParticipants.contains(author)) {
            allParticipants.add(author);
        }
        return allParticipants;
    }

    public Mono<ConversationMessageSentEvent> appendMessage(OffsetDateTime dateTime, ConversationId conversationId, ChatterId author, Message message) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationMessage conversationMessage = new ConversationMessage(conversationId, conversationDataId, author, message, dateTime);
        conversations.get(conversationId).add(conversationMessage);

        return Mono.just(new ConversationMessageSentEvent(conversationId, conversationDataId, message, author, dateTime));
    }

    public Mono<ConversationMarkedAsReadEvent> markEventAsRead(OffsetDateTime dateTime, ConversationId conversationId, ChatterId markedBy, ConversationDataId markedConversationDataId) {
        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
        ConversationReadMarker conversationReadMarker = new ConversationReadMarker(conversationId, conversationDataId, markedBy, markedConversationDataId, dateTime);
        conversations.get(conversationId).add(conversationReadMarker);
        conversationsReadStatus.get(conversationId).put(markedBy, markedConversationDataId);

        return Mono.just(new ConversationMarkedAsReadEvent(conversationId, conversationDataId, markedConversationDataId, markedBy, dateTime));
    }

    public Mono<List<ChatterId>> participants(ConversationId conversationId) {
        return Mono.just(conversations.get(conversationId).stream()
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
