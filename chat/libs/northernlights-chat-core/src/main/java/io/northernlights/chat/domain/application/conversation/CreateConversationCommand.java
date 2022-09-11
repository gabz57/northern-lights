package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static io.northernlights.chat.domain.application.conversation.CreateConversationCommand.CreateConversationCommandInput;
import static io.northernlights.chat.domain.application.conversation.CreateConversationCommand.CreateConversationCommandResult;

@RequiredArgsConstructor
public class CreateConversationCommand implements UseCase<CreateConversationCommandInput, CreateConversationCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;

    public Mono<CreateConversationCommandResult> execute(CreateConversationCommandInput input) {
        OffsetDateTime creationTime = timeService.now().toOffsetDateTime();
        return conversationStore.create(creationTime, input.creator, input.conversationName, input.participants, input.isPrivate)
            .map(ref -> new CreateConversationCommandResult(ref.getConversationId()));
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class CreateConversationCommandInput {
        String conversationName;
        ChatterId creator;
        List<ChatterId> participants;
        Boolean isPrivate;
    }

    @Value
    @RequiredArgsConstructor
    public static class CreateConversationCommandResult {
        ConversationId conversationId;
    }
}
