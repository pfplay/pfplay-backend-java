package com.pfplaybackend.api.partyroom.application.service.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskScheduleService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setKeyWithExpiration(String key, Object value, long timeout, TimeUnit timeUnit) {
        // For Expiration Event
        redisTemplate.opsForValue().set("TASK:WAIT:" + key, "", timeout, timeUnit);
        // For Task Arguments
        redisTemplate.opsForValue().set("WAIT:ARGS:" + key, value);
    }
}
