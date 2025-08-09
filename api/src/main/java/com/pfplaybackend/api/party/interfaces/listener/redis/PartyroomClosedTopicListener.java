package com.pfplaybackend.api.party.interfaces.listener.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.liveconnect.websocket.SimpMessageSender;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PartyroomClosedMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PartyroomClosedTopicListener implements MessageListener {
    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());
        try {
            PartyroomClosedMessage partyroomClosedMessage = objectMapper.readValue(jsonstring, PartyroomClosedMessage.class);
            messageSender.sendToGroup(partyroomClosedMessage.getPartyroomId().getId(), partyroomClosedMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
