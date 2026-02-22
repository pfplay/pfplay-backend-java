package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.dto.TokenClaimsRequest;
import com.pfplaybackend.api.common.config.security.jwt.enums.TokenClaim;
import com.pfplaybackend.api.common.config.security.jwt.enums.TokenSubject;
import com.pfplaybackend.api.common.config.security.jwt.properties.JwtProperties;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final java.time.Clock clock;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(TokenClaimsRequest claims) {
        Map<String, Object> tokenClaims = buildClaims(claims);
        return createToken(
                tokenClaims,
                TokenSubject.ACCESS_TOKEN_SUBJECT.getValue(),
                jwtProperties.getExpirationMs()
        );
    }

    public String generateNonExpiringAccessToken(TokenClaimsRequest claims) {
        Map<String, Object> tokenClaims = buildClaims(claims);
        return createNonExpiringToken(tokenClaims, TokenSubject.ACCESS_TOKEN_SUBJECT.getValue());
    }

    private Map<String, Object> buildClaims(TokenClaimsRequest claims) {
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put(TokenClaim.UID.getValue(), claims.uid());
        tokenClaims.put(TokenClaim.EMAIL.getValue(), claims.email());
        tokenClaims.put(TokenClaim.ACCESS_LEVEL.getValue(), claims.accessLevel().toString());
        tokenClaims.put(TokenClaim.AUTHORITY_TIER.getValue(), claims.authorityTier().toString());
        tokenClaims.put("type", "access");
        return tokenClaims;
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = Date.from(clock.instant());
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private String createNonExpiringToken(Map<String, Object> claims, String subject) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2099, Calendar.DECEMBER, 31, 23, 59, 59);
        Date expiryDate = calendar.getTime();

        Date now = Date.from(clock.instant());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return "access".equals(claims.get("type", String.class));
        } catch (Exception e) {
            log.error("Invalid access token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get(TokenClaim.UID.getValue(), String.class);
    }

    public String getEmailFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get(TokenClaim.EMAIL.getValue(), String.class);
    }

    public String getProviderFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("provider", String.class);
    }

    public AccessLevel getAccessLevelFromToken(String token) {
        Claims claims = extractClaims(token);
        String accessLevel = claims.get(TokenClaim.ACCESS_LEVEL.getValue(), String.class);
        return accessLevel != null ? AccessLevel.valueOf(accessLevel) : null;
    }

    public AuthorityTier getAuthorityTierFromToken(String token) {
        Claims claims = extractClaims(token);
        String authorityTier = claims.get(TokenClaim.AUTHORITY_TIER.getValue(), String.class);
        return authorityTier != null ? AuthorityTier.valueOf(authorityTier) : null;
    }

    public Long getAccessTokenExpiration() {
        return jwtProperties.getExpirationMs();
    }

    public boolean isTokenNearExpiry(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            Date now = Date.from(clock.instant());
            long timeUntilExpiry = expiration.getTime() - now.getTime();
            return timeUntilExpiry < 600_000L;
        } catch (Exception e) {
            log.debug("Cannot check token expiry: {}", e.getMessage());
            return true;
        }
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = extractClaims(token);
        return new HashMap<>(claims);
    }

    public boolean isMemberToken(String token) {
        try {
            AccessLevel accessLevel = getAccessLevelFromToken(token);
            return AccessLevel.ROLE_MEMBER.equals(accessLevel);
        } catch (Exception e) {
            log.debug("Cannot determine user type from token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isGuestToken(String token) {
        try {
            AccessLevel accessLevel = getAccessLevelFromToken(token);
            return AccessLevel.ROLE_GUEST.equals(accessLevel);
        } catch (Exception e) {
            log.debug("Cannot determine user type from token: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new AuthenticationException("Token has expired");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new AuthenticationException("Invalid token format");
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new AuthenticationException("Token validation failed");
        }
    }
}
