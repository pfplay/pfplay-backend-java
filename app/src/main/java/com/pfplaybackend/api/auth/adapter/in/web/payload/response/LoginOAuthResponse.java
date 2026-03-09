package com.pfplaybackend.api.auth.adapter.in.web.payload.response;

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
public class LoginOAuthResponse {

    @Builder.Default
    private String tokenType = "Cookie";

    private Long expiresIn;

    private LocalDateTime issuedAt;
}
