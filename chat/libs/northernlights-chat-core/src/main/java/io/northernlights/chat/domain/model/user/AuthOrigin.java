package io.northernlights.chat.domain.model.user;

import java.util.Arrays;

public enum AuthOrigin {
    GOOGLE("GOOGLE_SUB");

    private final String originKey;

    AuthOrigin(String originKey) {
        this.originKey = originKey;
    }

    public static AuthOrigin valueFromKey(String key) {
        return Arrays.stream(AuthOrigin.values())
            .filter(ao -> ao.getOriginKey().equals(key))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("No AuthOrigin using key '" + key + "'"));
    }

    public String getOriginKey() {
        return originKey;
    }
}
