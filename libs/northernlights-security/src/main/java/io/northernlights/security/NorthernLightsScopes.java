package io.northernlights.security;

import lombok.Getter;

@Getter
public enum NorthernLightsScopes {

    // Chatter scopes
    CHATTER_READ_SELF(Scope.CHATTER_READ_SELF_VALUE, "May read chatter information"),
    CHATTER_WRITE_SELF(Scope.CHATTER_WRITE_SELF_VALUE, "May edit chatter information"),

    // Conversation scopes
    CONVERSATION_CREATE(Scope.CONVERSATION_CREATE_SELF_VALUE, "May create a conversation including current chatter"),
    CONVERSATION_READ(Scope.CONVERSATION_READ_SELF_VALUE, "May read a conversation including current chatter"),
    CONVERSATION_WRITE(Scope.CONVERSATION_WRITE_SELF_VALUE, "May write a conversation including current chatter");

    public final String scope;
    public final String description;

    NorthernLightsScopes(String scope, String description) {
        this.scope = scope;
        this.description = description;
    }

    public static class Scope {
        public static final String CHATTER_READ_SELF_VALUE = "chatter:read:self";
        public static final String CHATTER_WRITE_SELF_VALUE = "chatter:write:self";
        public static final String CONVERSATION_CREATE_SELF_VALUE = "conversation:create:self";
        public static final String CONVERSATION_READ_SELF_VALUE = "conversation:read:self";
        public static final String CONVERSATION_WRITE_SELF_VALUE = "conversation:write:self";
    }
}
