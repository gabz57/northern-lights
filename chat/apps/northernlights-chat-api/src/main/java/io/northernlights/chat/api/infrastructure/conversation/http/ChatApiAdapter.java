package io.northernlights.chat.api.infrastructure.conversation.http;

import io.northernlights.chat.domain.application.conversation.InitializeSseChatCommand.InitializeSseChatCommandInput;
import io.northernlights.chat.domain.application.conversation.InitializeSseChatCommand.InitializeSseChatCommandResult;
import io.northernlights.chat.domain.application.conversation.InviteChatterCommand.InviteChatterCommandInput;
import io.northernlights.chat.domain.application.conversation.InviteChatterCommand.InviteChatterCommandResult;
import io.northernlights.chat.domain.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandInput;
import io.northernlights.chat.api.infrastructure.conversation.http.model.*;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.security.NorthernLightsAuthentication;
import lombok.RequiredArgsConstructor;

import static io.northernlights.chat.domain.application.conversation.CreateConversationCommand.CreateConversationCommandInput;
import static io.northernlights.chat.domain.application.conversation.CreateConversationCommand.CreateConversationCommandResult;
import static io.northernlights.chat.domain.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandResult;
import static io.northernlights.chat.domain.application.conversation.SendMessageCommand.SendMessageCommandInput;
import static io.northernlights.chat.domain.application.conversation.SendMessageCommand.SendMessageCommandResult;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ChatApiAdapter {
    public CreateConversationCommandInput adapt(CreateConversationRequest request, NorthernLightsAuthentication authentication) {
        return CreateConversationCommandInput.builder()
            .creator(authentication.getPrincipal().getChatterId())
            .conversationName(request.getName())
            .participants(request.getParticipants().stream().map(ChatterId::of).toList())
            .isPrivate(request.getDialogue())
            .build();
    }

    public CreateConversationResponse adapt(CreateConversationCommandResult result) {
        return CreateConversationResponse.builder()
            .conversationId(result.getConversationId().getId().toString())
            .build();
    }

    public SendMessageCommandInput adapt(SendMessageRequest request, NorthernLightsAuthentication authentication) {
        return SendMessageCommandInput.builder()
            .chatterID(authentication.getPrincipal().getChatterId())
            .conversationID(ConversationId.of(request.getConversationId()))
            .message(new Message(request.getMessage()))
            .build();
    }

    public SendMessageResponse adapt(SendMessageCommandResult result) {
        return SendMessageResponse.builder()
            .conversationId(result.getConversationId().getId().toString())
            .conversationDataId(result.getConversationDataId().getId())
            .build();
    }

    public MarkConversationAsReadCommandInput adapt(MarkAsReadRequest request, NorthernLightsAuthentication authentication) {
        return MarkConversationAsReadCommandInput.builder()
            .chatterId(authentication.getPrincipal().getChatterId())
            .conversationId(ConversationId.of(request.getConversationId()))
            .conversationDataId(ConversationDataId.of(request.getConversationDataId()))
            .build();
    }

    public MarkAsReadResponse adapt(MarkConversationAsReadCommandResult result) {
        return MarkAsReadResponse.builder()
            .conversationId(result.getConversationId().getId().toString())
            .conversationDataId(result.getConversationDataId().getId())
            .build();
    }

    public InviteChatterCommandInput adapt(InviteChatterRequest request, NorthernLightsAuthentication authentication) {
        return InviteChatterCommandInput.builder()
            .chatterId(authentication.getPrincipal().getChatterId())
            .conversationId(ConversationId.of(request.getConversationId()))
            .invitedChatterId(ChatterId.of(request.getChatterId()))
            .build();
    }

    public InviteChatterResponse adapt(InviteChatterCommandResult result) {
        return InviteChatterResponse.builder()
            .conversationId(result.getConversationId().getId().toString())
            .conversationDataId(result.getConversationDataId().getId())
            .build();
    }

    public InitializeSseChatCommandInput adapt(InitializeSseChatRequest request, NorthernLightsAuthentication authentication) {
        return InitializeSseChatCommandInput.builder()
            .chatterId(authentication.getPrincipal().getChatterId())
            .conversationStatus(request.getConversationStatuses().entrySet().stream()
                .collect(toMap(e -> ConversationId.of(e.getKey()), e -> ConversationDataId.of(e.getValue()))))
            .build();
    }

    public InitializeSseChatResponse adapt(InitializeSseChatCommandResult result) {
        return InitializeSseChatResponse.builder()
            .sseChatKey(result.getSseChatKey().getId().toString())
            .build();
    }
}
