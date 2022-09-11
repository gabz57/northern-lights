package io.northernlights.chat.domain.event.file;

import io.northernlights.chat.domain.event.ChatEvent;

public interface FileEvent extends ChatEvent {
    String getFileId();

    Type getType();
    enum Type implements ChatEventType {
        FILE_CREATED
    }
}
