package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.realtime.sender.SimpMessageSender;
import com.pfplaybackend.api.party.adapter.in.listener.message.PartyroomDeactivationMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
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
            log.error("Failed to deserialize message: {}", jsonstring, e);
            return;
        }
    }
}
