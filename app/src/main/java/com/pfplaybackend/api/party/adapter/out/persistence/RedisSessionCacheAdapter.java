package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.realtime.port.SessionCachePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisSessionCacheAdapter implements SessionCachePort {
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionCacheAdapter.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final PartyroomQueryPort partyroomQueryPort;

    @Transactional
    public void saveSessionCache(String sessionId, String userId, String destination) {
        UserId userIdObj = UserId.fromString(userId);
        String[] parts = destination.split("/");
        if(parts[1].equals("sub")) {
            String topic = parts[2];
            String separator = parts[3];
            if (topic.equals("partyrooms")) {
                Optional<PartyroomSessionDto> partyroomSessionDto = createSessionData(sessionId, userIdObj);
                if (partyroomSessionDto.isEmpty()) {
                    logger.warn("No active partyroom found for userId={}, sessionId={} — skipping session cache", userId, sessionId);
                    return;
                }
                PartyroomSessionDto sessionData = partyroomSessionDto.get();
                redisTemplate.opsForValue().set(sessionId, sessionData);
            }
        }
    }

    @Transactional
    public void deleteSessionCache(String sessionId){
        redisTemplate.delete(sessionId);
    }

    @Transactional(readOnly = true)
    public Optional<Object> getSessionCache(String sessionId) {
        Object object =  redisTemplate.opsForValue().get(sessionId);
        if (object == null) {
            return Optional.empty();
        }
        return Optional.of(object);
    }

    private Optional<PartyroomSessionDto> createSessionData(String sessionId, UserId userId) {
        Optional<ActivePartyroomDto> optional = partyroomQueryPort.getActivePartyroomByUserId(userId);
        if (optional.isPresent()) {
            PartyroomId partyroomId = new PartyroomId(optional.get().id());
            long crewId = optional.get().crewId();
            return Optional.of(new PartyroomSessionDto(sessionId, userId, partyroomId, crewId));
        }
        return Optional.empty();
    }
}
