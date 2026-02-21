package com.pfplaybackend.api.bootstrap.adapter;

import com.pfplaybackend.api.common.config.security.jwt.JwtCookieValidator;
import com.pfplaybackend.realtime.port.WebSocketAuthPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtWebSocketAuthAdapter implements WebSocketAuthPort {
    private final JwtCookieValidator jwtCookieValidator;

    @Override
    public Optional<String> extractUserId(HttpServletRequest request) {
        return jwtCookieValidator.extractUserId(request);
    }
}
