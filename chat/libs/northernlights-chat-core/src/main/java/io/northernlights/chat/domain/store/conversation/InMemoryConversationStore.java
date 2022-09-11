package io.northernlights.chat.domain.store.conversation;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.ConversationIdGenerator;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.northernlights.chat.domain.model.conversation.readmarker.ConversationReadMarker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.*;

public class InMemoryConversationStore implements ConversationStore {

    private final Map<ConversationId, Conversation> conversations = new HashMap<>();
    private final Map<ConversationId, Map<ChatterId, ConversationDataId>> conversationsReadStatus = new HashMap<>();

    private final ConversationIdGenerator conversationIdGenerator;
//    private final ConversationDataIdGenerator conversationDataIdGenerator;

    public InMemoryConversationStore(ConversationIdGenerator conversationIdGenerator) {
        this.conversationIdGenerator = conversationIdGenerator;
//        this.conversationDataIdGenerator = conversationDataIdGenerator;
//        // note: we should be doing this creation from a higher level to avoid creation conversations on one side,
//        // and attach them to related chatters blindly here
//        final ChatterId chatterId1 = new ChatterId("1");
//        final ChatterId chatterId2 = new ChatterId("2");
//        final ChatterId chatterId3 = new ChatterId("3");
//        final ChatterId chatterId4 = new ChatterId("4");
//        final ChatterId chatterId5 = new ChatterId("5");
//        final ChatterId chatterId6 = new ChatterId("6");
//        createConversation(OffsetDateTime.now(), "1", "Conversation 1", chatterId1, List.of(chatterId1, chatterId2), false);
//        createConversation(OffsetDateTime.now(), "2", "Conversation 2", chatterId1, List.of(chatterId1, chatterId3, chatterId4, chatterId5, chatterId6), false);
//        createConversation(OffsetDateTime.now(), "3", "Conversation 3", chatterId2, List.of(chatterId2, chatterId3), false);
//        createConversation(OffsetDateTime.now(), "4", "Conversation 4", chatterId2, List.of(chatterId2, chatterId4), false);
    }

//    private void createConversation(OffsetDateTime dateTime, String conversationIdValue, String conversationName, ChatterId author, List<ChatterId> allParticipants, Boolean dialogue) {
//        ConversationId conversationId = ConversationId.of(conversationIdValue);
//        Conversation conversationDataList = new Conversation();
//        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
//        conversationDataList.add(new ConversationCreation(conversationId, conversationDataId, author, conversationName, allParticipants, dateTime, dialogue));
//        conversations.put(conversationId, conversationDataList);
//        conversationsReadStatus.put(conversationId, new HashMap<>());
//    }

    public Mono<Conversation> conversation(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
        return sinceConversationDataId != null
            ? Mono.just(new Conversation(conversations.get(conversationId).stream()
            .filter(conversationData -> conversationData.getConversationDataId().compareTo(sinceConversationDataId) > 0)
            .toList()))
            : Mono.just(new Conversation(conversations.get(conversationId)));
    }

    public Flux<ConversationData> conversationData(ConversationId conversationId, @Nullable ConversationDataId sinceConversationDataId) {
//        return sinceConversationDataId != null
//            ? Mono.just(new Conversation(conversations.get(conversationId).stream()
//            .filter(conversationData -> conversationData.getConversationDataId().compareTo(sinceConversationDataId) > 0)
//            .toList()))
//            : Mono.just(new Conversation(conversations.get(conversationId)));
//
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    public Mono<ConversationCreation> conversationDetails(ConversationId conversationId) {
        return Mono.just((ConversationCreation) conversations.get(conversationId).get(0));
    }

    public Flux<ConversationReadMarker> readMarkers(ConversationId conversationId) {
        Map<ChatterId, ConversationDataId> data = conversationsReadStatus.get(conversationId);
        return Flux.fromStream(data.entrySet().stream())
            .map(e -> ConversationReadMarker.of(conversationId, e.getKey(), e.getValue()));
    }

    public Mono<ConversationDataRef> create(OffsetDateTime dateTime, ChatterId author, String conversationName, List<ChatterId> participants, Boolean dialogue) {
        List<ChatterId> allParticipants = uniqueChatterIdsWithAuthor(author, participants);
        ConversationId conversationId = conversationIdGenerator.generate();
        Conversation conversationDataList = new Conversation();
        ConversationDataId conversationDataId = null; // FIXME: use next value instead of : conversationDataIdGenerator.generate();
        conversationDataList.add(new ConversationCreation(conversationDataId, author, conversationName, allParticipants, dateTime, dialogue));
        conversations.put(conversationId, conversationDataList);
        conversationsReadStatus.put(conversationId, new HashMap<>());

        return Mono.just(ConversationDataRef.of(conversationId, conversationDataId));
    }

    private List<ChatterId> uniqueChatterIdsWithAuthor(ChatterId author, List<ChatterId> participants) {
        List<ChatterId> allParticipants = new ArrayList<>(participants.stream().distinct().toList());
        if (!allParticipants.contains(author)) {
            allParticipants.add(author);
        }
        return allParticipants;
    }

    public Mono<ConversationDataRef> appendMessage(ConversationId conversationId, OffsetDateTime dateTime, ChatterId author, Message message) {
        ConversationDataId conversationDataId = null; // FIXME: use next value instead of : conversationDataIdGenerator.generate();
        ConversationMessage conversationMessage = new ConversationMessage(conversationDataId, author, dateTime, message);
        conversations.get(conversationId).add(conversationMessage);

        return Mono.just(ConversationDataRef.of(conversationId, conversationDataId));
    }

    public Mono<ConversationDataRef> addChatter(ConversationId conversationId, OffsetDateTime dateTime, ChatterId invitedByChatterId, ChatterId invitedChatterId) {
        ConversationDataId conversationDataId = null; // FIXME: use next value instead of : conversationDataIdGenerator.generate();
        ConversationChatter conversationChatter = new ConversationChatter(conversationDataId, invitedByChatterId, dateTime, invitedChatterId);
        conversations.get(conversationId).add(conversationChatter);

        return Mono.just(ConversationDataRef.of(conversationId, conversationDataId));
    }

    public Mono<ConversationDataRef> markEventAsRead(ConversationId conversationId, OffsetDateTime dateTime, ChatterId markedBy, ConversationDataId markedConversationDataId) {
//        ConversationDataId conversationDataId = conversationDataIdGenerator.generate();
//        ConversationReadMarker conversationReadMarker = new ConversationReadMarker(conversationId, conversationDataId, markedBy, markedConversationDataId, dateTime);
//        conversations.get(conversationId).add(conversationReadMarker);
        conversationsReadStatus.get(conversationId).put(markedBy, markedConversationDataId);

        return Mono.just(ConversationDataRef.of(conversationId, markedConversationDataId));
    }

    public Mono<List<ChatterId>> participants(ConversationId conversationId) {
        return Mono.just(conversations.get(conversationId).stream()
            .reduce(
                new ArrayList<>(),
                (lst, conversationEvent) -> {

                    if (conversationEvent instanceof ConversationCreation conversationCreation) {
                        lst.addAll(conversationCreation.getParticipants());
                    }
                    if (conversationEvent instanceof ConversationChatter conversationChatter) {
                        lst.add(conversationChatter.getChatterId());
                    }
                    lst.addAll(new ArrayList<>());
                    return lst;
                },
                (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }));
    }

    public Mono<List<ConversationId>> listConversationIds(ChatterId chatterId) {
        return null;
    }
//
//
//    public Mono<Void> writeConversationCreated(ConversationCreatedEvent conversationEvent) {
//        conversationEvent.getParticipants()
//            .forEach(chatterId -> conversationsIdByChatterId.merge(chatterId, new HashSet<>(List.of(conversationEvent.getConversationId())), (set1, set2) -> {
//                set1.addAll(set2);
//                return set1;
//            }));
//        return Mono.empty();
//    }
//
//    public Mono<Void> writeChatterJoined(ChatterJoinedEvent conversationEvent) {
//        conversationsIdByChatterId.merge(conversationEvent.getInvited(), new HashSet<>(List.of(conversationEvent.getConversationId())), (set1, set2) -> {
//            set1.addAll(set2);
//            return set1;
//        });
//        return Mono.empty();
//    }

}
