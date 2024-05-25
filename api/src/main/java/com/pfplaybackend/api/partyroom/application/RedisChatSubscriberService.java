package com.pfplaybackend.api.partyroom.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.enums.MessageType;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatMessageTypeException;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
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
            final String messageChannel = (String) redisTemplate.getStringSerializer().deserialize(message.getChannel());

            if (chatDto.getMessageType().getName().equals(MessageType.CHAT.getName())) {
                messagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChatroomId(), chatDto);
                return;
            }

            if (chatDto.getMessageType().getName().equals(MessageType.PENALTY.getName())) {
                messagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChatroomId(), chatDto);
                return;
            }

            if (chatDto.getMessageType().getName().equals(MessageType.PROMOTE.getName())) {
                messagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChatroomId(), chatDto);
                return;
            }

            throw new UnsupportedChatMessageTypeException("Unsupported ChatMessageType request");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
