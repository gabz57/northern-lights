package io.northernlights.chat.store.r2dbc.conversation.model;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.*;
import io.r2dbc.postgresql.codec.Json;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("conversation_data")
public class ConversationDataModel implements Persistable<String> {
    @Transient
    private boolean isNew;
    @Id
    @Column("id")
    private String conversationDataId;
    @Column("conversation_id")
    private UUID conversationId;
    @Column("type")
    private ConversationDataType conversationDataType;
    @Column("data")
    private Json data;
    // @CreatedBy
    @Column("chatter_id")
    private UUID chatterId;
    @Column("datetime")
    private LocalDateTime dateTime;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @Nullable
    @Transient
    public String getId() {
        return conversationDataId;
    }

    @Transient
    public boolean isNew() {
        return isNew;
    }

    public enum ConversationDataType {
        CREATION,
        MESSAGE,
        CHATTER_ADD
    }

    public interface JsonData {

    }

    @Value
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode
    public static class CreationData implements JsonData {
        String name;
        List<String> participants;
        Boolean dialogue;

        public ConversationCreation fromJsonValue(ConversationDataModel model) {
            return ConversationCreation.builder()
                .conversationId(ConversationId.of(model.conversationId))
                .conversationDataId(ConversationDataId.of(model.conversationDataId))
                .chatterId(ChatterId.of(model.chatterId))
                .dateTime(model.dateTime.atOffset(ZoneOffset.UTC))
                .name(this.name)
                .participants(this.participants.stream().map(ChatterId::of).collect(Collectors.toList()))
                .dialogue(this.dialogue)
                .build();
        }

        public static ConversationDataModel.CreationData toJsonValue(ConversationCreation conversationCreation) {
            return ConversationDataModel.CreationData.builder()
                .name(conversationCreation.getName())
                .participants(conversationCreation.getParticipants().stream()
                    .map(ChatterId::getId).map(UUID::toString)
                    .collect(Collectors.toList()))
                .dialogue(conversationCreation.getDialogue())
                .build();
        }
    }

    @Value
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode
    public static class MessageData implements JsonData {
        Message message;

        @Value
        @SuperBuilder
        @Jacksonized
        public static class Message {
            String content;
        }

        public ConversationMessage fromJsonValue(ConversationDataModel model) {
            return ConversationMessage.builder()
                .conversationId(ConversationId.of(model.conversationId))
                .conversationDataId(ConversationDataId.of(model.conversationDataId))
                .chatterId(ChatterId.of(model.chatterId))
                .dateTime(model.dateTime.atOffset(ZoneOffset.UTC))
                .message(new io.northernlights.chat.domain.model.conversation.Message(this.message.getContent()))
                .build();
        }

        public static ConversationDataModel.MessageData toJsonValue(ConversationMessage conversationMessage) {
            return ConversationDataModel.MessageData.builder()
                .message(Message.builder()
                    .content(conversationMessage.getMessage().getContent())
                    .build())
                .build();
        }
    }

    @Value
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode
    public static class ChatterAddedData implements JsonData {
        String invitedChatterId;

        public ConversationChatter fromJsonValue(ConversationDataModel model) {
            return ConversationChatter.builder()
                .conversationId(ConversationId.of(model.conversationId))
                .conversationDataId(ConversationDataId.of(model.conversationDataId))
                .chatterId(ChatterId.of(model.chatterId))
                .dateTime(model.dateTime.atOffset(ZoneOffset.UTC))
                .invitedChatterId(ChatterId.of(this.invitedChatterId))
                .build();
        }

        public static ConversationDataModel.ChatterAddedData toJsonValue(ConversationChatter conversationChatter) {
            return ConversationDataModel.ChatterAddedData.builder()
                .invitedChatterId(conversationChatter.getInvitedChatterId().getId().toString())
                .build();
        }

    }
}
