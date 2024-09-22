package com.pfplaybackend.api.config.redis.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(String lockKey, String lockValue, long timeout, TimeUnit timeUnit) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String lockKey, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }
}
