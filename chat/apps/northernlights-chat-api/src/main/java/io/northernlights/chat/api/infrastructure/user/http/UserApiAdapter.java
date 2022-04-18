package io.northernlights.chat.api.infrastructure.user.http;

import io.northernlights.chat.api.application.user.SubscribeUserCommand.SubscribeUserCommandInput;
import io.northernlights.chat.api.application.user.UserInfoQuery.UserInfoQueryInput;
import io.northernlights.chat.api.application.user.UserInfoQuery.UserInfoQueryResult;
import io.northernlights.chat.api.infrastructure.user.http.model.SubscribeUserResponse;
import io.northernlights.chat.api.infrastructure.user.http.model.UserInfoResponse;
import io.northernlights.security.NorthernLightsAuthentication;
import lombok.RequiredArgsConstructor;

import static io.northernlights.chat.api.application.user.SubscribeUserCommand.*;

@RequiredArgsConstructor
public class UserApiAdapter {

    public SubscribeUserCommandInput adaptSubscribeUser(NorthernLightsAuthentication authentication) {
        return SubscribeUserCommandInput.builder()
            .origin(authentication.getPrincipal().getExternalOrigin())
            .uid(authentication.getPrincipal().getExternalUid())
            .name(authentication.getPrincipal().getName())
            .picture(authentication.getPrincipal().getPicture())
            .build();
    }

    public SubscribeUserResponse adapt(SubscribeUserCommandResult result) {
        return SubscribeUserResponse.builder()
            .chatterId(result.getChatterId().getId().toString())
            .build();
    }

    public UserInfoQueryInput adaptUserInfo(NorthernLightsAuthentication authentication) {
        return UserInfoQueryInput.builder()
            .origin(authentication.getPrincipal().getExternalOrigin())
            .uid(authentication.getPrincipal().getUid())
            .build();
    }

    public UserInfoResponse adapt(UserInfoQueryResult result) {
        return UserInfoResponse.builder()
            .chatterId(result.getChatterId().getId().toString())
            .build();
    }
}
