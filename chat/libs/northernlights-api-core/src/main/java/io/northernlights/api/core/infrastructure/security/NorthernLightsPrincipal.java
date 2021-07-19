package io.northernlights.api.core.infrastructure.security;

import lombok.Builder;
import lombok.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
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
//
//    private static String getPartnerId(AuthorizationContext context) {
//        return (String) Optional.ofNullable(context.getVariables().get(PARTNER_ID)).orElse(null);
//    }
//
//    public static Mono<AuthorizationDecision> canReadCustomProducts(Mono<Authentication> authentication, AuthorizationContext context) {
//        return getAuthorizationDecision(authentication, principal ->
//            (principal.hasScopeWithCooperative(PRODUCTS_READ_COOPERATIVE, getPartnerId(context))
//                || principal.hasScope(PRODUCTS_READ_ALL))
//                // FIXME: get rid of this temp fix to mimic current situation
//                || (principal.hasScopeWithCooperative(PRODUCTS_MANAGE_COOPERATIVE, getPartnerId(context))
//                || principal.hasScope(PRODUCTS_MANAGE_ALL))
//        );
//    }
//
//    public static Mono<AuthorizationDecision> canManageCustomProducts(Mono<Authentication> authentication, AuthorizationContext context) {
//        return getAuthorizationDecision(authentication, principal ->
//            principal.hasScopeWithCooperative(PRODUCTS_MANAGE_COOPERATIVE, getPartnerId(context))
//                || principal.hasScope(PRODUCTS_MANAGE_ALL));
//    }
//
//    public static Mono<AuthorizationDecision> canManagePartnerIndex(Mono<Authentication> authentication, AuthorizationContext context) {
//        return getAuthorizationDecision(authentication, principal ->
//            principal.hasScope(PRODUCTS_IDX_MANAGE_COOPERATIVE) && principal.belongsToCooperative(getPartnerId(context))
//                || principal.hasScope(PRODUCTS_IDX_MANAGE_ALL));
//    }
//
//    public static Mono<AuthorizationDecision> canManageInvivoReferentialIndex(Mono<Authentication> authentication, AuthorizationContext context) {
//        return getAuthorizationDecision(authentication, principal ->
//            principal.hasScope(PRODUCTS_IDX_MANAGE_INVIVO) || principal.hasScope(PRODUCTS_IDX_MANAGE_ALL));
//    }
//
//    boolean hasScope(com.invivodigitalfactory.marketplace.product.api.infrastructure.security.InvivoScope invivoScope) {
//        return scopes.contains(invivoScope.scope);
//    }
//
//    boolean hasScopeWithCooperative(com.invivodigitalfactory.marketplace.product.api.infrastructure.security.InvivoScope invivoScope, String partnerId) {
//        return invivoRoles.stream()
//            .filter(invivoRole -> invivoRole.isForPartner(partnerId))
//            .anyMatch(role -> role.getPermissions().contains(invivoScope.scope));
//    }
//
//    boolean belongsToCooperative(String partnerId) {
//        return partnerId != null && invivoRoles.stream()
//            .anyMatch(role -> role.getType().equals(ROLE_SELLER.type)
//                && role.getProfiles().stream()
//                .map(InvivoRoles.InvivoProfile::getCooperative)
//                .map(InvivoRoles.InvivoCooperative::getId)
//                .anyMatch(cooperativeId -> partnerId.equals(cooperativeId.toString())));
//    }
}
