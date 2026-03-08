package com.pfplaybackend.api.auth.adapter.in.web;

import com.pfplaybackend.api.auth.adapter.in.web.payload.request.GenerateOAuthUrlRequest;
import com.pfplaybackend.api.auth.adapter.in.web.payload.request.LoginOAuthRequest;
import com.pfplaybackend.api.auth.adapter.in.web.payload.response.GenerateOAuthUrlResponse;
import com.pfplaybackend.api.auth.adapter.in.web.payload.response.LoginOAuthResponse;
import com.pfplaybackend.api.auth.application.dto.command.OAuthLoginCommand;
import com.pfplaybackend.api.auth.application.dto.result.AuthResult;
import com.pfplaybackend.api.auth.application.dto.result.OAuthUrlResult;
import com.pfplaybackend.api.auth.application.service.AuthService;
import com.pfplaybackend.api.auth.application.service.LogoutService;
import com.pfplaybackend.api.auth.application.service.OAuthUrlService;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.auth.domain.exception.AuthException;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Auth API", description = "OAuth 인증 및 로그아웃 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final OAuthUrlService oAuthUrlService;
    private final AuthService authService;
    private final LogoutService logoutService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "OAuth 인증 URL 생성", description = "지정된 OAuth 제공자(Google, Twitter)의 인증 URL을 생성합니다. 프론트엔드에서 이 URL로 사용자를 리다이렉트하여 OAuth 인증을 시작합니다.")
    @ApiErrorCodes({AuthException.class})
    @PostMapping("/oauth/url")
    public ResponseEntity<GenerateOAuthUrlResponse> generateOAuthUrl(@Valid @RequestBody GenerateOAuthUrlRequest request) {
        log.info("Generating OAuth URL for provider: {}", request.getProvider());

        try {
            OAuthProvider provider = OAuthProvider.fromString(request.getProvider());
            OAuthUrlResult result = oAuthUrlService.generateAuthUrl(provider, request.getCodeVerifier());

            return ResponseEntity.ok(GenerateOAuthUrlResponse.builder()
                    .authUrl(result.authUrl())
                    .state(result.state())
                    .provider(result.provider())
                    .expiresIn(result.expiresIn())
                    .build());

        } catch (IllegalArgumentException e) {
            log.error("Invalid provider: {}", request.getProvider());
            return ResponseEntity.badRequest()
                    .body(GenerateOAuthUrlResponse.builder()
                            .success(false)
                            .message("Invalid OAuth provider: " + request.getProvider())
                            .build());
        } catch (Exception e) {
            log.error("Failed to generate OAuth URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenerateOAuthUrlResponse.builder()
                            .success(false)
                            .message("Failed to generate OAuth URL")
                            .build());
        }
    }

    @Operation(summary = "OAuth 콜백 로그인", description = "OAuth 제공자로부터 받은 인증 코드를 처리하여 로그인을 완료합니다. 성공 시 JWT 토큰이 HttpOnly 쿠키로 설정됩니다.")
    @PostMapping("/oauth/callback")
    public ResponseEntity<LoginOAuthResponse> oauthCallback(
            @Valid @RequestBody LoginOAuthRequest request,
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
                            .body(LoginOAuthResponse.builder()
                                    .success(false)
                                    .message("Invalid or expired state parameter")
                                    .build());
                }
            }

            // OAuth 로그인 처리
            OAuthLoginCommand command = new OAuthLoginCommand(request.getProvider(), request.getCode(), request.getCodeVerifier());
            AuthResult authResult = authService.processOAuthLogin(command);

            // JWT 토큰을 HttpOnly 쿠키로 저장
            cookieUtil.addAccessTokenCookie(response, authResult.accessToken());

            // 쿠키 전용 응답 반환 (토큰 정보 제거)
            return ResponseEntity.ok(LoginOAuthResponse.builder()
                    .tokenType(authResult.tokenType())
                    .expiresIn(authResult.expiresIn())
                    .issuedAt(authResult.issuedAt())
                    .success(true)
                    .message("Authentication successful")
                    .build());

        } catch (IllegalArgumentException e) {
            log.error("Invalid provider in OAuth callback: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(LoginOAuthResponse.builder()
                            .success(false)
                            .message("Invalid OAuth provider")
                            .build());
        } catch (Exception e) {
            log.error("OAuth callback failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginOAuthResponse.builder()
                            .success(false)
                            .message("Authentication failed")
                            .build());
        }
    }

    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다. 활성 파티룸이 있으면 퇴장 처리 후, 인증 쿠키를 삭제합니다.")
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
