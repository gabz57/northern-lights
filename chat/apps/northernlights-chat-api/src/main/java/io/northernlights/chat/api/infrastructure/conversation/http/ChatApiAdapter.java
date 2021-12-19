package io.northernlights.chat.api.infrastructure.conversation.http;

import io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandInput;
import io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandResult;
import io.northernlights.chat.api.application.conversation.InviteChatterCommand.InviteChatterCommandInput;
import io.northernlights.chat.api.application.conversation.InviteChatterCommand.InviteChatterCommandResult;
import io.northernlights.chat.api.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandInput;
import io.northernlights.chat.api.infrastructure.conversation.http.model.*;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import lombok.RequiredArgsConstructor;

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
            .creator(ChatterId.of(issuer))
            .conversationName(request.getName())
            .participants(request.getParticipants().stream().map(ChatterId::of).toList())
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
            .chatterID(ChatterId.of(issuer))
            .conversationID(ConversationId.of(request.getConversationId()))
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
            .chatterId(ChatterId.of(issuer))
            .conversationId(ConversationId.of(request.getConversationId()))
            .conversationDataId(ConversationDataId.of(request.getConversationDataId()))
            .build();
    }

    public MarkAsReadResponse adapt(MarkConversationAsReadCommandResult result) {
        return MarkAsReadResponse.builder()
            .conversationId(result.getConversationMarkedAsReadEvent().getConversationId().getId())
            .conversationDataId(result.getConversationMarkedAsReadEvent().getConversationDataId().getId())
            .build();
    }

    public InviteChatterCommandInput adapt(InviteChatterRequest request, String issuer) {
        return InviteChatterCommandInput.builder()
            .chatterId(ChatterId.of(issuer))
            .conversationId(ConversationId.of(request.getConversationId()))
            .invitedChatterId(ChatterId.of(request.getChatterId()))
            .build();
    }

    public InviteChatterResponse adapt(InviteChatterCommandResult result) {
        return InviteChatterResponse.builder()
            .conversationId(result.getChatterJoinedEvent().getConversationId().getId())
            .conversationDataId(result.getChatterJoinedEvent().getConversationDataId().getId())
            .build();
    }

    public ChatAuthenticationCommandInput adapt(ChatAuthenticationRequest request, String issuer) {
        return ChatAuthenticationCommandInput.builder()
            .chatterId(ChatterId.of(issuer))
            .conversationStatus(request.getConversationStatuses().entrySet().stream()
                .collect(toMap(e -> ConversationId.of(e.getKey()), e -> ConversationDataId.of(e.getValue()))))
            .build();
    }

    public ChatAuthenticationResponse adapt(ChatAuthenticationCommandResult result) {
        return ChatAuthenticationResponse.builder()
            .sseChatKey(result.getSseChatKey())
            .build();
    }
}
