package io.northernlights.chat.domain.store.chatter;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryChatterStore implements ChatterStore {

    // COLD
    private final Map<ChatterId, Chatter> chattersByChatterId = new HashMap<>();
    private final Map<ChatterId, Set<ConversationId>> conversationsIdByChatterId = new HashMap<>();

    public InMemoryChatterStore() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        String id3 = UUID.randomUUID().toString();
        String id4 = UUID.randomUUID().toString();
        String id5 = UUID.randomUUID().toString();
        String id6 = UUID.randomUUID().toString();
        String id7 = UUID.randomUUID().toString();
        String id8 = UUID.randomUUID().toString();
        createChatter(id1, "Tomtom");
        createChatter(id2, "Nana");
        createChatter(id3, "Pif");
        createChatter(id4, "Hercule");
        createChatter(id5, "Riri");
        createChatter(id6, "Fifi");
        createChatter(id7, "Loulou");
        createChatter(id8, "Picsou");
        // note: we should be doing this creation from a higher level to avoid creation conversations on one side,
        // and attach them to related chatters blindly here
        createConversation("1", List.of(id1, id2));
        createConversation("2", List.of(id1,id3,id4,id5,id6));
        createConversation("3", List.of(id2,id3));
        createConversation("4", List.of(id2,id4));
    }

    public Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds) {
        return Mono.just(chatterIds.stream().map(chattersByChatterId::get).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void createConversation(String conversationId, List<String> chatterIdValues) {
        chatterIdValues.forEach(chatterId -> conversationsIdByChatterId.merge(ChatterId.of(chatterId), new HashSet<>(List.of(ConversationId.of(conversationId))), (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        }));
    }

    private void createChatter(String id, String name) {
        final ChatterId chatterId = ChatterId.of(id);
        chattersByChatterId.put(chatterId, new Chatter(chatterId, name));
    }

    @Override
    public Mono<Chatter> insertChatter(Chatter creator) {
        chattersByChatterId.put(creator.getChatterId(), new Chatter(creator.getChatterId(), creator.getName()));
        return Mono.just(creator);
    }
}
