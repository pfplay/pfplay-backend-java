package com.pfplaybackend.api.liveconnect.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpMessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendToGroup(long groupId, Object object) {
        simpMessagingTemplate.convertAndSend("/sub/partyrooms/" + groupId, object);
    }

    public void sendToOne(String user, String message) {
        simpMessagingTemplate.convertAndSendToUser(user, "/sub/heartbeat", message);
    }
}