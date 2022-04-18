package io.northernlights.security;

import io.northernlights.security.jwt.JwtToken;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static java.util.stream.Collectors.toList;

@EqualsAndHashCode(callSuper = true)
public final class NorthernLightsAuthentication extends AbstractAuthenticationToken implements AuthenticatedPrincipal {

    private final NorthernLightsPrincipal northernLightsPrincipal;
    private transient JwtToken jwtToken;

    public NorthernLightsAuthentication(NorthernLightsPrincipal northernLightsPrincipal, JwtToken jwtToken) {
        super(grantedAuthorities(northernLightsPrincipal));
        this.northernLightsPrincipal = northernLightsPrincipal;
        this.jwtToken = jwtToken;
        super.setAuthenticated(true); // must use super, as we override
    }

    private static List<GrantedAuthority> grantedAuthorities(NorthernLightsPrincipal northernLightsPrincipal) {
        return northernLightsPrincipal.getRoles().stream()
            .map(NorthernLightsRoles.Role::getType)
            .map(NorthernLightsRoles.RoleType::getType)
            .distinct()
            /**
             *  {@link org.springframework.security.authorization.AuthorityReactiveAuthorizationManager#hasRole(String)}
             */
            .map(role -> "ROLE_" + role) // Spring Security
            .map(SimpleGrantedAuthority::new)
            .collect(toList());
    }

    @Override
    public String getName() {
        return northernLightsPrincipal.getUid();
    }

    @Override
    public JwtToken getCredentials() {
        return jwtToken;
    }

    @Override
    public NorthernLightsPrincipal getPrincipal() {
        return northernLightsPrincipal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        jwtToken = null;
    }
}
