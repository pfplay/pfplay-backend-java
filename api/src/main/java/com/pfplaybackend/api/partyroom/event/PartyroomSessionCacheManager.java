package com.pfplaybackend.api.partyroom.event;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.event.handler.SessionCacheManager;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.repository.PartymemberRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomSessionCacheManager implements SessionCacheManager {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PartymemberRepository partymemberRepository;

    @Transactional
    public void saveSessionCache(String sessionId, UserId userId, String destination) {
        if (destination.contains("partyroom")) {
            Optional<PartyroomSessionDto> partyroomSessionData = createSessionData(sessionId, userId);
            if (partyroomSessionData.isEmpty()) {
                throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
            }

            PartyroomSessionDto sessionData = partyroomSessionData.get();
            redisTemplate.opsForValue().set(sessionId, sessionData);
        }
    }

    @Transactional
    public void deleteSessionCache(String sessionId){
        redisTemplate.delete(sessionId);
    }

    private Optional<PartyroomSessionDto> createSessionData(String sessionId, UserId userId) {
        Optional<PartymemberData> data = partymemberRepository.findByUserId(userId);
        if (data.isPresent()) {
            PartyroomId partyroomId = data.get().getPartyroomData().getPartyroomId();
            long memberId = data.get().getId();
            return Optional.of(PartyroomSessionDto.create(sessionId, userId, partyroomId, memberId));
        }
        return Optional.empty();
    }
}
