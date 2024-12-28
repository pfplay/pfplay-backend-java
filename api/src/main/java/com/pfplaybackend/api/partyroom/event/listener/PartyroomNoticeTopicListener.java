package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.liveconnect.websocket.SimpMessageSender;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PartyroomNoticeTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }
}
