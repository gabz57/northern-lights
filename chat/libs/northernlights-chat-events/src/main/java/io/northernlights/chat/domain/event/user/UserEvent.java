package io.northernlights.chat.domain.event.user;

import io.northernlights.chat.domain.event.ChatEvent;

public interface UserEvent extends ChatEvent {
    String getUserId();

    Type getType();
    enum Type implements ChatEventType {
        USER_CREATED
    }
}
