package com.pfplaybackend.api.config.websocket.event.handler;
import com.pfplaybackend.api.user.domain.value.UserId;

public interface SessionEventHandler {
    void saveSessionCache(String sessionId, UserId userId, String destination);
}
