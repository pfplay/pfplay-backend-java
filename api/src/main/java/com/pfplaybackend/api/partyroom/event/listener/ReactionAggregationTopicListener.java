package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.ReactionAggregationMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class ReactionAggregationTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());
        try {
            ReactionAggregationMessage reactionAggregationMessage = objectMapper.readValue(jsonstring, ReactionAggregationMessage.class);
            messageSender.sendToGroup(reactionAggregationMessage.getPartyroomId().getId(), reactionAggregationMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
