package com.pfplaybackend.api.partyroom.application.service.cache;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.cache.SessionCacheManager;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
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
    private final PartyroomInfoService partyroomInfoService;

    @Transactional
    public void saveSessionCache(String sessionId, UserId userId, String destination) {
        String[] parts = destination.split("/");
        if(parts[1].equals("sub")) {
            String topic = parts[2];
            String separator = parts[3];
            if (topic.equals("partyroom")) {
                Optional<PartyroomSessionDto> PartyroomSessionDto = createSessionData(sessionId, userId);
                if (PartyroomSessionDto.isEmpty()) {
                    throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
                }
                PartyroomSessionDto sessionData = PartyroomSessionDto.get();
                // TODO Value: "crewInfo", Key: "sessionId"
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
        Optional<ActivePartyroomWithCrewDto> optional = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
        if (optional.isPresent()) {
            PartyroomId partyroomId = new PartyroomId(optional.get().getId());
            long crewId = optional.get().getCrewId();
            return Optional.of(new PartyroomSessionDto(sessionId, userId, partyroomId, crewId));
        }
        return Optional.empty();
    }
}
