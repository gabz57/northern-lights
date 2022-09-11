package io.northernlights.chat.store.r2dbc.conversation.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.data.ConversationChatter;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationMessage;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

import static io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.*;

@RequiredArgsConstructor
public class ConversationDataAdapter {
    private final ObjectMapper r2dbcObjectMapper;

    public ConversationDataModel of(OffsetDateTime dateTime, ChatterId author, ConversationData conversationData, ConversationDataType conversationDataType) {
        return builder()
            .conversationDataType(conversationDataType)
            .chatterId(author.getId())
            .dateTime(dateTime.toLocalDateTime())
            .data(toJson(conversationData))
            .isNew(true)
            .build();
    }

    private Json toJson(ConversationData conversationData) {
        try {
            return Json.of(r2dbcObjectMapper.writeValueAsString(switch (conversationData.getConversationDataType()) {
                case CREATION -> CreationData.toJsonValue((ConversationCreation) conversationData);
                case MESSAGE -> MessageData.toJsonValue((ConversationMessage) conversationData);
                case CHATTER_ADD -> ChatterAddedData.toJsonValue((ConversationChatter) conversationData);
            }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ConversationData toConversationData(ConversationDataModel model) {
        Json data = model.getData();
        String json = data.asString();
        try {
            return switch (model.getConversationDataType()) {
                case CREATION -> r2dbcObjectMapper.readValue(json, CreationData.class).fromJsonValue(model);
                case MESSAGE -> r2dbcObjectMapper.readValue(json, MessageData.class).fromJsonValue(model);
                case CHATTER_ADD -> r2dbcObjectMapper.readValue(json, ChatterAddedData.class).fromJsonValue(model);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
