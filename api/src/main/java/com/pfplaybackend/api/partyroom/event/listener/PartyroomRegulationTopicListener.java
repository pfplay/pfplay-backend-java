package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.PlaybackMessage;
import com.pfplaybackend.api.partyroom.event.message.RegulationMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PartyroomRegulationTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            RegulationMessage deserialized = objectMapper.readValue(new String(message.getBody()), RegulationMessage.class);
            messageSender.sendToGroup(deserialized.getPartyroomId().getId(), deserialized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
