package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.PlaybackStartMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;


@AllArgsConstructor
public class PlaybackStartTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PlaybackStartMessage deserialized = objectMapper.readValue(new String(message.getBody()), PlaybackStartMessage.class);
            messageSender.sendToGroup(deserialized.getPartyroomId().getId(), deserialized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}