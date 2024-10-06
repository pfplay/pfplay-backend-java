package com.pfplaybackend.api.partyroom.application.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.cache.SessionCacheManager;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.OutgoingGroupChatMessage;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.dto.IncomingGroupChatMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {

    private static final Logger logger = LoggerFactory.getLogger(PartyroomChatService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagePublisher messagePublisher;
    private final SessionCacheManager sessionCacheManager;

    private final ObjectMapper objectMapper;

    public void sendMessage(String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) {
        Optional<Object> optional = sessionCacheManager.getSessionCache(sessionId);

        if (optional.isEmpty()) {
            throw ExceptionCreator.create(PartyroomException.CACHE_MISSED_SESSION);
        }

        final Object object = optional.get();
        if (object instanceof Map) {
            try {
                PartyroomSessionDto sessionDto = objectMapper.convertValue(object, PartyroomSessionDto.class);
                if(isPossibleChat(sessionDto.getCrewId())) {
                    OutgoingGroupChatMessage outgoingGroupChatMessage = OutgoingGroupChatMessage.from(sessionDto, incomingGroupChatMessage.getContent());
                    messagePublisher.publish(MessageTopic.CHAT, outgoingGroupChatMessage);
                }
            } catch (IllegalArgumentException e) {
                logger.error(
                        "Cannot send message, SessionId: " + sessionId
                                + ", message: " + incomingGroupChatMessage.getContent()
                                + ", ex: " + e.getMessage()
                );
            }
        }
    }

    public boolean isPossibleChat(Long crewIdValue) {
        String key = "PENALTY:CHAT_BAN:" + crewIdValue;
        return Boolean.FALSE.equals(redisTemplate.hasKey(key));
    }
}
