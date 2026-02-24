package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.jwt.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        var cookieConfig = jwtProperties.getCookie();
        addCookie(response,
                cookieConfig.getAccessTokenName(),
                token,
                cookieConfig.getAccessTokenExpirySeconds());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        var cookieConfig = jwtProperties.getCookie();
        addCookie(response,
                cookieConfig.getRefreshTokenName(),
                token,
                cookieConfig.getRefreshTokenExpirySeconds());
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, jwtProperties.getCookie().getAccessTokenName());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, jwtProperties.getCookie().getRefreshTokenName());
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        var cookieConfig = jwtProperties.getCookie();

        // Spring Boot 3.x에서 ResponseCookie 사용 권장
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(name).append("=").append(value);
        cookieBuilder.append("; Path=").append(cookieConfig.getPath());
        cookieBuilder.append("; Max-Age=").append(maxAge);
        cookieBuilder.append("; HttpOnly");

        if (cookieConfig.isSecure()) {
            cookieBuilder.append("; Secure");
        }

        // SameSite 속성
        cookieBuilder.append("; SameSite=").append(cookieConfig.getSameSite());

        response.addHeader("Set-Cookie", cookieBuilder.toString());

        log.debug("Added cookie: {} with maxAge: {}", name, maxAge);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        var cookieConfig = jwtProperties.getCookie();

        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(name).append("=");
        cookieBuilder.append("; Path=").append(cookieConfig.getPath());
        cookieBuilder.append("; Max-Age=0");
        cookieBuilder.append("; HttpOnly");

        if (cookieConfig.isSecure()) {
            cookieBuilder.append("; Secure");
        }

        cookieBuilder.append("; SameSite=").append(cookieConfig.getSameSite());

        response.addHeader("Set-Cookie", cookieBuilder.toString());

        log.debug("Deleted cookie: {}", name);
    }
}
