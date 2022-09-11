package io.northernlights.chat.store.r2dbc.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.conversation.ConversationEvent;
import io.northernlights.chat.domain.event.file.FileEvent;
import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.domain.event.user.UserEvent;
import io.northernlights.chat.store.r2dbc.event.model.ChatEventModel;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class R2dbcChatEventStore implements ChatEventStore {

    private final ChatEventRepository chatEventRepository;
    private final ObjectMapper chatEventObjectMapper;

    @Transactional
    public Mono<Void> publish(ChatEvent event) {
        return chatEventRepository.save(toModel(event))
            .then();
    }

    private ChatEventModel toModel(ChatEvent event) {
        String aggregateType;
        String aggregateId;
        if (event instanceof ConversationEvent conversationEvent) {
            aggregateType = "CONVERSATION";
            aggregateId = conversationEvent.getConversationId();
        } else if (event instanceof UserEvent userEvent) {
            aggregateType = "USER";
            aggregateId = userEvent.getUserId();
        } else if (event instanceof FileEvent fileEvent) {
            aggregateType = "FILE";
            aggregateId = fileEvent.getFileId();
        } else {
            throw new RuntimeException("Missing switch branch in " + this.getClass().getSimpleName());
        }
        return ChatEventModel.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(event.getType().name())
            .payload(toJson(event))
            .isNew(true)
            .build();
    }

    private Json toJson(ChatEvent chatEvent) {
        try {
            return Json.of(chatEventObjectMapper.writeValueAsString(chatEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
