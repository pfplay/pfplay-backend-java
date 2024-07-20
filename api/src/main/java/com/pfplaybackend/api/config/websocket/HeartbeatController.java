package com.pfplaybackend.api.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HeartbeatController {

    @MessageMapping("/heartbeat")
    @SendTo("/sub/heartbeat")
    public String sendMessage(MessageHeaders headers) {
        return "PONG";
    }
}
