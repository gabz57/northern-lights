package io.northernlights.chat.domain.application.user;

import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.user.UserStore;
import io.northernlights.commons.TimeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCommands {

    private final TimeService timeService;
    private final UserStore userStore;
    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;

    public SubscribeUserCommand subscribeUser() {
        return new SubscribeUserCommand(timeService, userStore, chatterStore, conversationStore);
    }

    public UserInfoQuery userInfo() {
        return new UserInfoQuery(userStore);
    }
}
