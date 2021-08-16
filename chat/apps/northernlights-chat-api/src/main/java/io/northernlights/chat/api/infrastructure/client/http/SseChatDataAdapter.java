package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.model.*;
import io.northernlights.chat.api.infrastructure.client.http.SseChatPayload.SseChatConversation;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationMessage;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SseChatDataAdapter {

    private static final String CONVERSATION_INSTALL = "CONVERS:INSTALL";
    private static final String CONVERSATION_PARTIAL = "CONVERS:PARTIAL";
    private static final String CONVERSATION_UPDATE_MESSAGE = "CONVERS:UPDATE:MESSAGE";
    private static final String CONVERSATION_UPDATE_READ_MARKER = "CONVERS:UPDATE:READ_MARKER";
    private static final String CHATTER_INSTALL = "CHATTER:INSTALL";

    public Flux<SseChatData> mapToSseChatData(ChatData chatData) {
        return switch (chatData.getChatDataType()) {
            case HELLO -> {
                ChatDataHello chatDataConversationInstall = (ChatDataHello) chatData;
                yield Flux.just(SseChatData.builder()
                    .event("Hello " + chatDataConversationInstall.getSseChatKey())
                    .build());
            }
            case INSTALL -> {
                ChatDataConversationInstall chatDataConversationInstall = (ChatDataConversationInstall) chatData;
                yield Flux.concat(
                    installChatters(chatDataConversationInstall.getChatters()),
                    installConversation(chatDataConversationInstall)
                );
            }
            case PARTIAL -> {
                ChatDataConversationPartial chatDataConversationPartial = (ChatDataConversationPartial) chatData;
                yield Flux.concat(
                    installChatters(chatDataConversationPartial.getChatters()),
                    partialConversation(chatDataConversationPartial)
                );
            }
            case UPDATE -> {
                ChatDataUpdate chatDataUpdate = (ChatDataUpdate) chatData;
                if (chatDataUpdate.getMessage() != null) {
                    yield updateConversationMessage(chatDataUpdate, chatDataUpdate.getMessage());
                } else if (chatDataUpdate.getMarkedAsRead() != null) {
                    yield updateConversationReadMarker(chatDataUpdate, chatDataUpdate.getMarkedAsRead());
                } else {
                    yield Flux.empty();
                }
            }
        };
    }

    private Flux<SseChatData> updateConversationMessage(ChatDataUpdate chatDataUpdate, ChatDataUpdate.MessageValue messageValue) {
        return Flux.just(SseChatData.builder()
            // .id()
            .event(CONVERSATION_UPDATE_MESSAGE)
            .payload(SseChatPayload.builder()
                .conversation(SseChatConversation.builder()
                    .id(chatDataUpdate.getConversationId().getId())
                    .data(List.of(
                        SseChatPayload.SseChatConversationData.builder()
                            .id(messageValue.getConversationDataId().getId())
                            .message(messageValue.getMessage().getContent())
                            .dateTime(messageValue.getDateTime().toEpochSecond())
                            .author(messageValue.getAuthor().getId())
                            .build()
                    ))
                    .build())
                .build())
            .build());
    }

    private Flux<SseChatData> updateConversationReadMarker(ChatDataUpdate chatDataUpdate, ChatDataUpdate.MarkedAsReadValue markedAsReadValue) {
        return Flux.just(SseChatData.builder()
            // .id()
            .event(CONVERSATION_UPDATE_READ_MARKER)
            .payload(SseChatPayload.builder()
                .conversation(SseChatConversation.builder()
                    .id(chatDataUpdate.getConversationId().getId())
                    .readMarkers(Map.of(markedAsReadValue.getBy().getId(), markedAsReadValue.getAt().getId()))
                    .build())
                .build())
            .build());
    }

    private Flux<SseChatData> installConversation(ChatDataConversationInstall chatDataConversationInstall) {
        return Flux.just(SseChatData.builder()
            // .id()
            .event(CONVERSATION_INSTALL)
            .payload(SseChatPayload.builder()
                .conversation(SseChatConversation.builder()
                    .id(chatDataConversationInstall.getConversationId().getId())
                    .creator(chatDataConversationInstall.getCreatedBy().getId())
                    .createdAt(chatDataConversationInstall.getCreatedAt().toEpochSecond())
                    .name(chatDataConversationInstall.getName())
//                                .from(conversationData.get(0).getConversationDataId())
//                                .to(conversationData.get(conversationData.size() - 1).getConversationDataId())
                    .dialogue(chatDataConversationInstall.getDialogue())
                    .participants(chatDataConversationInstall.getChatters().stream().map(Chatter::getChatterID).map(ChatterId::getId).collect(Collectors.toList()))
                    .data(chatDataConversationInstall.getConversationData().stream()
                        .map(this::toSseChatConversationData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                    .readMarkers(chatDataConversationInstall.getReadMarkers().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue().getId())))
                    .build())
                .build())
            .build());
    }

    private Flux<SseChatData> partialConversation(ChatDataConversationPartial chatDataConversationPartial) {
        return Flux.just(SseChatData.builder()
            // .id()
            .event(CONVERSATION_PARTIAL)
            .payload(SseChatPayload.builder()
                .conversation(SseChatConversation.builder()
                    .id(chatDataConversationPartial.getConversationId().getId())
//                                .from(conversationData.get(0).getConversationDataId())
//                                .to(conversationData.get(conversationData.size() - 1).getConversationDataId())
//                    .dialogue(chatDataConversationPartial.getDialogue())
                    .participants(chatDataConversationPartial.getChatters().stream().map(Chatter::getChatterID).map(ChatterId::getId).collect(Collectors.toList()))
                    .data(chatDataConversationPartial.getConversationData().stream()
                        .map(this::toSseChatConversationData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                    .readMarkers(chatDataConversationPartial.getReadMarkers().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue().getId())))
                    .build())
                .build())
            .build());
    }
    private Flux<SseChatData> installChatters(List<Chatter> chatters) {
        return Flux.fromIterable(chatters)
            .map(chatter -> SseChatData.builder()
                // .id()
                .event(CHATTER_INSTALL)
                .payload(SseChatPayload.builder()
                    .chatter(SseChatPayload.SseChatChatter.builder()
                        .id(chatter.getChatterID().getId())
                        .name(chatter.getName())
                        .build())
                    .build())
                .build());
    }

    private SseChatPayload.SseChatConversationData toSseChatConversationData(ConversationData conversationData) {
        return switch (conversationData.getConversationDataType()) {
            case CREATION, READ_MARKER:
                yield null;
            case MESSAGE:
                ConversationMessage conversationMessage = (ConversationMessage) conversationData;
                yield SseChatPayload.SseChatConversationData.builder()
                    .id(conversationMessage.getConversationDataId().getId())
                    .author(conversationMessage.getChatterId().getId())
                    .message(conversationMessage.getMessage().getContent())
                    .dateTime(conversationMessage.getDateTime().toEpochSecond())
                    .build();
        };
    }
}
