package io.northernlights.api.core.infrastructure.security.jwt;


import io.northernlights.api.core.infrastructure.security.NorthernLightsPrincipal;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JwtTokenNorthernLightsAuthenticationAdapter {

    public NorthernLightsPrincipal buildNorthernLightsPrincipal(JwtToken jwtToken) {
        return NorthernLightsPrincipal.builder()
            .uid(jwtToken.getUid())
//            .roles(readNorthernLightsRoles(jwtToken))
            .scopes(readScopes(jwtToken))
            .build();
    }

//    private NorthernLightsRoles readNorthernLightsRoles(JwtToken jwtToken) {
//        // FIXME: add '-qua'
//        return jwtToken.readClaim("https://shop-api.marketplace-qua.aaaaaaaa.com/v1/roles", NorthernLightsRoles.class, new NorthernLightsRoles());
//    }

    private List<String> readScopes(JwtToken jwtToken) {
        return Arrays.stream((jwtToken.readClaim("scope", String.class, "").split(" ")))
            .filter(s -> !s.isEmpty())
            .distinct()
            .collect(toList());
    }

}
