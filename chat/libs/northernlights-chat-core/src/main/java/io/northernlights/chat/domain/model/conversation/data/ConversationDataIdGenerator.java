package io.northernlights.chat.domain.model.conversation.data;

import io.northernlights.commons.IdGenerator;

public interface ConversationDataIdGenerator extends IdGenerator<ConversationDataId> {
    ConversationDataId generate();
}
