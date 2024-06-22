package com.pfplaybackend.api.partyroom.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.exception.UnknownSocketDtoException;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PenaltyDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PromoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatSubscriberService implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            final String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            final ChatDto chatDto = objectMapper.readValue(publishMessage, ChatDto.class);
            final String partyroomId = chatDto.getFromUser().getPartyroomId();
            final String payload = "/sub/partyroom/" + partyroomId;
            messagingTemplate.convertAndSend(payload, chatDto);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
