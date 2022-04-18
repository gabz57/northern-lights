package io.northernlights.security.jwt.auth0;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.northernlights.commons.TimeService;
import io.northernlights.security.AuthenticationException;
import io.northernlights.security.jwt.JwtToken;
import io.northernlights.security.jwt.JwtProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class Auth0GoogleJwtProvider implements JwtProvider {
    private final JwkProvider jwkProvider;
    private final String googleClientId;
    private final TimeService timeService;

    public Auth0GoogleJwtProvider(TimeService timeService, String googleClientId) {
        this.timeService = timeService;
        try {
            this.jwkProvider = new UrlJwkProvider(new URL("https://www.googleapis.com/oauth2/v3/certs"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid jwks uri", e);
        }
        this.googleClientId = googleClientId;
    }

    public JwtToken of(String authToken) {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(authToken);
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("Failed to decode authentication token", e);
        }

        Algorithm algorithm;
        try {
            Jwk jwk = jwkProvider.get(jwt.getKeyId());
            // only the public key is used during verification
            algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
        } catch (IllegalArgumentException | JwkException e) {
            throw new AuthenticationException("Failed to use RSA256 algorithm with token", e);
        }

        JWTVerifier.BaseVerification verification = (JWTVerifier.BaseVerification) JWT.require(algorithm)
            .withIssuer("accounts.google.com", "https://accounts.google.com")
            .withAudience(googleClientId + ".apps.googleusercontent.com");
        try {
            verification
                .build(() -> Date.from(timeService.now().toInstant()))
                .verify(jwt);
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("Invalid JWT", e);
        }

        return new JwtToken.NorthernLightsJwtToken(new Auth0NorthernLightsJwtToken(jwt));
    }
}
