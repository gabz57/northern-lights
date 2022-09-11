package io.northernlights.chat.api.infrastructure.client.http;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public class SseChatterDataAssert extends AbstractAssert<SseChatterDataAssert, SseChatPayload.SseChatChatter> {

    public static SseChatterDataAssert assertThat(SseChatPayload.SseChatChatter actual) {
        return new SseChatterDataAssert(actual);
    }

    public SseChatterDataAssert(SseChatPayload.SseChatChatter actual) {
        super(actual, SseChatterDataAssert.class);
    }

    public SseChatterDataAssert hasId(String id) {
        isNotNull();
        if (!Objects.equals(this.actual.getId(), id)) {
            failWithMessage("Expected chatter to have id %s but was %s",
                id, this.actual.getId());
        }
        return this;
    }
    public SseChatterDataAssert hasId(ChatterId id) {
        isNotNull();
        if (!Objects.equals(this.actual.getId(), id.getId().toString())) {
            failWithMessage("Expected chatter to have id %s but was %s",
                id, this.actual.getId());
        }
        return this;
    }

}
