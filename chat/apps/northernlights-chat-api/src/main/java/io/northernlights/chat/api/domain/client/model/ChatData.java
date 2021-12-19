package io.northernlights.chat.api.domain.client.model;

public interface ChatData {

    ChatDataType getChatDataType();

    enum ChatDataType {
        INSTALL,
        PARTIAL,
        LIVE_UPDATE
    }

}
