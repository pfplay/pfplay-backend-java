package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.liveconnect.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.ReactionMotionMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class ReactionMotionTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());
        try {
            ReactionMotionMessage reactionMotionMessage = objectMapper.readValue(jsonstring, ReactionMotionMessage.class);
            messageSender.sendToGroup(reactionMotionMessage.getPartyroomId().getId(), reactionMotionMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
