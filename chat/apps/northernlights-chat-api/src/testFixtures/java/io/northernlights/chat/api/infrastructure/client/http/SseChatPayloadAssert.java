package io.northernlights.chat.api.infrastructure.client.http;

import org.assertj.core.api.AbstractObjectAssert;

public class SseChatPayloadAssert extends AbstractObjectAssert<SseChatPayloadAssert, SseChatPayload> {

    public static SseChatPayloadAssert assertThat(SseChatPayload actual) {
        return new SseChatPayloadAssert(actual);
    }

    public SseChatPayloadAssert(SseChatPayload actual) {
        super(actual, SseChatPayloadAssert.class);
    }

    public SseConversationDataAssert withConversationData() {
        isNotNull();
        if (this.actual.getConversation() == null) {
            failWithMessage("Expected payload to be conversation but was %s", findDataType());
        }
        return SseConversationDataAssert.assertThat(this.actual.getConversation());
    }

    public SseChatterDataAssert withChatterData() {
        isNotNull();
        if (this.actual.getChatter() == null) {
            failWithMessage("Expected payload to be chatter but was %s", findDataType());
        }
        return SseChatterDataAssert.assertThat(this.actual.getChatter());
    }

    private String findDataType() {
        isNotNull();
        if (this.actual.getConversation() != null) {
            return "conversation";
        } else if (this.actual.getChatter() != null) {
            return "chatter";
        }
        return "not";
    }
}
