package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.domain.application.conversation.InviteChatterCommand.InviteChatterCommandInput;
import static io.northernlights.chat.domain.application.conversation.InviteChatterCommand.InviteChatterCommandResult;

@RequiredArgsConstructor
public class InviteChatterCommand implements UseCase<InviteChatterCommandInput, InviteChatterCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;

    public Mono<InviteChatterCommandResult> execute(InviteChatterCommandInput input) {
        // note: this currently add a chatter without need to accept any invitation
        return conversationStore.addChatter(input.conversationId, timeService.now().toOffsetDateTime(), input.chatterId, input.invitedChatterId)
            .map(ref -> new InviteChatterCommandResult(ref.getConversationId(), ref.getConversationDataId()));
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class InviteChatterCommandInput {
        ChatterId chatterId;
        ConversationId conversationId;
        ChatterId invitedChatterId;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class InviteChatterCommandResult {
        ConversationId conversationId;
        ConversationDataId conversationDataId;
    }
}
