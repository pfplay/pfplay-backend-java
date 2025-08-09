package com.pfplaybackend.api.auth.dto.request;

import com.pfplaybackend.api.auth.validation.ValidProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUrlRequest {

    @NotBlank(message = "Provider is required")
    @ValidProvider
    private String provider;

    @NotBlank(message = "Code verifier is required")
    @Size(min = 43, max = 128, message = "Code verifier must be between 43 and 128 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-._~]+$", message = "Invalid code verifier format")
    private String codeVerifier;

    /**
     * 리다이렉트 URI 오버라이드 (선택적)
     * 설정된 기본값과 다른 리다이렉트 URI를 사용하고 싶을 때
     */
    private String redirectUri;
}
