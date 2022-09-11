package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import io.northernlights.commons.TimeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatCommands {

    private final TimeService timeService;
    private final ConversationStore conversationStore;
    private final SseKeyStore sseKeyStore;

    public InitializeSseChatCommand authenticate() {
        return new InitializeSseChatCommand(this.sseKeyStore);
    }

    public CreateConversationCommand createConversation() {
        return new CreateConversationCommand(this.timeService, this.conversationStore);
    }

    public SendMessageCommand sendMessage() {
        return new SendMessageCommand(this.timeService, this.conversationStore);
    }

    public MarkConversationAsReadCommand markAsRead() {
        return new MarkConversationAsReadCommand(this.timeService, this.conversationStore);
    }

    public InviteChatterCommand inviteChatter() {
        return new InviteChatterCommand(this.timeService, this.conversationStore);
    }

    // leave conversation
    // invite to conversation
}
