package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.api.domain.client.model.ChatDataConversationInstall;
import io.northernlights.chat.api.domain.client.model.ChatDataConversationPartial;
import io.northernlights.chat.api.domain.client.model.ChatDataUpdate;
import io.northernlights.chat.domain.event.*;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.Conversation;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationCreation;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ChatDataAdapter {
    private final ChatterStore chatterStore;

    public ChatData adaptConversationInstallData(
        ConversationId conversationId,
        ConversationCreation conversationCreation,
        List<Chatter> chatters,
        Conversation conversation,
        Map<ChatterId, ConversationDataId> readMarkerByChatterId
    ) {
        return ChatDataConversationInstall.builder()
            .name(conversationCreation.getName())
            .createdBy(conversationCreation.getChatterId())
            .createdAt(conversationCreation.getDateTime())
            .conversationId(conversationId)
            .dialogue(conversationCreation.getDialogue())
            .chatters(chatters)
            .conversationData(conversation)
            .readMarkers(readMarkerByChatterId)
            .build();
    }

    public ChatData adaptPartialColdData(
        ConversationId conversationId,
        List<Chatter> chatters,
        Conversation conversation,
        Map<ChatterId, ConversationDataId> readMarkerByChatterId
    ) {
        return ChatDataConversationPartial.builder()
            .conversationId(conversationId)
            .chatters(chatters)
            .conversationData(conversation)
            .readMarkers(readMarkerByChatterId)
            .build();
    }

    public Mono<ChatData> adaptLiveData(ConversationEvent conversationEvent) {
        return switch (conversationEvent.getConversationEventType()) {
            case CONVERSATION_CREATED -> adaptLiveData((ConversationCreatedEvent) conversationEvent);
            case CONVERSATION_MESSAGE -> adaptLiveData((ConversationMessageSentEvent) conversationEvent);
            case CONVERSATION_MARKED_AS_READ -> adaptLiveData((ConversationMarkedAsReadEvent) conversationEvent);
            case CHATTER_JOINED -> adaptLiveData((ChatterJoinedEvent) conversationEvent);
        };
    }

    private Mono<ChatData> adaptLiveData(ConversationCreatedEvent conversationEvent) {
        return chatterStore.listChatters(conversationEvent.getParticipants())
            .map(chatters -> ChatDataConversationInstall.builder()
                .name(conversationEvent.getName())
                .createdBy(conversationEvent.getCreatedBy())
                .createdAt(conversationEvent.getDateTime())
                .conversationId(conversationEvent.getConversationId())
                .dialogue(conversationEvent.getDialogue())
                .chatters(chatters)
                .conversationData(new Conversation())
                .readMarkers(new HashMap<>())
                .build());
    }

    private Mono<ChatData> adaptLiveData(ConversationMessageSentEvent conversationEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(conversationEvent.getConversationId())
            .message(ChatDataUpdate.MessageValue.builder()
                .conversationDataId(conversationEvent.getConversationDataId())
                .from(conversationEvent.getAuthor())
                .dateTime(conversationEvent.getDateTime())
                .message(conversationEvent.getMessage())
                .build())
            .build());
    }

    private Mono<ChatData> adaptLiveData(ConversationMarkedAsReadEvent conversationEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(conversationEvent.getConversationId())
            .markedAsRead(ChatDataUpdate.MarkedAsReadValue.builder()
                .conversationDataId(conversationEvent.getConversationDataId())
                .by(conversationEvent.getMarkedBy())
                .at(conversationEvent.getMarkedConversationDataId())
                .build())
            .build());
    }

    private Mono<ChatData> adaptLiveData(ChatterJoinedEvent conversationEvent) {
        return Mono.just(ChatDataUpdate.builder()
            .conversationId(conversationEvent.getConversationId())
            .chatterAdd(ChatDataUpdate.ChatterAddValue.builder()
                .conversationDataId(conversationEvent.getConversationDataId())
                .from(conversationEvent.getInvitedBy())
                .chatterId(conversationEvent.getInvited())
                .dateTime(conversationEvent.getDateTime())
                .build())
            .build());
    }
}
