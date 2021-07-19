package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.domain.ConversationEventPublisher;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.model.conversation.event.ConversationMessageSentEvent;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandInput;
import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandResult;

@RequiredArgsConstructor
public class SendMessageCommand implements UseCase<SendMessageCommandInput, SendMessageCommandResult> {

    private final ConversationStore conversationStore;
    private final ChatterStore chatterStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public Mono<SendMessageCommandResult> execute(SendMessageCommandInput input) {
        return conversationStore.appendMessage(input.conversationID, input.chatterID, input.message)
            .doOnNext(conversationUpdatedEvent ->
                conversationStore.participants(input.conversationID)
                    .subscribe(participants -> chatterStore.writeConversationUpdate(conversationUpdatedEvent, participants)))
            .doOnNext(conversationEventPublisher::publish)
            .map(SendMessageCommandResult::new);
    }

    @Value
    @RequiredArgsConstructor
    public static class SendMessageCommandInput {
        ConversationId conversationID;
        ChatterId chatterID;
        Message message;
    }

    @Value
    @RequiredArgsConstructor
    public static class SendMessageCommandResult {
        ConversationMessageSentEvent conversationMessageSentEvent;
    }
}
