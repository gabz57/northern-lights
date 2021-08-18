package io.northernlights.chat.store.chatter.infrastructure;

import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryChatterStore implements ChatterStore {

    // COLD
    private final Map<ChatterId, Chatter> chattersByChatterId = new HashMap<>();
    private final Map<ChatterId, Set<ConversationId>> conversationsIdByChatterId = new HashMap<>();
    // LIVE
    private final Map<String, ChatterId> chatterIdBySseChatKey = new HashMap<>();
    private final Map<String, Map<ConversationId, ConversationDataId>> conversationStatusesBySseChatKey = new HashMap<>();

    public InMemoryChatterStore() {
        createChatter("1", "Tomtom");
        createChatter("2", "Nana");
        createChatter("3", "Pif");
        createChatter("4", "Hercule");
        createChatter("5", "Riri");
        createChatter("6", "Fifi");
        createChatter("7", "Loulou");
        createChatter("8", "Picsou");
        createConversation("1", List.of("1", "2"));
        createConversation("2", List.of("1", "3", "4", "5", "6"));
        createConversation("3", List.of("2", "3"));
        createConversation("4", List.of("2", "4"));
    }

    private void createConversation(String id, List<String> chatterIdValues) {
        chatterIdValues.forEach(chatterId -> conversationsIdByChatterId.merge(new ChatterId(chatterId), new HashSet<>(List.of(new ConversationId(id))), (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        }));
    }

    private void createChatter(String id, String name) {
        final ChatterId chatterId = new ChatterId(id);
        chattersByChatterId.put(chatterId, new Chatter(chatterId, name));
    }

    public Mono<Void> writeConversationCreated(ConversationCreatedEvent conversationEvent) {
        conversationEvent.getParticipants()
            .forEach(chatterId -> conversationsIdByChatterId.merge(chatterId, new HashSet<>(List.of(conversationEvent.getConversationId())), (set1, set2) -> {
                set1.addAll(set2);
                return set1;
            }));
        return Mono.empty();
    }

    public Mono<Void> writeChatterJoined(ChatterJoinedEvent conversationEvent) {
        conversationsIdByChatterId.merge(conversationEvent.getInvited(), new HashSet<>(List.of(conversationEvent.getConversationId())), (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        });
        return Mono.empty();
    }

    public Mono<List<ConversationId>> listConversationIds(ChatterId chatterId) {
        return Mono.just(new ArrayList<>(conversationsIdByChatterId.getOrDefault(chatterId, Collections.emptySet())));
    }

    public Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds) {
        return Mono.just(chatterIds.stream().map(chattersByChatterId::get).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public Mono<String> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus) {
        final String sseChatKey = UUID.randomUUID().toString();
        chatterIdBySseChatKey.put(sseChatKey, chatterId);
        conversationStatusesBySseChatKey.put(sseChatKey, conversationStatus);
        return Mono.just(sseChatKey);
    }

    public Mono<ChatterId> findChatterIdBySseChatKey(String sseChatKey) {
        log.info("findChatterIdBySseChatKey > {}", sseChatKey);
        return Mono.just(chatterIdBySseChatKey.get(sseChatKey));
    }

    public Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(String sseChatKey) {
        return Mono.justOrEmpty(conversationStatusesBySseChatKey.remove(sseChatKey));
    }

    public void revoke(String sseChatKey) {
        chatterIdBySseChatKey.remove(sseChatKey);
        conversationStatusesBySseChatKey.remove(sseChatKey);
    }
}
