package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.api.domain.client.model.*;
import io.northernlights.chat.api.infrastructure.client.http.SseChatPayload.SseChatConversation;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.data.ConversationChatter;
import io.northernlights.chat.domain.model.conversation.data.ConversationData;
import io.northernlights.chat.domain.model.conversation.data.ConversationMessage;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SseChatDataAdapter {

    private static final String CONVERSATION_INSTALL = "CONVERS:INSTALL";
    private static final String CONVERSATION_PARTIAL = "CONVERS:PARTIAL";
    private static final String CONVERSATION_UPDATE_MESSAGE = "CONVERS:UPDATE:MESSAGE";
    private static final String CONVERSATION_UPDATE_READ_MARKER = "CONVERS:UPDATE:READ_MARKER";
    private static final String CONVERSATION_ADD_CHATTER = "CONVERS:UPDATE:ADD_CHATTER";
    private static final String CHATTER_INSTALL = "CHATTER:INSTALL";

    public Flux<SseChatData> mapToSseChatData(ChatData chatData) {
        return switch (chatData.getChatDataType()) {
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
            case LIVE_UPDATE -> {
                ChatDataUpdate chatDataUpdate = (ChatDataUpdate) chatData;
                if (chatDataUpdate.getMessage() != null) {
                    yield updateConversationMessage(chatDataUpdate, chatDataUpdate.getMessage());
                } else if (chatDataUpdate.getMarkedAsRead() != null) {
                    yield updateConversationReadMarker(chatDataUpdate, chatDataUpdate.getMarkedAsRead());
                } else if (chatDataUpdate.getChatterAdd() != null) {
                    yield updateConversationChatter(chatDataUpdate, chatDataUpdate.getChatterAdd());
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
                    .id(chatDataUpdate.getConversationId().getId().toString())
                    .data(SseChatPayload.SseChatConversationDataList.of(
                        SseChatPayload.SseChatConversationMessageData.builder()
                            .id(messageValue.getConversationDataId().getId().toString())
                            .dateTime(messageValue.getDateTime().toEpochSecond())
                            .from(messageValue.getFrom().getId().toString())
                            .message(messageValue.getMessage().getContent())
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
                    .id(chatDataUpdate.getConversationId().getId().toString())
                    .readMarkers(Map.of(markedAsReadValue.getBy().getId().toString(), markedAsReadValue.getAt().getId().toString()))
                    .build())
                .build())
            .build());
    }

    // LIVE
    private Flux<SseChatData> updateConversationChatter(ChatDataUpdate chatDataUpdate, ChatDataUpdate.ChatterAddValue chatterAddValue) {
        return Flux.just(SseChatData.builder()
            // .id()
            .event(CONVERSATION_ADD_CHATTER)
            .payload(SseChatPayload.builder()
                .conversation(SseChatConversation.builder()
                    .id(chatDataUpdate.getConversationId().getId().toString())
//                    .participants(List.of(chatterAddValue.getChatterId().getId()))
                    // TO DO: ⬆ or ⬇ or both ??
                    .data(SseChatPayload.SseChatConversationDataList.of(
                        SseChatPayload.SseChatConversationChatterData.builder()
                            .id(chatterAddValue.getConversationDataId().getId().toString())
                            .dateTime(chatterAddValue.getDateTime().toEpochSecond())
                            .from(chatterAddValue.getFrom().getId().toString())
                            .chatterId(chatterAddValue.getChatterId().getId().toString())
                            .build()
                    ))
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
                    .id(chatDataConversationInstall.getConversationId().getId().toString())
                    .creator(chatDataConversationInstall.getCreatedBy().getId().toString())
                    .createdAt(chatDataConversationInstall.getCreatedAt().toEpochSecond())
                    .name(chatDataConversationInstall.getName())
//                                .from(conversationData.get(0).getConversationDataId())
//                                .to(conversationData.get(conversationData.size() - 1).getConversationDataId())
                    .dialogue(chatDataConversationInstall.getDialogue())
                    .participants(chatDataConversationInstall.getChatters().stream().map(Chatter::getChatterID).map(ChatterId::getId).map(UUID::toString).toList())
                    .data(chatDataConversationInstall.getConversationData().stream()
                        .map(this::toSseChatConversationData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(SseChatPayload.SseChatConversationDataList::new)))
                    .readMarkers(chatDataConversationInstall.getReadMarkers().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId().toString(), e -> e.getValue().getId().toString())))
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
                    .id(chatDataConversationPartial.getConversationId().getId().toString())
//                                .from(conversationData.get(0).getConversationDataId())
//                                .to(conversationData.get(conversationData.size() - 1).getConversationDataId())
//                    .dialogue(chatDataConversationPartial.getDialogue())
                    .participants(chatDataConversationPartial.getChatters().stream().map(Chatter::getChatterID).map(ChatterId::getId).map(UUID::toString).toList())
                    .data(chatDataConversationPartial.getConversationData().stream()
                        .map(this::toSseChatConversationData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(SseChatPayload.SseChatConversationDataList::new)))
                    .readMarkers(chatDataConversationPartial.getReadMarkers().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId().toString(), e -> e.getValue().getId().toString())))
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
                        .id(chatter.getChatterID().getId().toString())
                        .name(chatter.getName())
                        .build())
                    .build())
                .build());
    }

    // COLD .data
    private SseChatPayload.SseChatConversationData toSseChatConversationData(ConversationData conversationData) {
        return switch (conversationData.getConversationDataType()) {
//            case CREATION, READ_MARKER:
            case CREATION:
                yield null;
            case MESSAGE:
                ConversationMessage conversationMessage = (ConversationMessage) conversationData;
                yield SseChatPayload.SseChatConversationMessageData.builder()
                    .id(conversationMessage.getConversationDataId().getId().toString())
                    .dateTime(conversationMessage.getDateTime().toEpochSecond())
                    .from(conversationMessage.getChatterId().getId().toString())
                    .message(conversationMessage.getMessage().getContent())
                    .build();
            case CHATTER_ADD:
                ConversationChatter conversationChatter = (ConversationChatter) conversationData;
                yield SseChatPayload.SseChatConversationChatterData.builder()
                    .id(conversationChatter.getConversationDataId().getId().toString())
                    .dateTime(conversationChatter.getDateTime().toEpochSecond())
                    .from(conversationChatter.getInvitedByChatterId().getId().toString())
                    .chatterId(conversationChatter.getChatterId().getId().toString())
                    .build();
        };
    }
}
