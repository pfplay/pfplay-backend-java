package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.liveconnect.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.OutgoingGroupChatMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class ChatTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            OutgoingGroupChatMessage deserialized = objectMapper.readValue(new String(message.getBody()), OutgoingGroupChatMessage.class);
            messageSender.sendToGroup(deserialized.getPartyroomId().getId(), deserialized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
