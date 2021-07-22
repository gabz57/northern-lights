package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatCommands {

    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public ChatAuthenticationCommand authenticate() {
        return new ChatAuthenticationCommand(this.chatterStore);
    }

    public CreateConversationCommand createConversation() {
        return new CreateConversationCommand(this.chatterStore, this.conversationStore, this.conversationEventPublisher);
    }

    public SendMessageCommand sendMessage() {
        return new SendMessageCommand(this.conversationStore, this.chatterStore, this.conversationEventPublisher);
    }

    public MarkConversationAsReadCommand markAsRead() {
        return new MarkConversationAsReadCommand(this.conversationStore, this.chatterStore, this.conversationEventPublisher);
    }

    // leave conversation
    // invite to conversation
}
