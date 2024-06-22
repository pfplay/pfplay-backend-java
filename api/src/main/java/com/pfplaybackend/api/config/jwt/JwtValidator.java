
package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import com.pfplaybackend.api.config.jwt.enums.TokenSubject;
import com.pfplaybackend.api.user.domain.enums.AuthorityTier;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtValidator {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    /**
     * 쿠키에서 AccessToken 추출
     */
    public Optional<String> extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public DecodedJWT getDecodedJWT(String accessToken) {
        return JWT.decode(accessToken);
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    public UserCredentials getUserAuthentication(DecodedJWT decodedJWT) {
        String extractedUid = decodedJWT.getClaim(TokenClaim.UID.getValue()).asString();
        String extractedAuthorityTier = decodedJWT.getClaim(TokenClaim.AUTHORITY_TIER.getValue()).asString();
        return UserCredentials.create(UUID.fromString(extractedUid), AuthorityTier.valueOf(extractedAuthorityTier));
    }
}