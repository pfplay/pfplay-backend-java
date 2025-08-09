package com.pfplaybackend.api.common.config.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    @Value("${app.jwt.cookie.access-token-name:access_token}")
    private String accessTokenCookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String tokenFromCookie = resolveFromCookie(request);
        if (StringUtils.hasText(tokenFromCookie)) {
            log.debug("JWT token resolved from cookie");
            return tokenFromCookie;
        }
        return null;
    }

    private String resolveFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (accessTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}