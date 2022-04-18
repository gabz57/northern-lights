package io.northernlights.security.jwt;

import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.domain.model.user.NorthernLightsUser;
import io.northernlights.security.NorthernLightsPrincipal;
import io.northernlights.security.NorthernLightsRoles;
import io.northernlights.security.NorthernLightsScopes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.northernlights.security.NorthernLightsRoles.ROLE_CHATTER;

public class JwtTokenNorthernLightsAuthenticationAdapter {

    public NorthernLightsPrincipal buildNorthernLightsPrincipal(JwtToken jwtToken, NorthernLightsUser user) {
        return NorthernLightsPrincipal.builder()
            .uid(jwtToken.getUid())
            .roles(new NorthernLightsRoles(List.of(NorthernLightsRoles.Role.builder().type(ROLE_CHATTER).build())))
            .chatterId(user.getChatterId())
            .scopes(userScopes())
            .externalUid(jwtToken.getUid())
            .externalOrigin(AuthOrigin.GOOGLE)
            .picture(readPicture(jwtToken))
            .build();
    }

    public NorthernLightsPrincipal buildNotSubscribedUser(JwtToken jwtToken) {
        return NorthernLightsPrincipal.builder()
            .uid(jwtToken.getUid())
            .roles(new NorthernLightsRoles())
            .chatterId(null)
            .scopes(Collections.emptyList())
            .externalUid(jwtToken.getUid())
            .externalOrigin(AuthOrigin.GOOGLE)
            .picture(readPicture(jwtToken))
            .build();
    }

    private String readPicture(JwtToken jwtToken) {
        return jwtToken.readClaim("picture", String.class, null);
    }

    private List<String> userScopes() {
        return Arrays.stream(NorthernLightsScopes.values())
            .map(NorthernLightsScopes::getScope)
            .collect(Collectors.toList());
    }
//    private NorthernLightsRoles readRoles(JwtToken jwtToken) {
//        return jwtToken.readClaim("https://northernlights.io/v1/roles", NorthernLightsRoles.class, new NorthernLightsRoles());

//    }
//
//    private List<String> readScopes(JwtToken jwtToken) {
//        return Arrays.stream((jwtToken.readClaim("scope", String.class, "").split(" ")))
//            .filter(s -> !s.isEmpty())
//            .distinct()
//            .toList();
//    }
}
