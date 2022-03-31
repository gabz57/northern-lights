package io.northernlights.chat.api.infrastructure.client.http;

import org.assertj.core.api.AbstractAssert;

import java.util.Map;

public class SseConversationReadMarkerAssert extends AbstractAssert<SseConversationReadMarkerAssert, Map<String, String>> {
    public static SseConversationReadMarkerAssert assertThat(Map<String, String> actual) {
        return new SseConversationReadMarkerAssert(actual);
    }

    public SseConversationReadMarkerAssert(Map<String, String> actual) {
        super(actual, SseConversationReadMarkerAssert.class);
    }

}
