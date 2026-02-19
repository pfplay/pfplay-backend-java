package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.realtime.sender.SimpMessageSender;
import com.pfplaybackend.api.party.adapter.in.listener.message.DjQueueChangeMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class DjQueueChangeTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            DjQueueChangeMessage djQueueChangeMessage = objectMapper.readValue(new String(message.getBody()), DjQueueChangeMessage.class);
            messageSender.sendToGroup(djQueueChangeMessage.getPartyroomId().getId(), djQueueChangeMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
