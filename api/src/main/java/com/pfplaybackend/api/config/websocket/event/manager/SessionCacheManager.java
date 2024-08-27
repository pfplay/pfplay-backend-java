package com.pfplaybackend.api.config.websocket.event.manager;
import com.pfplaybackend.api.user.domain.value.UserId;

public interface SessionCacheManager {
    void saveSessionCache(String sessionId, UserId userId, String destination);
    void deleteSessionCache(String sessionId);
}
