package io.northernlights.security;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.user.AuthOrigin;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@Builder
@Value
public class NorthernLightsPrincipal implements Principal, Serializable {
    String uid;
    String name;
    List<String> scopes;
    ChatterId chatterId;
    NorthernLightsRoles roles;
    String picture;
    String externalUid;
    AuthOrigin externalOrigin;

    public String getName() {
        return uid;
    }

    public String getUsername() {
        return name;
    }
//
//    private static Mono<AuthorizationDecision> getAuthorizationDecision(Mono<Authentication> authentication, Function<NorthernLightsPrincipal, Boolean> decisionCriteria) {
//        return authentication
//            .map(Authentication::getPrincipal)
//            .cast(NorthernLightsPrincipal.class)
//            .map(decisionCriteria)
//            .map(AuthorizationDecision::new);
//    }
//
//    public static Mono<AuthorizationDecision> canCreateConversation(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
////        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_CREATE));
//        return getAuthorizationDecision(authentication, principal -> true);
//    }
//
//    public static Mono<AuthorizationDecision> canSendMessage(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
////        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_WRITE));
//        return getAuthorizationDecision(authentication, principal -> true);
//    }
//
//    public static Mono<AuthorizationDecision> canMarkAsRead(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
////        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_WRITE));
//        return getAuthorizationDecision(authentication, principal -> true);
//    }
//
//    boolean hasScope(NorthernLightsScopes northernLightsScopes) {
//        return scopes.contains(northernLightsScopes.scope);
//    }
}
