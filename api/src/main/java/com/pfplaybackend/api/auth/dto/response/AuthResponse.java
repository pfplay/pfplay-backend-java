package com.pfplaybackend.api.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    // 쿠키 기반이므로 토큰은 응답에서 제외 (보안)
    private String accessToken;
    private String refreshToken;

    // 쿠키 기반에서는 "Cookie" 또는 null로 설정
    @Builder.Default
    private String tokenType = "Cookie";

    private Long expiresIn;
    private UserInfo user;
    private LocalDateTime issuedAt;

    // 성공 여부 (선택적)
    @Builder.Default
    private Boolean success = true;

    // 메시지 (선택적)
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
        private String profileImage;
        private String provider;
        private String userType;
        private String authorityTier;
        private LocalDateTime createdAt;
    }

    /**
     * 쿠키 전용 응답 생성 (토큰 정보 제거)
     */
    public AuthResponse forCookieResponse() {
        return AuthResponse.builder()
                .tokenType("Cookie")
                .expiresIn(this.expiresIn)
                .user(this.user)
                .issuedAt(this.issuedAt)
                .success(true)
                .message("Authentication successful")
                .build();
    }

    /**
     * API 테스트용 응답 생성 (토큰 포함)
     */
    public AuthResponse forApiResponse() {
        return AuthResponse.builder()
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .tokenType("Bearer")
                .expiresIn(this.expiresIn)
                .user(this.user)
                .issuedAt(this.issuedAt)
                .success(true)
                .build();
    }

    /**
     * 에러 응답 생성
     */
    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .issuedAt(java.time.LocalDateTime.now())
                .build();
    }
}