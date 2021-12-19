package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.domain.event.ConversationCreatedEvent;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.northernlights.chat.api.application.conversation.CreateConversationCommand.CreateConversationCommandInput;
import static io.northernlights.chat.api.application.conversation.CreateConversationCommand.CreateConversationCommandResult;

@RequiredArgsConstructor
public class CreateConversationCommand implements UseCase<CreateConversationCommandInput, CreateConversationCommandResult> {

    private final TimeService timeService;
    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;
    private final ConversationEventPublisher conversationEventPublisher;

    public Mono<CreateConversationCommandResult> execute(CreateConversationCommandInput input) {
        return conversationStore.create(timeService.now().toOffsetDateTime(), input.creator, input.conversationName, input.participants, input.dialogue)
            .doOnNext(chatterStore::writeConversationCreated)
            .doOnNext(conversationEventPublisher::publish)
            .map(CreateConversationCommandResult::new);
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class CreateConversationCommandInput {
        String conversationName;
        ChatterId creator;
        List<ChatterId> participants;
        Boolean dialogue;
    }

    @Value
    @RequiredArgsConstructor
    public static class CreateConversationCommandResult {
        ConversationCreatedEvent conversationCreatedEvent;
    }
}
