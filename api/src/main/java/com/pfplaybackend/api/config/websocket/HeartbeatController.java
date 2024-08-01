package com.pfplaybackend.api.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
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