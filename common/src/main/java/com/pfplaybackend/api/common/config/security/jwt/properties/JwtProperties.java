package com.pfplaybackend.api.common.config.security.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private Long expirationMs;
    private Long refreshExpirationMs;
    private CookieProperties cookie = new CookieProperties();

    @Data
    public static class CookieProperties {
        private String accessTokenName = "AccessToken";
        private String refreshTokenName = "RefreshToken";
        private String domain = "localhost";
        private String path = "/";
        private Integer accessTokenExpirySeconds = 86400;
        private Integer refreshTokenExpirySeconds = 604800;
        private boolean secure = true;
        private String sameSite = "Lax";
    }
}