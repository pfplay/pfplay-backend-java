package com.pfplaybackend.api.party.application.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.dto.chat.ChatMessageDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.party.application.port.out.ChatPenaltyCachePort;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.realtime.port.SessionCachePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomChatCommandService {

    private static final Logger logger = LoggerFactory.getLogger(PartyroomChatCommandService.class);

    private final ChatPenaltyCachePort chatPenaltyCachePort;
    private final RedisMessagePublisher messagePublisher;
    private final SessionCachePort sessionCachePort;
    private final Clock clock;

    private final ObjectMapper objectMapper;

    public void sendMessage(String sessionId, String content) {
        Optional<Object> optional = sessionCachePort.getSessionCache(sessionId);

        if (optional.isEmpty()) {
            throw ExceptionCreator.create(PartyroomException.CACHE_MISSED_SESSION);
        }

        final Object object = optional.get();
        if (object instanceof Map) {
            try {
                PartyroomSessionDto sessionDto = objectMapper.convertValue(object, PartyroomSessionDto.class);
                if(isPossibleChat(sessionDto.crewId())) {
                    ChatMessageDto chatPayload = ChatMessageDto.from(sessionDto, content, clock.millis());
                    messagePublisher.publish(MessageTopic.CHAT.topic(), chatPayload);
                }
            } catch (IllegalArgumentException e) {
                logger.error(
                        "Cannot send message, SessionId: " + sessionId
                                + ", message: " + content
                                + ", ex: " + e.getMessage()
                );
            }
        }
    }

    public boolean isPossibleChat(Long crewIdValue) {
        return !chatPenaltyCachePort.isChatBanned(crewIdValue);
    }
}
