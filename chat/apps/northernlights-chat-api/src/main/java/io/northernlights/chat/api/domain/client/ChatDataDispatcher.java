package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.ConversationEvent;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.northernlights.chat.domain.event.ConversationEvent.ConversationEventType.CONVERSATION_CREATED;
import static java.util.Optional.ofNullable;

public class ChatDataDispatcher implements ChatDataProvider {

    private final Map<ChatterId, List<ConversationId>> followedConversationsByChatterId = new ConcurrentHashMap<>();
    private final Set<ConversationId> followedConversations = new HashSet<>();
    private final Flux<ConversationEvent> allConversationEventsFlux;
    private final ChatDataAdapter chatDataAdapter;

    public ChatDataDispatcher(ConversationEventSubscriber conversationEventSubscriber, ChatDataAdapter chatDataAdapter) {
        this.chatDataAdapter = chatDataAdapter;
        this.allConversationEventsFlux = Flux.from(conversationEventSubscriber.subscribe())
            .doOnNext(this::updateFollowedConversationsOnCreation)
            .filter(this::followedConversations)
            .share();
    }

    private boolean followedConversations(ConversationEvent conversationEvent) {
        return followedConversations.contains(conversationEvent.getConversationId());
    }

    public Flux<ChatData> chatterFlow(ChatClientID chatClientId, List<ConversationId> conversationIds) {
        followedConversationsByChatterId.put(chatClientId.getChatterId(), conversationIds);
        followedConversations.addAll(conversationIds);
        return allConversationEventsFlux
            .filter(conversationEvent -> isConversationFollowedByChatter(chatClientId, conversationEvent))
            .flatMap(chatDataAdapter::adaptLiveData)
            .doOnTerminate(() -> cleanFollowedConversations(chatClientId));
    }

    private boolean isConversationFollowedByChatter(ChatClientID chatClientId, ConversationEvent conversationEvent) {
        return followedConversationsByChatterId.get(chatClientId.getChatterId())
            .contains(conversationEvent.getConversationId());
    }

    private void cleanFollowedConversations(ChatClientID chatClientId) {
        List<ConversationId> removedConversationIds = followedConversationsByChatterId.remove(chatClientId.getChatterId());
        // ⚠️ count after removal of current chatter conversations
        Map<ConversationId, Long> nbFollowersByConversationId = followedConversationsByChatterId.values().stream().flatMap(Collection::stream)
            .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        // check for each conversation if other chatter are plugged
        removedConversationIds.forEach(removedConversationId -> {
            if (nbFollowersByConversationId.getOrDefault(removedConversationId, 0L) == 0L) {
                followedConversations.remove(removedConversationId);
            }
        });
    }

    private void updateFollowedConversationsOnCreation(ConversationEvent conversationEvent) {
        if (conversationEvent.getConversationEventType().equals(CONVERSATION_CREATED)) {
            ConversationCreatedEvent conversationCreatedEvent = (ConversationCreatedEvent) conversationEvent;
            conversationCreatedEvent.getParticipants()
                .forEach(chatterId -> ofNullable(followedConversationsByChatterId.get(chatterId))
                    .ifPresent(lst -> lst.add(conversationCreatedEvent.getConversationId())));
        }
    }
}
