package io.northernlights.chat.api.infrastructure.conversation.http;

import io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandInput;
import io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandResult;
import io.northernlights.chat.api.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandInput;
import io.northernlights.chat.api.infrastructure.conversation.http.model.*;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import static io.northernlights.chat.api.application.conversation.CreateConversationCommand.CreateConversationCommandInput;
import static io.northernlights.chat.api.application.conversation.CreateConversationCommand.CreateConversationCommandResult;
import static io.northernlights.chat.api.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandResult;
import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandInput;
import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandResult;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ChatApiAdapter {
    public CreateConversationCommandInput adapt(CreateConversationRequest request, String issuer) {
        return CreateConversationCommandInput.builder()
            .creator(new ChatterId(issuer))
            .conversationName(request.getName())
            .participants(request.getParticipants().stream().map(ChatterId::new).collect(Collectors.toList()))
            .dialogue(request.getDialogue())
            .build();
    }

    public CreateConversationResponse adapt(CreateConversationCommandResult result) {
        return CreateConversationResponse.builder()
            .conversationId(result.getConversationCreatedEvent().getConversationId().getId())
            .build();
    }

    public SendMessageCommandInput adapt(SendMessageRequest request, String issuer) {
        return SendMessageCommandInput.builder()
            .chatterID(new ChatterId(issuer))
            .conversationID(new ConversationId(request.getConversationId()))
            .message(new Message(request.getMessage()))
            .build();
    }

    public SendMessageResponse adapt(SendMessageCommandResult result) {
        return SendMessageResponse.builder()
            .conversationId(result.getConversationMessageSentEvent().getConversationId().getId())
            .conversationDataId(result.getConversationMessageSentEvent().getConversationDataId().getId())
            .build();
    }

    public MarkConversationAsReadCommandInput adapt(MarkAsReadRequest request, String issuer) {
        return MarkConversationAsReadCommandInput.builder()
            .chatterId(new ChatterId(issuer))
            .conversationId(new ConversationId(request.getConversationId()))
            .conversationDataId(new ConversationDataId(request.getConversationDataId()))
            .build();
    }

    public MarkAsReadResponse adapt(MarkConversationAsReadCommandResult result) {
        return MarkAsReadResponse.builder()
            .conversationId(result.getConversationMarkedAsReadEvent().getConversationId().getId())
            .conversationDataId(result.getConversationMarkedAsReadEvent().getConversationDataId().getId())
            .build();
    }

    public ChatAuthenticationCommandInput adapt(ChatAuthenticationRequest request, String issuer) {
        return ChatAuthenticationCommandInput.builder()
            .chatterId(new ChatterId(issuer))
            .conversationStatus(request.getConversationStatuses().entrySet().stream()
                .collect(toMap(e -> new ConversationId(e.getKey()), e -> new ConversationDataId(e.getValue()))))
            .build();
    }

    public ChatAuthenticationResponse adapt(ChatAuthenticationCommandResult result) {
        return ChatAuthenticationResponse.builder()
            .sseChatKey(result.getSseChatKey())
            .build();
    }
}
