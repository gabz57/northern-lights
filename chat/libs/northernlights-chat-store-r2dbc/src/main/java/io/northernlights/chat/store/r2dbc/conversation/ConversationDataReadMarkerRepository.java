package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataReadMarkerModel;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

@RequiredArgsConstructor
public class ConversationDataReadMarkerRepository {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Flux<ConversationDataReadMarkerModel> findAllByConversationId(UUID conversationId) {
        return r2dbcEntityTemplate.select(query(where("conversation_id").is(conversationId)), ConversationDataReadMarkerModel.class);
    }

    public Mono<ConversationDataReadMarkerModel> markAsRead(OffsetDateTime dateTime, ChatterId author, ConversationId conversationId, ConversationDataId conversationDataId) {
        return r2dbcEntityTemplate.update(ConversationDataReadMarkerModel.class)
            .matching(query(where("conversation_id").is(conversationId.getId())
                .and(where("chatter_id").is(author.getId()))))
            .apply(update("conversation_data_id", conversationDataId.getId()))
            .flatMap(updateCount -> {
                if (updateCount > 0) {
                    return Mono.empty();
                } else {
                    return r2dbcEntityTemplate.insert(ConversationDataReadMarkerModel.class)
                        .using(ConversationDataReadMarkerModel.builder()
                            .conversationId(conversationId.getId())
                            .conversationDataId(conversationDataId.getId())
                            .chatterId(author.getId())
//                            .createdAt(dateTime.toLocalDateTime())
                            .build());
                }
            });
    }

    public Mono<Void> deleteAll() {
        return r2dbcEntityTemplate.delete(ConversationDataReadMarkerModel.class).all().then();
    }
}
