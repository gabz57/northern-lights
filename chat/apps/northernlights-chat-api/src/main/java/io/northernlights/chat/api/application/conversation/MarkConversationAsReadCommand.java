package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.domain.ConversationEventPublisher;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.api.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandInput;
import static io.northernlights.chat.api.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandResult;

@RequiredArgsConstructor
public class MarkConversationAsReadCommand implements UseCase<MarkConversationAsReadCommandInput, MarkConversationAsReadCommandResult> {

    private final ConversationStore conversationStore;
    private final ChatterStore chatterStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public Mono<MarkConversationAsReadCommandResult> execute(MarkConversationAsReadCommandInput input) {
        return conversationStore.markEventAsRead(input.conversationID, input.chatterID, input.conversationDataID)
            .doOnNext(conversationUpdatedEvent ->
                conversationStore.participants(input.conversationID)
                    .subscribe(participants -> chatterStore.writeConversationUpdate(conversationUpdatedEvent, participants)))
            .doOnNext(conversationEventPublisher::publish)
            .map(MarkConversationAsReadCommandResult::new);
    }

    @Value
    @RequiredArgsConstructor
    public static class MarkConversationAsReadCommandInput {
        ChatterId chatterID;
        ConversationId conversationID;
        ConversationDataId conversationDataID;
    }

    @Value
    @RequiredArgsConstructor
    public static class MarkConversationAsReadCommandResult {
        ConversationMarkedAsReadEvent conversationMarkedAsReadEvent;
    }
}
