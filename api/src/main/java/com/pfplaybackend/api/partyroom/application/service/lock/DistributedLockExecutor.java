package com.pfplaybackend.api.partyroom.application.service.lock;

import com.pfplaybackend.api.config.redis.lock.RedisLockService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final RedisLockService redisLockService;
    private final PlaybackManagementService playbackManagementService;

    private static final String LOCK_PREFIX = "lock";
    private static final String LOCK_VALUE = "unique-identifier";

    public void performTaskWithLock(String LOCK_SUFFIX, Supplier<Void> action) {
        String LOCK_KEY = LOCK_PREFIX + LOCK_SUFFIX;
        boolean lockAcquired = redisLockService.acquireLock(LOCK_KEY, LOCK_VALUE, 10, TimeUnit.SECONDS);
        if (lockAcquired) {
            try {
                action.get();
            } finally {
                redisLockService.releaseLock(LOCK_KEY, LOCK_VALUE);
            }
        } else {
            System.out.println("Could not acquire lock, another process is holding it.");
        }
    }
}
