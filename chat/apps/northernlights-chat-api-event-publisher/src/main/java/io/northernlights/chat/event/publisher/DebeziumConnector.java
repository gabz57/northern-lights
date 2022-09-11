package io.northernlights.chat.event.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.ChatEventObjectMapper;
import io.northernlights.chat.domain.event.ChatEventWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class DebeziumConnector {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final RedisChatEventPublisher redisChatEventPublisher;
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    @Value("${chat.redis.workspace}")
    private String workspace;

    public DebeziumConnector(Configuration customerConnectorConfiguration, RedisChatEventPublisher redisChatEventPublisher) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(customerConnectorConfiguration.asProperties())
            .notifying(this::handleChangeEvent)
            .build();
        this.redisChatEventPublisher = redisChatEventPublisher;
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();

        log.info("Key = '" + sourceRecord.key() + "' value = '" + sourceRecord.value() + "'");

        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (sourceRecordChangeValue != null) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

            if (operation != Operation.READ) {
                String record = operation == Operation.DELETE ? BEFORE : AFTER; // Handling Update & Insert operations.

                Struct struct = (Struct) sourceRecordChangeValue.get(record);
                Map<String, Object> payload = struct.schema().fields().stream()
                    .map(Field::name)
                    .filter(fieldName -> struct.get(fieldName) != null)
                    .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                    .collect(toMap(Pair::getFirst, Pair::getSecond));
                ChatEventWrapper chatEventWrapper = toChatEventWrapper(payload);
                this.redisChatEventPublisher.publish(chatEventWrapper, workspace);
            }
        }
    }

    private ChatEventWrapper toChatEventWrapper(Map<String, Object> payload) {
        try {
            return ChatEventWrapper.builder()
                .type("event_callback")
                .eventId((String) payload.get("event_id")) // uuid
                .eventTime((Long) payload.get("event_time")) // unix ts in ms
                .event(ChatEventObjectMapper.chatEventObjectMapper().readValue((String)payload.get("event"), ChatEvent.class)) // ChatEvent in json
                .aggregateType((String) payload.get("aggregate_type")) // CONVERSATION
                .aggregateId((String) payload.get("aggregate_id")) // uuid (conversation)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }
}
