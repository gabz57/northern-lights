package io.northernlights.chat.api.infrastructure.client.http;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractAssert;
import org.springframework.http.codec.ServerSentEvent;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class ServerSentEventAssert extends AbstractAssert<ServerSentEventAssert, ServerSentEvent<SseChatPayload>> {

    public static ServerSentEventAssert assertThat(ServerSentEvent<SseChatPayload> actual) {
        log.info("{}", actual);
        return new ServerSentEventAssert(actual);
    }

    public ServerSentEventAssert(ServerSentEvent<SseChatPayload> actual) {
        super(actual, ServerSentEventAssert.class);
    }

    // assertion methods described later
    public ServerSentEventAssert hasId(String id) {
        isNotNull();
        if (!Objects.equals(this.actual.id(), id)) {
            failWithMessage("Expected ServerSentEvent to have id %s but was %s", id, this.actual.id());
        }
        return this;
    }

    public ServerSentEventAssert hasEvent(String event) {
        isNotNull();
        if (!Objects.equals(this.actual.event(), event)) {
            failWithMessage("Expected ServerSentEvent to have event %s but was %s", event, this.actual.event());
        }
        return this;
    }

    public ServerSentEventAssert hasComment(String comment) {
        isNotNull();
        if (!Objects.equals(this.actual.comment(), comment)) {
            failWithMessage("Expected ServerSentEvent to have comment %s but was %s", comment, this.actual.comment());
        }
        return this;
    }

    public ServerSentEventAssert hasRetry(Duration retry) {
        isNotNull();
        if (!Objects.equals(this.actual.retry(), retry)) {
            failWithMessage("Expected ServerSentEvent to have retry %s but was %s", retry, this.actual.retry());
        }
        return this;
    }

    public SseChatPayloadAssert hasData() {
        isNotNull();
        if (this.actual.data() == null) {
            failWithMessage("Expected ServerSentEvent to have data but was null");
        }
        return SseChatPayloadAssert.assertThat(this.actual.data());
    }
}
