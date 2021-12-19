package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ConversationDataRepository extends ReactiveSortingRepository<ConversationDataModel, String> {

    Flux<ConversationDataModel> findAllByConversationId(UUID conversationId);
    Flux<ConversationDataModel> findAllByConversationIdAndConversationDataIdGreaterThan(UUID conversationId, String conversationDataId);
    Mono<ConversationDataModel> findFirstByConversationIdAndConversationDataType(UUID conversationId, ConversationDataModel.ConversationDataType conversationDataType);
}
