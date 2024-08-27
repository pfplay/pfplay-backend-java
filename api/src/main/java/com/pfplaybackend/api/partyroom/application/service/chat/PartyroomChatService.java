package com.pfplaybackend.api.partyroom.application.service.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.OutgoingGroupChatMessage;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.dto.IncomingGroupChatMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {

    private static final Logger logger = LoggerFactory.getLogger(PartyroomChatService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagePublisher messagePublisher;

    private final ObjectMapper objectMapper;

    public void sendMessage(String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) {
        // TODO Get sender's information from DBMS or Cache By sessionId(String)
        // TODO Create OutgoingChatMessage for publish
        Object object = redisTemplate.opsForValue().get(sessionId);
        if (object == null) {
            throw ExceptionCreator.create(PartyroomException.NOT_FOUND_SESSION);
        }

        if (object instanceof Map) {
            try {
                PartyroomSessionDto sessionDto = objectMapper.convertValue(object, PartyroomSessionDto.class);
                OutgoingGroupChatMessage outgoingGroupChatMessage = OutgoingGroupChatMessage.from(sessionDto, incomingGroupChatMessage.getMessage());
                messagePublisher.publish(MessageTopic.CHAT, outgoingGroupChatMessage);
            } catch (IllegalArgumentException e) {
                logger.error(
                        "Cannot send message, SessionId: " + sessionId
                                + ", message: " + incomingGroupChatMessage.getMessage()
                                + ", ex: " + e.getMessage()
                );
            }
        }
    }
}
