package com.pfplaybackend.api.common.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String topic, Object object) {
        redisTemplate.convertAndSend(topic, object);
    }
}
