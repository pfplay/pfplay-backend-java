package com.pfplaybackend.api.auth.adapter.in.web.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateOAuthUrlResponse {

    /**
     * OAuth 인증 서버 URL
     */
    private String authUrl;

    /**
     * CSRF 방지를 위한 state 파라미터
     */
    private String state;

    /**
     * OAuth 제공자 (google, twitter 등)
     */
    private String provider;

    /**
     * URL 유효 시간 (초)
     */
    private Long expiresIn;
}
