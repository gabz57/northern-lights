package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataAdapter;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.data.relational.core.query.Query.query;

@RequiredArgsConstructor
public class ConversationDataRepository {
    private static String tableConversationData(ConversationId conversationId) {
        return "conversation_" + cleanConversationId(conversationId);
    }

    private static String cleanConversationId(ConversationId conversationId) {
        return conversationId.getId().toString().replace("-", "");
    }


    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ConversationDataAdapter conversationDataAdapter;

    public Mono<Void> createConversationTable(ConversationId conversationId) {
        return r2dbcEntityTemplate.getDatabaseClient()
            .sql("CREATE TABLE " + tableConversationData(conversationId) +
                " (" +
                "    id         SERIAL PRIMARY KEY," +
                "    type       conversation_data_type not null," +
                "    chatter_id uuid                   not null," +
                "    data       json                   not null," +
                "    datetime   timestamp              not null," +
                "    created_at timestamp              not null" +
                ")")
            .then();
    }

    @Transactional
    public Mono<ConversationDataModel> writeConversationData(ConversationId conversationId, OffsetDateTime dateTime, ChatterId author, /*, ConversationDataId conversationDataId*/ ConversationData conversationData, ConversationDataModel.ConversationDataType conversationDataType) {
        return r2dbcEntityTemplate.insert(ConversationDataModel.class)
            .into(tableConversationData(conversationId))
            .using(conversationDataAdapter.of(dateTime, author, /*conversationId, /*conversationDataId,*/ conversationData, conversationDataType));
    }

    @Transactional(readOnly = true)
    Flux<ConversationDataModel> findAllByConversationId(ConversationId conversationId) {
        return r2dbcEntityTemplate.select(ConversationDataModel.class)
            .from(tableConversationData(conversationId))
            .all();
    }

    @Transactional(readOnly = true)
    Flux<ConversationDataModel> findAllByConversationIdAndConversationDataIdGreaterThan(ConversationId conversationId, ConversationDataId conversationDataId) {
        return r2dbcEntityTemplate.select(ConversationDataModel.class)
            .from(tableConversationData(conversationId))
            .matching(query(Criteria.where("id").greaterThan(Long.valueOf(conversationDataId.getId()))))
            .all();
    }

    @Transactional(readOnly = true)
    Mono<ConversationDataModel> findFirstByConversationIdAndConversationDataType(ConversationId conversationId, ConversationDataModel.ConversationDataType conversationDataType) {
        return r2dbcEntityTemplate.select(ConversationDataModel.class)
            .from(tableConversationData(conversationId))
            .matching(query(Criteria.where("type").is(conversationDataType)))
            .first();
    }

    @Transactional(readOnly = true)
    public Mono<ConversationDataModel> findById(ConversationId conversationId, ConversationDataId conversationDataId) {
        return r2dbcEntityTemplate.select(ConversationDataModel.class)
            .from(tableConversationData(conversationId))
            .matching(query(Criteria.where("id").is(Long.valueOf(conversationDataId.getId()))))
            .one();
    }

    @Transactional
    public Mono<Integer> deleteAll(ConversationId conversationId) {
        return r2dbcEntityTemplate.delete(ConversationDataModel.class)
            .from(tableConversationData(conversationId))
            .all();
    }
}
