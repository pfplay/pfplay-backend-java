package com.pfplaybackend.api.liveconnect.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class HeartbeatController {

    private final SimpMessageSender simpMessageSender;

    @MessageMapping("/heartbeat")
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        simpMessageSender.sendToOne(principal.getName(), "PONG");
    }
}