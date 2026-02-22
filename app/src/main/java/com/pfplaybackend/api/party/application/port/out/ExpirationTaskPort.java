package com.pfplaybackend.api.party.application.port.out;

import java.util.concurrent.TimeUnit;

public interface ExpirationTaskPort {
    void scheduleExpiration(String key, Object args, long timeout, TimeUnit unit);
    void cancelExpiration(String key);
    <T> T getTaskArgs(String key, Class<T> type);
    void clearTaskArgs(String key);
}
