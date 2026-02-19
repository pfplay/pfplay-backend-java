package com.pfplaybackend.realtime.port;

import java.util.Optional;

public interface SessionCachePort {
    void saveSessionCache(String sessionId, String userId, String destination);
    void deleteSessionCache(String sessionId);
    Optional<Object> getSessionCache(String sessionId);
}
