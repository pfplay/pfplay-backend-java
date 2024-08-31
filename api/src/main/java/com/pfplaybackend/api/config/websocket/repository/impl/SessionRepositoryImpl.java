package com.pfplaybackend.api.config.websocket.repository.impl;

import com.pfplaybackend.api.config.websocket.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.config.websocket.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<PartyroomSessionData> getPartyroomSessionData(String sessionId) {
        return Optional.ofNullable((PartyroomSessionData) redisTemplate.opsForValue().get(sessionId));
    }

    public PartyroomSessionData savePartyroomSessionData(String sessionId, PartyroomSessionData partyroomSessionData) {
        redisTemplate.opsForValue().set(sessionId, partyroomSessionData);
        return partyroomSessionData;
    }
}
