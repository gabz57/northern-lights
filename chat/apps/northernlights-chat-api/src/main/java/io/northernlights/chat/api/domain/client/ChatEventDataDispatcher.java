package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationEvent;
import io.northernlights.chat.domain.event.conversation.ConversationJoinedEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
public class ChatEventDataDispatcher implements ChatDataProvider {

    private final Map<ChatterId, List<ConversationId>> followedConversationsByChatterId = new ConcurrentHashMap<>();
    private final Set<ConversationId> followedConversations = new HashSet<>();
    private final Flux<ChatEvent> followedConversationsEventFlow;
    private final ChatEventDataAdapter chatDataAdapter;

    public ChatEventDataDispatcher(ChatEventSource chatEventSource, ChatEventDataAdapter chatDataAdapter) {
        log.info("Building ChatEventDataDispatcher");
        this.chatDataAdapter = chatDataAdapter;
        this.followedConversationsEventFlow = chatEventSource.subscribe()
            .doOnNext(this::updateFollowedConversations)
            .filter(this::followedConversationsOrGeneral)
            .share();
        followedConversationsEventFlow.subscribe(e -> log.info("Dispatching {}", e.getType()));
    }

    public Flux<ChatData> chatterFlow(ChatClientID chatClientId, List<ConversationId> conversationIds) {
        followedConversationsByChatterId.put(chatClientId.getChatterId(), conversationIds);
        followedConversations.addAll(conversationIds);
        return followedConversationsEventFlow
            .filter(eventsForChatter(chatClientId))
            .flatMap(chatDataAdapter::adaptLiveData)
            .doOnTerminate(() -> cleanFollowedConversations(chatClientId));
    }

    private void updateFollowedConversations(ChatEvent chatEvent) {
        if (chatEvent instanceof ConversationEvent conversationEvent) {
            switch (conversationEvent.getType()) {
                case CONVERSATION_CREATED -> {
                    log.info("updateFollowedConversations -> Creation");
                    ConversationCreatedEvent conversationCreatedEvent = (ConversationCreatedEvent) chatEvent;
                    conversationCreatedEvent.getParticipants()
                        .forEach(chatterId ->
                            ofNullable(followedConversationsByChatterId.get(ChatterId.of(chatterId)))
                                .ifPresent(lst -> {
                                    lst.add(ConversationId.of(conversationCreatedEvent.getConversationId()));
                                    followedConversations.add(ConversationId.of(conversationCreatedEvent.getConversationId()));
                                }));
                }
                case CONVERSATION_JOINED -> {
                    log.info("updateFollowedConversations -> ChatterJoined");
                    ConversationJoinedEvent chatterJoinedEvent = (ConversationJoinedEvent) chatEvent;
                    ofNullable(followedConversationsByChatterId.get(ChatterId.of(chatterJoinedEvent.getInvited())))
                        .ifPresent(followedConversationsOfChatter -> {
                            followedConversationsOfChatter.add(ConversationId.of(chatterJoinedEvent.getConversationId()));
                            followedConversations.add(ConversationId.of(chatterJoinedEvent.getConversationId()));
                        });
                }
                default -> {
                }
            }
        }
    }

    private boolean followedConversationsOrGeneral(ChatEvent chatEvent) {
        if (chatEvent instanceof ConversationEvent conversationEvent) {
            return followedConversations.contains(ConversationId.of(conversationEvent.getConversationId()));
        } else {
            return true;
        }
    }

    private Predicate<ChatEvent> eventsForChatter(ChatClientID chatClientId) {
        return chatEvent -> {
            if (chatEvent instanceof ConversationEvent conversationEvent) {
                return followedConversationsByChatterId.get(chatClientId.getChatterId())
                    .contains(ConversationId.of(conversationEvent.getConversationId()));
            } else {
                return true;
            }
        };
    }

    private void cleanFollowedConversations(ChatClientID chatClientId) {
        List<ConversationId> removedConversationIds = followedConversationsByChatterId.remove(chatClientId.getChatterId());
        // ⚠️ do not inline above expression, count must be done
        //    after removal of current chatter conversations
        Map<ConversationId, Long> nbFollowersByConversationId = followedConversationsByChatterId.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        // check for each conversation if other chatter are plugged
        removedConversationIds.forEach(removedConversationId -> {
            if (nbFollowersByConversationId.getOrDefault(removedConversationId, 0L) == 0L) {
                followedConversations.remove(removedConversationId);
            }
        });
    }
}
