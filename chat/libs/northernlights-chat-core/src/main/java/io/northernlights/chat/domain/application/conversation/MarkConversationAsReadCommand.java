package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.domain.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandInput;
import static io.northernlights.chat.domain.application.conversation.MarkConversationAsReadCommand.MarkConversationAsReadCommandResult;

@RequiredArgsConstructor
public class MarkConversationAsReadCommand implements UseCase<MarkConversationAsReadCommandInput, MarkConversationAsReadCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;

    public Mono<MarkConversationAsReadCommandResult> execute(MarkConversationAsReadCommandInput input) {
        return conversationStore.markEventAsRead(input.conversationId, timeService.now().toOffsetDateTime(), input.chatterId, input.conversationDataId)
            .map((ref) -> new MarkConversationAsReadCommandResult(ref.getConversationId(), ref.getConversationDataId()));
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
        ConversationId conversationId;
        ConversationDataId conversationDataId;
    }
}
