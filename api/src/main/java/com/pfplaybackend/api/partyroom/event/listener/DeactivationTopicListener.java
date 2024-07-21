package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.DeactivationMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class DeactivationTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());
        try {
            DeactivationMessage deactivationMessage = objectMapper.readValue(jsonstring, DeactivationMessage.class);
            messageSender.sendToGroup(deactivationMessage.getPartyroomId().getId(),deactivationMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
