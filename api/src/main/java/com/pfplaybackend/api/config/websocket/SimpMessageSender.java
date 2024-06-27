package com.pfplaybackend.api.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpMessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendToGroup(String topic, Object object) {
        System.out.println(topic + ": " + object);
        simpMessagingTemplate.convertAndSend(topic, object);
    }

    public void sendToOne(String sessionId, String message) {
        System.out.println(sessionId + ": " + message);
        // TODO 특정 사용자에게만 호출
        // simpMessagingTemplate.convertAndSendToUser();
    }
}