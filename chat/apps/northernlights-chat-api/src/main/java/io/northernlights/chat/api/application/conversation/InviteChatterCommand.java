package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.event.ChatterJoinedEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.api.application.conversation.InviteChatterCommand.InviteChatterCommandInput;
import static io.northernlights.chat.api.application.conversation.InviteChatterCommand.InviteChatterCommandResult;

@RequiredArgsConstructor
public class InviteChatterCommand implements UseCase<InviteChatterCommandInput, InviteChatterCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;
    private final ChatterStore chatterStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public Mono<InviteChatterCommandResult> execute(InviteChatterCommandInput input) {
        return conversationStore.addChatter(timeService.now().toOffsetDateTime(), input.conversationId, input.chatterId, input.invitedChatterId)
            .doOnNext(chatterStore::writeChatterJoined)
            .doOnNext(conversationEventPublisher::publish)
//            .flatMap(conversationUpdatedEvent -> conversationStore.markEventAsRead(timeService.now().toOffsetDateTime(), input.conversationId, input.chatterId, conversationUpdatedEvent.getConversationDataId())
//                .doOnNext(conversationEventPublisher::publish)
//                .thenReturn(conversationUpdatedEvent)
//            )
            .map(InviteChatterCommandResult::new);
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
        ChatterJoinedEvent chatterJoinedEvent;
    }
}
