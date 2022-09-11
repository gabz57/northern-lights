package io.northernlights.chat.store.r2dbc.ssekey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.store.r2dbc.ssekey.model.SseChatKeyModel;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class R2dbcSseKeyStore implements SseKeyStore {
    private final ObjectMapper r2dbcObjectMapper;
    private final ChatterSseChatKeyRepository chatterSseChatKeyRepository;

    @Transactional
    public Mono<SseChatKey> storeStatusAndGenerateSseChatKey(ChatterId chatterId, Map<ConversationId, ConversationDataId> conversationStatus) {
        return chatterSseChatKeyRepository.save(SseChatKeyModel.builder()
                .chatterId(chatterId.getId())
                .conversationStatus(toJson(conversationStatus))
                .build())
            .map(SseChatKeyModel::getSseChatKey)
            .map(SseChatKey::of);
    }

    @Transactional(readOnly = true)
    public Mono<ChatterId> findChatterIdBySseChatKey(SseChatKey sseChatKey) {
        return chatterSseChatKeyRepository.findById(sseChatKey.getId())
            .map(SseChatKeyModel::getChatterId)
            .map(ChatterId::of);
    }

    @Transactional
    public Mono<Map<ConversationId, ConversationDataId>> useConversationStatusesBySseChatKey(SseChatKey sseChatKey) {
        return chatterSseChatKeyRepository.findById(sseChatKey.getId())
            .flatMap(entity -> chatterSseChatKeyRepository.delete(entity).thenReturn(entity))
            .map(SseChatKeyModel::getConversationStatus)
            .map(this::toConversationStatuses);
    }

    @Transactional
    public Mono<Void> revoke(SseChatKey sseChatKey) {
        return chatterSseChatKeyRepository.findById(sseChatKey.getId())
            .flatMap(chatterSseChatKeyRepository::delete);
    }

    private Map<ConversationId, ConversationDataId> toConversationStatuses(Json json) {
        SseChatKeyModel.ConversationStatuses conversationStatuses;
        try {
            conversationStatuses = r2dbcObjectMapper.readValue(json.asString(), SseChatKeyModel.ConversationStatuses.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return conversationStatuses.entrySet().stream()
            .collect(Collectors.toMap(
                e -> ConversationId.of(e.getKey()),
                e -> ConversationDataId.of(e.getValue())
            ));
    }

    private Json toJson(Map<ConversationId, ConversationDataId> conversationStatuses) {
        try {
            return Json.of(r2dbcObjectMapper.writeValueAsString(SseChatKeyModel.ConversationStatuses.of(conversationStatuses)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
