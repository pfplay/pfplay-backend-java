package com.pfplaybackend.api.partyroom.event;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleEventBroadcaster implements EventBroadcaster {

    private final SimpMessagingTemplate webSocketMessagingTemplate;

    @Override
    public void broadcast(String topic, String message) {
        System.out.println(topic + ": " + message);
        // webSocketMessagingTemplate.convertAndSend(topic, message);
        // webSocketMessagingTemplate.convertAndSendToUser();
    }
}