package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.properties.JwtProperties;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtCookieValidator {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * 요청에서 유효한 Access Token 추출 및 검증
     */
    public Optional<String> extractAndValidateAccessToken(HttpServletRequest request) {
        return extractCookie(request, jwtProperties.getCookie().getAccessTokenName())
                .filter(this::isValidAccessToken);
    }

    /**
     * 요청에서 유효한 Refresh Token 추출 및 검증
     */
    public Optional<String> extractAndValidateRefreshToken(HttpServletRequest request) {
        return extractCookie(request, jwtProperties.getCookie().getRefreshTokenName())
                .filter(this::isValidRefreshToken);
    }

    /**
     * Access Token이 존재하고 유효한지 확인
     */
    public boolean hasValidAccessToken(HttpServletRequest request) {
        return extractAndValidateAccessToken(request).isPresent();
    }

    /**
     * Refresh Token이 존재하고 유효한지 확인
     */
    public boolean hasValidRefreshToken(HttpServletRequest request) {
        return extractAndValidateRefreshToken(request).isPresent();
    }

    /**
     * 인증된 사용자인지 확인 (유효한 Access Token 보유)
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        return hasValidAccessToken(request);
    }

    /**
     * 토큰이 곧 만료될 예정인지 확인
     */
    public boolean isTokenNearExpiry(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(jwtService::isTokenNearExpiry)
                .orElse(true);
    }

    /**
     * 사용자가 Member인지 확인
     */
    public boolean isMemberUser(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(jwtService::isMemberToken)
                .orElse(false);
    }

    /**
     * 사용자가 Guest인지 확인
     */
    public boolean isGuestUser(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(jwtService::isGuestToken)
                .orElse(false);
    }

    /**
     * 요청에서 사용자 ID 추출
     */
    public Optional<String> extractUserId(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getUserIdFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract user ID from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * 요청에서 사용자 이메일 추출
     */
    public Optional<String> extractUserEmail(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getEmailFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract email from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * 요청에서 OAuth 제공자 추출
     */
    public Optional<String> extractProvider(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getProviderFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract provider from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * 요청에서 접근 레벨 추출
     */
    public Optional<AccessLevel> extractAccessLevel(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getAccessLevelFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract access level from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * 요청에서 권한 등급 추출
     */
    public Optional<AuthorityTier> extractAuthorityTier(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getAuthorityTierFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract authority tier from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * 요청에서 모든 클레임 추출
     */
    public Optional<java.util.Map<String, Object>> extractAllClaims(HttpServletRequest request) {
        return extractAndValidateAccessToken(request)
                .map(token -> {
                    try {
                        return jwtService.getAllClaimsFromToken(token);
                    } catch (Exception e) {
                        log.debug("Failed to extract claims from token: {}", e.getMessage());
                        return null;
                    }
                });
    }

    /**
     * JWT 토큰 자동 갱신이 필요한지 확인
     */
    public boolean needsTokenRefresh(HttpServletRequest request) {
        // Access Token이 없거나 만료 임박, 하지만 유효한 Refresh Token이 있는 경우
        return !hasValidAccessToken(request) && hasValidRefreshToken(request) ||
                isTokenNearExpiry(request);
    }

    /**
     * 쿠키에서 특정 이름의 값 추출
     */
    public Optional<String> extractCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Access Token 유효성 검증
     */
    private boolean isValidAccessToken(String token) {
        try {
            return jwtService.validateAccessToken(token);
        } catch (Exception e) {
            log.debug("Invalid access token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token 유효성 검증
     */
    private boolean isValidRefreshToken(String token) {
        try {
            return jwtService.validateRefreshToken(token);
        } catch (Exception e) {
            log.debug("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }
}
