package io.northernlights.chat.api.infrastructure.client.http;

import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public class SseConversationDataAssert extends AbstractAssert<SseConversationDataAssert, SseChatPayload.SseChatConversation> {

    public static SseConversationDataAssert assertThat(SseChatPayload.SseChatConversation actual) {
        return new SseConversationDataAssert(actual);
    }

    public SseConversationDataAssert(SseChatPayload.SseChatConversation actual) {
        super(actual, SseConversationDataAssert.class);
    }

    public SseConversationReadMarkerAssert withReadMarkers() {
        isNotNull();
        if (this.actual.getReadMarkers() == null) {
            failWithMessage("Expected conversation to have read markers but was null");
        }
        return SseConversationReadMarkerAssert.assertThat(this.actual.getReadMarkers());
    }

    public SseConversationDataAssert hasCreator(String creator) {
        isNotNull();
        if (!Objects.equals(this.actual.getCreator(), creator)) {
            failWithMessage("Expected conversation to have been created by %s but was %s",
                creator, this.actual.getCreator());
        }
        return this;
    }

    public SseConversationDataAssert hasDialogue(Boolean dialogue) {
        isNotNull();
        if (!Objects.equals(this.actual.getDialogue(), dialogue)) {
            failWithMessage("Expected conversation to have dialogue %s but was %s",
                dialogue, this.actual.getDialogue());
        }
        return this;
    }

    public SseConversationDataAssert hasName(String name) {
        isNotNull();
        if (!Objects.equals(this.actual.getName(), name)) {
            failWithMessage("Expected conversation to have name %s but was %s",
                name, this.actual.getName());
        }
        return this;
    }
}
