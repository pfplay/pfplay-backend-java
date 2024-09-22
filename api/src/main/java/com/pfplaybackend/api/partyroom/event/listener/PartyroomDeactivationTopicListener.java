package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.PartyroomDeactivationMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PartyroomDeactivationTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());
        try {
            PartyroomDeactivationMessage partyroomDeactivationMessage = objectMapper.readValue(jsonstring, PartyroomDeactivationMessage.class);
            messageSender.sendToGroup(partyroomDeactivationMessage.getPartyroomId().getId(), partyroomDeactivationMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
