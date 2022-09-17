package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataRef;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataReadMarkerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

@RequiredArgsConstructor
public class ConversationDataReadMarkerRepository {
    private static String tableConversationMarkers(ConversationId conversationId) {
        return "conversation_markers_" + cleanConversationId(conversationId);
    }

    private static String cleanConversationId(ConversationId conversationId) {
        return conversationId.getId().toString().replace("-", "");
    }

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Transactional
    public Mono<Void> createConversationMarkersTable(ConversationId conversationId) {
        return r2dbcEntityTemplate.getDatabaseClient()
            .sql("CREATE TABLE " + tableConversationMarkers(conversationId) +
                " (" +
                "    conversation_data_id varchar(255) not null," +
                "    chatter_id           uuid         PRIMARY KEY," +
                "    created_at           timestamp    not null," +
                "    updated_at           timestamp    not null" +
                ")")
            .then();
    }

    @Transactional(readOnly = true)
    public Flux<ConversationDataReadMarkerModel> findAllByConversationId(ConversationId conversationId) {
        return r2dbcEntityTemplate.select(ConversationDataReadMarkerModel.class)
            .from(tableConversationMarkers(conversationId))
            .all();
    }

    @Transactional
    public Mono<ConversationDataRef> markAsRead(OffsetDateTime dateTime, ChatterId author, ConversationId conversationId, ConversationDataId conversationDataId) {
        return r2dbcEntityTemplate.update(ConversationDataReadMarkerModel.class)
            .inTable(tableConversationMarkers(conversationId))
            .matching(query(where("chatter_id").is(author.getId())))
            .apply(update("conversation_data_id", conversationDataId.getId()))
            .flatMap(updateCount -> {
                if (updateCount > 0) {
                    return Mono.just(ConversationDataRef.of(conversationId, conversationDataId));
                } else {
                    return r2dbcEntityTemplate.insert(ConversationDataReadMarkerModel.class)
                        .into(tableConversationMarkers(conversationId))
                        .using(ConversationDataReadMarkerModel.builder()
                            .conversationDataId(conversationDataId.getId())
                            .chatterId(author.getId())
//                            .createdAt(dateTime.toLocalDateTime())
                            .build())
                        .thenReturn(ConversationDataRef.of(conversationId, conversationDataId));
                }
            });
    }

    @Transactional
    public Mono<Integer> deleteAll(ConversationId conversationId) {
        return r2dbcEntityTemplate.delete(ConversationDataReadMarkerModel.class)
            .from(tableConversationMarkers(conversationId))
            .all();
    }
}
