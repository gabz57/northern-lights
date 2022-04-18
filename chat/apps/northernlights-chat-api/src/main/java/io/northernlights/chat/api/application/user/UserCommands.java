package io.northernlights.chat.api.application.user;

import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.user.UserStore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCommands {

    private final UserStore userStore;
    private final ChatterStore chatterStore;

    public SubscribeUserCommand subscribeUser() {
        return new SubscribeUserCommand(userStore, chatterStore);
    }

    public UserInfoQuery userInfo() {
        return new UserInfoQuery(userStore);
    }
}
