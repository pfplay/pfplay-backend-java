package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.AccessMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PartyroomAccessTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            AccessMessage accessMessage = objectMapper.readValue(new String(message.getBody()), AccessMessage.class);
            messageSender.sendToGroup(accessMessage.getPartyroomId().getId(), accessMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
