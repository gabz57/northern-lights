package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.Message;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.commons.TimeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import static io.northernlights.chat.domain.application.conversation.SendMessageCommand.SendMessageCommandInput;
import static io.northernlights.chat.domain.application.conversation.SendMessageCommand.SendMessageCommandResult;

@RequiredArgsConstructor
public class SendMessageCommand implements UseCase<SendMessageCommandInput, SendMessageCommandResult> {

    private final TimeService timeService;
    private final ConversationStore conversationStore;

    public Mono<SendMessageCommandResult> execute(SendMessageCommandInput input) {
        return conversationStore.appendMessage(input.conversationID, timeService.now().toOffsetDateTime(), input.chatterID, input.message)
            .map(ref -> new SendMessageCommandResult(ref.getConversationId(), ref.getConversationDataId()));
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
        ConversationId conversationId;
        ConversationDataId conversationDataId;
    }
}
