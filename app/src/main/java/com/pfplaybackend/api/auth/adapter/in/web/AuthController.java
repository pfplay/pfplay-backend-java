package com.pfplaybackend.api.auth.adapter.in.web;

import com.pfplaybackend.api.auth.application.service.AuthService;
import com.pfplaybackend.api.auth.application.service.LogoutService;
import com.pfplaybackend.api.auth.application.service.OAuthUrlService;
import com.pfplaybackend.api.auth.adapter.in.web.dto.response.AuthResponse;
import com.pfplaybackend.api.auth.adapter.in.web.dto.request.OAuthLoginRequest;
import com.pfplaybackend.api.auth.adapter.in.web.dto.request.OAuthUrlRequest;
import com.pfplaybackend.api.auth.adapter.in.web.dto.response.OAuthUrlResponse;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final OAuthUrlService oAuthUrlService;
    private final AuthService authService;
    private final LogoutService logoutService;
    private final CookieUtil cookieUtil;

    /**
     * OAuth 인증 URL 생성
     */
    @PostMapping("/oauth/url")
    public ResponseEntity<OAuthUrlResponse> generateOAuthUrl(@Valid @RequestBody OAuthUrlRequest request) {
        log.info("Generating OAuth URL for provider: {}", request.getProvider());

        try {
            OAuthProvider provider = OAuthProvider.fromString(request.getProvider());
            OAuthUrlResponse response = oAuthUrlService.generateAuthUrl(provider, request.getCodeVerifier());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid provider: {}", request.getProvider());
            return ResponseEntity.badRequest()
                    .body(OAuthUrlResponse.builder()
                            .success(false)
                            .message("Invalid OAuth provider: " + request.getProvider())
                            .build());
        } catch (Exception e) {
            log.error("Failed to generate OAuth URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OAuthUrlResponse.builder()
                            .success(false)
                            .message("Failed to generate OAuth URL")
                            .build());
        }
    }

    /**
     * OAuth 콜백 처리 (쿠키 방식만 지원)
     */
    @PostMapping("/oauth/callback")
    public ResponseEntity<AuthResponse> oauthCallback(
            @Valid @RequestBody OAuthLoginRequest request,
            HttpServletResponse response) {

        try {
            OAuthProvider provider = OAuthProvider.fromString(request.getProvider());

            // State 검증
            if (request.getState() != null) {
                boolean stateValid = oAuthUrlService.validateAndConsumeState(
                        request.getState(), provider, request.getCodeVerifier());

                if (!stateValid) {
                    log.warn("Invalid state parameter for OAuth callback");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(AuthResponse.builder()
                                    .success(false)
                                    .message("Invalid or expired state parameter")
                                    .build());
                }
            }

            // OAuth 로그인 처리
            AuthResponse authResponse = authService.processOAuthLogin(request);

            // JWT 토큰을 HttpOnly 쿠키로 저장
            cookieUtil.addAccessTokenCookie(response, authResponse.getAccessToken());

            // 쿠키 전용 응답 반환 (토큰 정보 제거)
            return ResponseEntity.ok(authResponse.forCookieResponse());

        } catch (IllegalArgumentException e) {
            log.error("Invalid provider in OAuth callback: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Invalid OAuth provider")
                            .build());
        } catch (Exception e) {
            log.error("OAuth callback failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Authentication failed")
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        try {
            logoutService.exitActivePartyroomIfPresent();
        } catch (Exception e) {
            log.warn("Failed to exit active partyroom during logout: {}", e.getMessage());
        }

        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);

        return ResponseEntity.ok().build();
    }
}
