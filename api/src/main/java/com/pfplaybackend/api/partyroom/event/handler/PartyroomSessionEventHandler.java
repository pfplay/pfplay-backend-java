package com.pfplaybackend.api.partyroom.event.handler;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.event.handler.SessionEventHandler;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
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
public class PartyroomSessionEventHandler implements SessionEventHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PartymemberRepository partymemberRepository;

    @Transactional
    public void saveSessionCache(String sessionId, UserId userId, String destination) {
        if (destination.contains("partyroom")) {
            Optional<PartyroomSessionData> partyroomSessionData = createSessionData(sessionId, userId);
            if (partyroomSessionData.isEmpty()) {
                throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
            }

            PartyroomSessionData sessionData = partyroomSessionData.get();
            redisTemplate.opsForValue().set(sessionId, sessionData);
        }
    }

    private Optional<PartyroomSessionData> createSessionData(String sessionId, UserId userId) {
        Optional<PartymemberData> data = partymemberRepository.findByUserId(userId);
        if (data.isPresent()) {
            PartyroomId partyroomId = data.get().getPartyroomData().getPartyroomId();
            return Optional.of(PartyroomSessionData.create(sessionId, userId, partyroomId));
        }
        return Optional.empty();
    }
}
