package com.pfplaybackend.api.partyroom.repository.impl;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.partyroom.repository.custom.SessionRepository;
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

    public PartyroomSessionData savePartyroomSessionData(PartyroomSessionData partyroomSessionData) {
        String sessionId = partyroomSessionData.getSessionId();
        redisTemplate.opsForValue().set(sessionId, partyroomSessionData);
        return partyroomSessionData;
    }
}
