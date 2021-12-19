package io.northernlights.security;

import lombok.Builder;
import lombok.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

@Builder
@Value
public class NorthernLightsPrincipal implements Serializable {
    String uid;
    List<String> scopes;
//    NorthernLightsRoles roles;

    private static Mono<AuthorizationDecision> getAuthorizationDecision(Mono<Authentication> authentication, Function<NorthernLightsPrincipal, Boolean> decisionCriteria) {
        return authentication
            .map(Authentication::getPrincipal)
            .cast(NorthernLightsPrincipal.class)
            .map(decisionCriteria)
            .map(AuthorizationDecision::new);
    }

    public static Mono<AuthorizationDecision> canCreateConversation(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
//        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_CREATE));
        return getAuthorizationDecision(authentication, principal -> true);
    }

    public static Mono<AuthorizationDecision> canSendMessage(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
//        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_WRITE));
        return getAuthorizationDecision(authentication, principal -> true);
    }

    public static Mono<AuthorizationDecision> canMarkAsRead(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
//        return getAuthorizationDecision(authentication, principal -> principal.hasScope(NorthernLightsScopes.CONVERSATION_WRITE));
        return getAuthorizationDecision(authentication, principal -> true);
    }

    boolean hasScope(NorthernLightsScopes northernLightsScopes) {
        return scopes.contains(northernLightsScopes.scope);
    }
}
