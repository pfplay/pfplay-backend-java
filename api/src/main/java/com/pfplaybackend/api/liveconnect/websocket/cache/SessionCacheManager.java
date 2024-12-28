package com.pfplaybackend.api.liveconnect.websocket.cache;
import com.pfplaybackend.api.user.domain.value.UserId;
import java.util.Optional;

public interface SessionCacheManager {
    void saveSessionCache(String sessionId, UserId userId, String destination);
    void deleteSessionCache(String sessionId);
    Optional<Object> getSessionCache(String sessionId);
}
