package com.pfplaybackend.api.partyroom.application.service.task;

import com.pfplaybackend.api.config.redis.lock.RedisLockService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskExecutorService {

    private final RedisLockService redisLockService;
    private final PlaybackManagementService playbackManagementService;

    // FIXME LOCK KEY 접두어
    private static final String LOCK_KEY = "my-lock";
    private static final String LOCK_VALUE = "unique-identifier";

    public void performTaskWithLock(PartyroomId partyroomId, UserId userId) {
        boolean lockAcquired = redisLockService.acquireLock(LOCK_KEY, LOCK_VALUE, 10, TimeUnit.SECONDS);

        if (lockAcquired) {
            try {
                // 분산 잠금이 성공적으로 획득되면 수행할 작업
                playbackManagementService.complete(partyroomId, userId);
            } finally {
                // 작업이 끝나면 LOCK 해제
                redisLockService.releaseLock(LOCK_KEY, LOCK_VALUE);
            }
        } else {
            System.out.println("Could not acquire lock, another process is holding it.");
        }
    }
}
