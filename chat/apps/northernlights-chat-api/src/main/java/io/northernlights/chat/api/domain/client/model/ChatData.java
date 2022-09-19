package io.northernlights.chat.api.domain.client.model;

public interface ChatData {

    ChatDataType getChatDataType();

    enum ChatDataType {
        HELLO,
        CHATTERS,
        CONVERSATION_INSTALL,
        CONVERSATION_PARTIAL,
        LIVE_UPDATE
    }

}
