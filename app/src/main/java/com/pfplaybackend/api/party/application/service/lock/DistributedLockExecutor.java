package com.pfplaybackend.api.party.application.service.lock;

import com.pfplaybackend.api.common.config.redis.lock.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final RedisLockService redisLockService;

    private static final String LOCK_PREFIX = "lock";

    public void performTaskWithLock(String lockSuffix, Supplier<Void> action) {
        String lockKey = LOCK_PREFIX + lockSuffix;
        String lockValue = UUID.randomUUID().toString();
        boolean lockAcquired = redisLockService.acquireLock(lockKey, lockValue, 10, TimeUnit.SECONDS);
        if (lockAcquired) {
            try {
                action.get();
            } finally {
                redisLockService.releaseLock(lockKey, lockValue);
            }
        } else {
            log.warn("Could not acquire lock, another process is holding it. key={}", lockKey);
        }
    }
}
