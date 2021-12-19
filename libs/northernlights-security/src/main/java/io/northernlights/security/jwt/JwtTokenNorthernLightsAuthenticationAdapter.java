package io.northernlights.security.jwt;

import io.northernlights.security.NorthernLightsPrincipal;

import java.util.Arrays;
import java.util.List;

public class JwtTokenNorthernLightsAuthenticationAdapter {

    public NorthernLightsPrincipal buildNorthernLightsPrincipal(JwtToken jwtToken) {
        return NorthernLightsPrincipal.builder()
            .uid(jwtToken.getUid())
//            .roles(readNorthernLightsRoles(jwtToken))
            .scopes(readScopes(jwtToken))
            .build();
    }

//    private NorthernLightsRoles readNorthernLightsRoles(JwtToken jwtToken) {
//        return jwtToken.readClaim("https://shop-api.marketplace-qua.aaaaaaaa.com/v1/roles", NorthernLightsRoles.class, new NorthernLightsRoles());
//    }

    private List<String> readScopes(JwtToken jwtToken) {
        return Arrays.stream((jwtToken.readClaim("scope", String.class, "").split(" ")))
            .filter(s -> !s.isEmpty())
            .distinct()
            .toList();
    }

}
