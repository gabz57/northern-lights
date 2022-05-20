package io.northernlights.chat.domain;

public class ApiConstants {
    public static final String USER_API = "/v1/user/api";
    public static final String USER_API_INFO = USER_API + "/info";
    public static final String USER_API_SUBSCRIBE = USER_API + "/subscribe";

    public static final String CHAT_API = "/v1/chat/api";
    public static final String CHAT_API_INITIALIZE_SSE = CHAT_API + "/init-sse";
    public static final String CHAT_API_OPEN = CHAT_API + "/open";
    public static final String CHAT_API_MARK_AS_READ = CHAT_API + "/mark-as-read";
    public static final String CHAT_API_MESSAGE = CHAT_API + "/message";
    public static final String CHAT_API_INVITE_CHATTER = CHAT_API + "/invite-chatter";
    public static final String CHAT_API_ALL = CHAT_API + "/**";
    public static final String CHAT_API_SSE = CHAT_API + "/sse";

}
