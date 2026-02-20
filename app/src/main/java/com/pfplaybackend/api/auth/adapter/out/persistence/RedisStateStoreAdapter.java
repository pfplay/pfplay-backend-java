package com.pfplaybackend.api.auth.adapter.out.persistence;

import com.pfplaybackend.api.auth.application.port.out.StateStorePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStateStoreAdapter implements StateStorePort {

    private final RedisTemplate<String, String> redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final String STATE_PREFIX = "oauth2:state:";
    private static final long STATE_VALIDITY_MINUTES = 10;

    public String generateAndStoreState(String provider) {
        String state = generateRandomState();
        String key = STATE_PREFIX + state;

        redisTemplate.opsForValue().set(
                key,
                provider,
                STATE_VALIDITY_MINUTES,
                TimeUnit.MINUTES
        );

        log.debug("Stored state in Redis for provider {}: {}", provider, state);
        return state;
    }

    public boolean validateAndConsumeState(String state, String expectedProvider) {
        if (state == null || state.isEmpty()) {
            return false;
        }

        String key = STATE_PREFIX + state;
        String storedProvider = redisTemplate.opsForValue().get(key);

        if (storedProvider == null) {
            log.warn("State not found in Redis: {}", state);
            return false;
        }

        if (!expectedProvider.equals(storedProvider)) {
            log.warn("Provider mismatch. Expected: {}, Actual: {}", expectedProvider, storedProvider);
            return false;
        }

        // State 소비 (삭제)
        redisTemplate.delete(key);
        log.debug("State validated and consumed from Redis: {}", state);

        return true;
    }

    private String generateRandomState() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
