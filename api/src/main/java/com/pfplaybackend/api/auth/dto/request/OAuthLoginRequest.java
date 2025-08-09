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
public class OAuthLoginRequest {

    @NotBlank(message = "Provider is required")
    @ValidProvider // Custom validation annotation
    private String provider;

    @NotBlank(message = "Authorization code is required")
    private String code;

    @NotBlank(message = "Code verifier is required")
    @Size(min = 43, max = 128, message = "Code verifier must be between 43 and 128 characters")
    @Pattern(regexp = "^[A-Za-z0-9-._~]+$", message = "Invalid code verifier format")
    private String codeVerifier;

    // Optional: state parameter for CSRF protection
    private String state;
}
