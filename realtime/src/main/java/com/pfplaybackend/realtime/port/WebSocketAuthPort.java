package com.pfplaybackend.realtime.port;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface WebSocketAuthPort {
    Optional<String> extractUserId(HttpServletRequest request);
}
