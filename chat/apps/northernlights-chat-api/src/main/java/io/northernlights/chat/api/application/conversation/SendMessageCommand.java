package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.event.ConversationMessageSentEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandInput;
import static io.northernlights.chat.api.application.conversation.SendMessageCommand.SendMessageCommandResult;

@RequiredArgsConstructor
public class SendMessageCommand implements UseCase<SendMessageCommandInput, SendMessageCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;
    private final ChatterStore chatterStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public Mono<SendMessageCommandResult> execute(SendMessageCommandInput input) {
        return conversationStore.appendMessage(timeService.now().toOffsetDateTime(), input.conversationID, input.chatterID, input.message)
//            .doOnNext(conversationUpdatedEvent ->
//                conversationStore.participants(input.conversationID)
//                    .map(participants -> chatterStore.writeConversationUpdate(conversationUpdatedEvent, participants)))
            .doOnNext(conversationEventPublisher::publish)
            .flatMap(conversationUpdatedEvent -> conversationStore.markEventAsRead(timeService.now().toOffsetDateTime(), input.conversationID, input.chatterID, conversationUpdatedEvent.getConversationDataId())
                .doOnNext(conversationEventPublisher::publish)
                .thenReturn(conversationUpdatedEvent)
            )
            .map(SendMessageCommandResult::new);
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class SendMessageCommandInput {
        ConversationId conversationID;
        ChatterId chatterID;
        Message message;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class SendMessageCommandResult {
        ConversationMessageSentEvent conversationMessageSentEvent;
    }
}
