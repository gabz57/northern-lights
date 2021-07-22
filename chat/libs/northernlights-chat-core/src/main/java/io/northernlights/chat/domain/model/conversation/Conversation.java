package io.northernlights.chat.domain.model.conversation;

import io.northernlights.chat.domain.model.conversation.data.ConversationData;

import java.util.ArrayList;
import java.util.Collection;

public class Conversation extends ArrayList<ConversationData> {
    public Conversation() {
    }

    public Conversation(Collection<? extends ConversationData> c) {
        super(c);
    }
}
