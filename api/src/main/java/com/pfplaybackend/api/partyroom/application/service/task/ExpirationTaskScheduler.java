package com.pfplaybackend.api.partyroom.application.service.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExpirationTaskScheduler {
    private final RedisTemplate<String, Object> redisTemplate;

    private final String TASK_PREFIX = "TASK:WAIT:";
    private final String ARGS_PREFIX = "WAIT:ARGS:";

    public void setKeyWithExpiration(String suffix, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(TASK_PREFIX + suffix, "", timeout, timeUnit);
        redisTemplate.opsForValue().set(ARGS_PREFIX + suffix, value);
    }

    public void deleteKey(String suffix) {
        redisTemplate.delete(TASK_PREFIX + suffix);
        redisTemplate.delete(ARGS_PREFIX  + suffix);
    }
}
