package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.liveconnect.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.PartyroomAccessMessage;
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
            PartyroomAccessMessage partyroomAccessMessage = objectMapper.readValue(new String(message.getBody()), PartyroomAccessMessage.class);
            messageSender.sendToGroup(partyroomAccessMessage.getPartyroomId().getId(), partyroomAccessMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
