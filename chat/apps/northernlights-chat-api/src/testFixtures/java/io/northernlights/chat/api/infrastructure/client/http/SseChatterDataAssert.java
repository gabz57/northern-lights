package io.northernlights.chat.api.infrastructure.client.http;

import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public class SseChatterDataAssert extends AbstractAssert<SseChatterDataAssert, SseChatPayload.SseChatChatter> {

    public static SseChatterDataAssert assertThat(SseChatPayload.SseChatChatter actual) {
        return new SseChatterDataAssert(actual);
    }

    public SseChatterDataAssert(SseChatPayload.SseChatChatter actual) {
        super(actual, SseChatterDataAssert.class);
    }

    public SseChatterDataAssert hasName(String name) {
        isNotNull();
        if (!Objects.equals(this.actual.getName(), name)) {
            failWithMessage("Expected chatter to have name %s but was %s",
                name, this.actual.getName());
        }
        return this;
    }

    public SseChatterDataAssert hasId(String id) {
        isNotNull();
        if (!Objects.equals(this.actual.getId(), id)) {
            failWithMessage("Expected chatter to have id %s but was %s",
                id, this.actual.getName());
        }
        return this;
    }

}
