package io.northernlights.chat.domain.model.conversation;

import io.northernlights.commons.IdGenerator;

public interface ConversationIdGenerator extends IdGenerator<ConversationId> {
    ConversationId generate();
}
