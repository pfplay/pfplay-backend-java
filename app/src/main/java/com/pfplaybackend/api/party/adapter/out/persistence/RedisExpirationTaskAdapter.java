package com.pfplaybackend.api.party.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.party.application.port.out.ExpirationTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisExpirationTaskAdapter implements ExpirationTaskPort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TASK_PREFIX = "TASK:WAIT:";
    private static final String ARGS_PREFIX = "WAIT:ARGS:";

    @Override
    public void scheduleExpiration(String key, Object args, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(TASK_PREFIX + key, "", timeout, unit);
        redisTemplate.opsForValue().set(ARGS_PREFIX + key, args);
    }

    @Override
    public void cancelExpiration(String key) {
        redisTemplate.delete(TASK_PREFIX + key);
        redisTemplate.delete(ARGS_PREFIX + key);
    }

    @Override
    public <T> T getTaskArgs(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(ARGS_PREFIX + key);
        return objectMapper.convertValue(value, type);
    }

    @Override
    public void clearTaskArgs(String key) {
        redisTemplate.delete(ARGS_PREFIX + key);
    }
}
