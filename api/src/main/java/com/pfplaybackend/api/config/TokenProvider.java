package com.pfplaybackend.api.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.security.handle.AudienceValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

@Slf4j
@Configuration
public class TokenProvider {

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    private final Instant now = Instant.now();
    private final long expiry = 2600000L;   // 한달

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(this.publicKey).build();
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        decoder.setJwtValidator(withAudience);
        return decoder;
    }

    public String createAccessToken(Authority scope, String email, Long userId) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(email)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .claim("scope", scope.getRole())
                .claim("userId", userId)
                .build();

        log.info("jwt expiresAt ={}" , claims.getExpiresAt());
        return jwtEncoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String createGuestAccessToken(Authority scope, Long id) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(String.valueOf(id))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .claim("scope", scope.getRole())
                .claim("userId", id)
                .build();

        return jwtEncoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
