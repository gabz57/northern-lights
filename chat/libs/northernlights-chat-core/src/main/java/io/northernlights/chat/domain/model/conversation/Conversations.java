package io.northernlights.chat.domain.model.conversation;

import io.northernlights.chat.domain.model.conversation.data.ConversationData;

import java.util.ArrayList;
import java.util.Collection;

public interface Conversations {

    abstract class AbstractConversation extends ArrayList<ConversationData> {
        public AbstractConversation(Collection<? extends ConversationData> c) {
            super(c);
        }
    }

    class CompleteConversation extends AbstractConversation {
        public CompleteConversation(Collection<? extends ConversationData> c) {
            super(c);
        }
    }

    class PartialConversation extends AbstractConversation {
        public PartialConversation(Collection<? extends ConversationData> c) {
            super(c);
        }

    }
}
