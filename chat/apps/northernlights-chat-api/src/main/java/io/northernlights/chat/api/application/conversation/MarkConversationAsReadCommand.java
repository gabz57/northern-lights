package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.event.ConversationMarkedAsReadEvent;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import lombok.Builder;
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
        return conversationStore.markEventAsRead(input.conversationId, input.chatterId, input.conversationDataId)
//            .zipWhen(conversationUpdatedEvent -> conversationStore.participants(input.conversationID)
//                    .map(participants -> chatterStore.writeConversationUpdate(conversationUpdatedEvent, participants)), (t1,t2) -> t1)
            .zipWhen(conversationEventPublisher::publish, (t1,t2) -> t1)
            .map(MarkConversationAsReadCommandResult::new);
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class MarkConversationAsReadCommandInput {
        ChatterId chatterId;
        ConversationId conversationId;
        ConversationDataId conversationDataId;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class MarkConversationAsReadCommandResult {
        ConversationMarkedAsReadEvent conversationMarkedAsReadEvent;
    }
}
