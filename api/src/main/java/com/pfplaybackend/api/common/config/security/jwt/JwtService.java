package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.enums.TokenClaim;
import com.pfplaybackend.api.common.config.security.jwt.enums.TokenSubject;
import com.pfplaybackend.api.common.config.security.jwt.properties.JwtProperties;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.AuthenticationException;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
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

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Member용 AccessToken 생성
     */
    public String generateAccessTokenForMember(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaim.UID.getValue(), member.getUserId().getUid().toString());
        claims.put(TokenClaim.EMAIL.getValue(), member.getEmail());
        claims.put(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString());
        claims.put(TokenClaim.AUTHORITY_TIER.getValue(), member.getAuthorityTier().toString());
        claims.put("type", "access");

        // FIXME sub(subject)는 "누구를 위한 토큰인가?"를 나타내는 값이다.
        return createToken(
                claims,
                TokenSubject.ACCESS_TOKEN_SUBJECT.getValue(),
                jwtProperties.getExpirationMs()
        );
    }

    /**
     * Guest용 AccessToken 생성
     */
    public String generateAccessTokenForGuest(Guest guest) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaim.UID.getValue(), guest.getUserId().getUid().toString());
        claims.put(TokenClaim.EMAIL.getValue(), "N/A");
        claims.put(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_GUEST.toString());
        claims.put(TokenClaim.AUTHORITY_TIER.getValue(), AuthorityTier.GT.toString());
        claims.put("type", "access");

        // FIXME sub(subject)는 "누구를 위한 토큰인가?"를 나타내는 값이다.
        return createToken(
                claims,
                TokenSubject.ACCESS_TOKEN_SUBJECT.getValue(),
                jwtProperties.getExpirationMs()
        );
    }

    /**
     * Member용 만료되지 않는 AccessToken 생성
     */
    public String generateNonExpiringAccessTokenForMember(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaim.UID.getValue(), member.getUserId().getUid().toString());
        claims.put(TokenClaim.EMAIL.getValue(), member.getEmail());
        claims.put(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString());
        claims.put(TokenClaim.AUTHORITY_TIER.getValue(), member.getAuthorityTier().toString());
        claims.put("type", "access");

        return createNonExpiringToken(claims, TokenSubject.ACCESS_TOKEN_SUBJECT.getValue());
    }

    /**
     * Guest용 만료되지 않는 AccessToken 생성
     */
    public String generateNonExpiringAccessTokenForGuest(Guest guest) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaim.UID.getValue(), guest.getUserId().getUid().toString());
        claims.put(TokenClaim.EMAIL.getValue(), "N/A");
        claims.put(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_GUEST.toString());
        claims.put(TokenClaim.AUTHORITY_TIER.getValue(), AuthorityTier.GT.toString());
        claims.put("type", "access");

        return createNonExpiringToken(claims, TokenSubject.ACCESS_TOKEN_SUBJECT.getValue());
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
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

        Date now = new Date();

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
        // UID 클레임에서 사용자 ID 추출
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

    /**
     * 토큰이 곧 만료될 예정인지 확인 (만료 10분 전)
     */
    public boolean isTokenNearExpiry(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeUntilExpiry = expiration.getTime() - now.getTime();

            // 10분 = 600,000ms
            return timeUntilExpiry < 600_000L;
        } catch (Exception e) {
            log.debug("Cannot check token expiry: {}", e.getMessage());
            return true; // 파싱 실패시 만료로 간주
        }
    }

    /**
     * 토큰에서 모든 클레임을 추출
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = extractClaims(token);
        return new HashMap<>(claims);
    }

    /**
     * 토큰이 Member 토큰인지 확인
     */
    public boolean isMemberToken(String token) {
        try {
            AccessLevel accessLevel = getAccessLevelFromToken(token);
            return AccessLevel.ROLE_MEMBER.equals(accessLevel);
        } catch (Exception e) {
            log.debug("Cannot determine user type from token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰이 Guest 토큰인지 확인
     */
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
            // JJWT 0.12.x 방식
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