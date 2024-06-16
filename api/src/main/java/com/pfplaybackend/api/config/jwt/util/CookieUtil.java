package com.pfplaybackend.api.config.jwt.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie getCookieWithToken(String cookieKey, String token) {
        return ResponseCookie.from(cookieKey, token)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(3600000)
                .build();
    }
}
