package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.realtime.sender.SimpMessageSender;
import com.pfplaybackend.api.party.adapter.in.listener.message.PartyroomAccessMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
@AllArgsConstructor
public class PartyroomAccessTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PartyroomAccessMessage partyroomAccessMessage = objectMapper.readValue(new String(message.getBody()), PartyroomAccessMessage.class);
            messageSender.sendToGroup(partyroomAccessMessage.getPartyroomId().getId(), partyroomAccessMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", new String(message.getBody()), e);
            return;
        }
    }
}
