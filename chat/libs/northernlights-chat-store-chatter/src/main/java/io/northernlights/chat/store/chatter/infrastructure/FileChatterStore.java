package io.northernlights.chat.store.chatter.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
public class FileChatterStore implements ChatterStore {
    private static final String CHATTER_PATH = "chatters/";
    private static final String CHATTER_CHATTER_BY_SSE_CHAT_KEY_PATH = "sse/%s-chatter.txt";
    private static final String CHATTER_STATUS_BY_SSE_CHAT_KEY_PATH = "sse/%s-status.txt";
    private static final String CHATTER_CHATTER_BY_CHATTER_ID_PATH = "chatter/%s/chatter.txt";
    private static final String CHATTER_CONVERSATIONS_BY_CHATTER_ID_PATH = "chatter/%s/conversations.txt";
    //
//    // COLD
//    private final Map<ChatterId, Chatter> chattersByChatterId = new HashMap<>();
//    private final Map<ChatterId, Set<ConversationId>> conversationsIdByChatterId = new HashMap<>();
//    // LIVE
//    private final Map<String, ChatterId> chatterIdBySseChatKey = new HashMap<>();
//    private final Map<String, Map<ConversationId, ConversationDataId>> conversationStatusesBySseChatKey = new HashMap<>();
    private FileService fileService;

    public FileChatterStore() {
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
        return listConversationIds(conversationEvent.getInvited())
            .doOnNext(convIds -> {
                if (!convIds.contains(conversationEvent.getConversationId())) {
                    Path conversationPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CONVERSATIONS_BY_CHATTER_ID_PATH, conversationEvent.getInvited()));
                    try {
                        Files.writeString(conversationPath, conversationEvent.getConversationId().getId() + "\n", StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            })
            .then();
    }

    public Mono<List<ConversationId>> listConversationIds(ChatterId chatterId) {
        Path conversationPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CONVERSATIONS_BY_CHATTER_ID_PATH, chatterId.getId()));
        if (Files.exists(conversationPath)) {
            return Flux.using(() -> Files.lines(conversationPath), Flux::fromStream, BaseStream::close)
                .map(ConversationId::new)
                .collectList();
        } else {
            return Mono.just(emptyList());
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Chatter> chatter(ChatterId chatterId) {
        Path chatterPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CHATTER_BY_CHATTER_ID_PATH, chatterId.getId()));
        if (Files.exists(chatterPath)) {
            final Chatter chatter;
            try {
                chatter = objectMapper.readValue(chatterPath.toFile(), Chatter.class);
            } catch (IOException e) {
                e.printStackTrace();
                return Mono.empty();
            }
            return Mono.just(chatter);
        } else {
            return Mono.empty();
        }
    }

    public Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds) {
        Flux.fromIterable(chatterIds).map()
        Flux.using(() -> Files.lines(conversationPath), Flux::fromStream, BaseStream::close)
            .map(ConversationId::new)
            .collectList();

        return Mono.just(chatterIds.stream().map(chattersByChatterId::get).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public Mono<String> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus) {
        final String sseChatKey = UUID.randomUUID().toString();

        Path chatterIdBySseChatKeyPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CHATTER_BY_SSE_CHAT_KEY_PATH, sseChatKey));
        try {
            Files.writeString(chatterIdBySseChatKeyPath, chatterId.getId(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String s;
        try {
            s = objectMapper.writeValueAsString(conversationStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Path chatterStatusBySseChatKeyPath = fileService.get(CHATTER_PATH, String.format(CHATTER_STATUS_BY_SSE_CHAT_KEY_PATH, sseChatKey));
        try {
            Files.writeString(chatterStatusBySseChatKeyPath, s, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        chatterIdBySseChatKey.put(sseChatKey, chatterId);
//        conversationStatusesBySseChatKey.put(sseChatKey, conversationStatus);
        return Mono.just(sseChatKey);
    }

    public Mono<ChatterId> findChatterIdBySseChatKey(String sseChatKey) {
        log.info("findChatterIdBySseChatKey > {}", sseChatKey);
        Path sseChatKeyPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CHATTER_BY_SSE_CHAT_KEY_PATH, sseChatKey));
        if (Files.exists(sseChatKeyPath)) {
            return Flux.using(() -> Files.lines(sseChatKeyPath), Flux::fromStream, BaseStream::close)
                .collectList()
                .flatMap(lines -> lines.stream()
                    .findFirst()
                    .map(ChatterId::new)
                    .map(Mono::just)
                    .orElse(Mono.empty()));
        } else {
            return Mono.empty();
        }
    }

    public Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(String sseChatKey) {
        return Mono.justOrEmpty(conversationStatusesBySseChatKey.remove(sseChatKey));
    }

    public void revoke(String sseChatKey) {
        Path chatterIdBySseChatKeyPath = fileService.get(CHATTER_PATH, String.format(CHATTER_CHATTER_BY_SSE_CHAT_KEY_PATH, sseChatKey));
        try {
            Files.deleteIfExists(chatterIdBySseChatKeyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path chatterStatusBySseChatKeyPath = fileService.get(CHATTER_PATH, String.format(CHATTER_STATUS_BY_SSE_CHAT_KEY_PATH, sseChatKey));
        try {
            Files.deleteIfExists(chatterStatusBySseChatKeyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
