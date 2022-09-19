package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.*;
import io.northernlights.chat.domain.event.ChatEvent;
import io.northernlights.chat.domain.event.conversation.ConversationCreatedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationEvent;
import io.northernlights.chat.domain.event.conversation.ConversationJoinedEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMarkedAsReadEvent;
import io.northernlights.chat.domain.event.conversation.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.readmarker.ConversationReadMarker;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChatEventDataAdapter {

    public ChatData adaptConversationInstall(
        ConversationId conversationId,
        ConversationCreation conversationCreation,
        List<ChatterId> chatters,
        Conversation conversation,
        List<ConversationReadMarker> conversationReadMarkers
    ) {
        return ChatDataConversationInstall.builder()
            .name(conversationCreation.getName())
            .createdBy(conversationCreation.getChatterId())
            .createdAt(conversationCreation.getDateTime())
            .conversationId(conversationId)
            .isPrivate(conversationCreation.getDialogue())
            .chatters(chatters)
            .conversationData(conversation)
            .readMarkers(conversationReadMarkers.stream()
                .collect(Collectors.toMap(ConversationReadMarker::getChatterId, ConversationReadMarker::getConversationDataId)))
            .build();
    }

    public ChatData adaptConversationUpdate(
        ConversationId conversationId,
        List<ChatterId> chatters,
        Conversation conversation,
        List<ConversationReadMarker> conversationReadMarkers
    ) {
        return ChatDataConversationPartial.builder()
            .conversationId(conversationId)
            .chatters(chatters)
            .conversationData(conversation)
            .readMarkers(conversationReadMarkers.stream()
                .collect(Collectors.toMap(ConversationReadMarker::getChatterId, ConversationReadMarker::getConversationDataId)))
            .build();
    }

    public Mono<ChatData> adaptLiveData(ChatEvent chatEvent) {
        if (chatEvent instanceof ConversationEvent conversationEvent) {
            return switch (conversationEvent.getType()) {
                case CONVERSATION_CREATED -> onEvent((ConversationCreatedEvent) chatEvent);
                case CONVERSATION_MESSAGE -> onEvent((ConversationMessageSentEvent) chatEvent);
                case CONVERSATION_MARKED_AS_READ -> onEvent((ConversationMarkedAsReadEvent) chatEvent);
                case CONVERSATION_JOINED -> onEvent((ConversationJoinedEvent) chatEvent);
            };
        } else if (chatEvent instanceof io.northernlights.chat.domain.event.file.FileEvent fileEvent) {
            throw new IllegalStateException("Unexpected event type: " + fileEvent.getType().name());
//            return switch (fileEvent.getType()) {
//                case FILE_CREATED:
//            };
        } else if (chatEvent instanceof io.northernlights.chat.domain.event.user.UserEvent userEvent) {
            throw new IllegalStateException("Unexpected event type: " + userEvent.getType().name());
//            return switch (userEvent.getType()) {
//                case USER_CREATED:
//            };
        } else {
            throw new IllegalStateException("Unexpected event type: " + chatEvent.getType().name());
        }
    }

    private Mono<ChatData> onEvent(ConversationCreatedEvent chatEvent) {
        return Mono.just(ChatDataConversationInstall.builder()
            .name(chatEvent.getName())
            .createdBy(ChatterId.of(chatEvent.getCreatedBy()))
            .createdAt(chatEvent.getTimestamp())
            .conversationId(ConversationId.of(chatEvent.getConversationId()))
            .isPrivate(chatEvent.getIsPrivate())
            .chatters(chatEvent.getParticipants().stream().map(ChatterId::of).toList())
            .conversationData(new Conversation())
            .readMarkers(new HashMap<>())
            .build());
    }

    private Mono<ChatData> onEvent(ConversationMessageSentEvent chatEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(ConversationId.of(chatEvent.getConversationId()))
            .message(ChatDataUpdate.MessageValue.builder()
                .conversationDataId(ConversationDataId.of(chatEvent.getConversationDataId()))
                .from(ChatterId.of(chatEvent.getAuthor()))
                .dateTime(chatEvent.getTimestamp())
                .message(new Message(chatEvent.getMessage().getText()))
                .build())
            .build());
    }

    private Mono<ChatData> onEvent(ConversationMarkedAsReadEvent chatEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(ConversationId.of(chatEvent.getConversationId()))
            .markedAsRead(ChatDataUpdate.MarkedAsReadValue.builder()
                .conversationDataId(ConversationDataId.of(chatEvent.getConversationDataId()))
                .by(ChatterId.of(chatEvent.getMarkedBy()))
//                .at(chatEvent.getMarkedConversationDataId())
                .build())
            .build());
    }

    private Mono<ChatData> onEvent(ConversationJoinedEvent chatEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(ConversationId.of(chatEvent.getConversationId()))
            .chatterAdd(ChatDataUpdate.ChatterAddValue.builder()
                .conversationDataId(ConversationDataId.of(chatEvent.getConversationDataId()))
                .from(ChatterId.of(chatEvent.getInvitedBy()))
                .chatterId(ChatterId.of(chatEvent.getInvited()))
                .dateTime(chatEvent.getTimestamp())
                .build())
            .build());
    }
//
//    private Mono<ChatData> adaptLiveData(ConversationCreatedEvent conversationEvent) {
//        return chatterStore.listChatters(conversationEvent.getParticipants())
//            .map(chatters -> ChatDataConversationInstall.builder()
//                .name(conversationEvent.getName())
//                .createdBy(conversationEvent.getCreatedBy())
//                .createdAt(conversationEvent.getDateTime())
//                .conversationId(conversationEvent.getConversationId())
//                .isPrivate(conversationEvent.getDialogue())
//                .chatters(chatters)
//                .conversationData(new Conversation())
//                .readMarkers(new HashMap<>())
//                .build());
//    }
//
//    private Mono<ChatData> adaptLiveData(ConversationMessageSentEvent conversationEvent) {
//        return Mono.just(ChatDataUpdate.builder()
//            .conversationId(conversationEvent.getConversationId())
//            .message(ChatDataUpdate.MessageValue.builder()
//                .conversationDataId(conversationEvent.getConversationDataId())
//                .from(conversationEvent.getAuthor())
//                .dateTime(conversationEvent.getDateTime())
//                .message(conversationEvent.getMessage())
//                .build())
//            .build());
//    }
//
//    private Mono<ChatData> adaptLiveData(ConversationMarkedAsReadEvent conversationEvent) {
//        return Mono.just(ChatDataUpdate.builder()
//            .conversationId(conversationEvent.getConversationId())
//            .markedAsRead(ChatDataUpdate.MarkedAsReadValue.builder()
//                .conversationDataId(conversationEvent.getConversationDataId())
//                .by(conversationEvent.getMarkedBy())
////                .at(conversationEvent.getMarkedConversationDataId())
//                .build())
//            .build());
//    }
//
//    private Mono<ChatData> adaptLiveData(ChatterJoinedEvent conversationEvent) {
//        return Mono.just(ChatDataUpdate.builder()
//            .conversationId(conversationEvent.getConversationId())
//            .chatterAdd(ChatDataUpdate.ChatterAddValue.builder()
//                .conversationDataId(conversationEvent.getConversationDataId())
//                .from(conversationEvent.getInvitedBy())
//                .chatterId(conversationEvent.getInvited())
//                .dateTime(conversationEvent.getDateTime())
//                .build())
//            .build());
//    }
}
