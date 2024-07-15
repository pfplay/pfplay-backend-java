package com.pfplaybackend.api.config.websocket;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.exception.InvalidPartyroomIdRequestException;
import com.pfplaybackend.api.partyroom.exception.UnsupportedSocketRequestException;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HeartbeatController {

    @MessageMapping("/heartbeat")
    @SendTo("/sub/heartbeat")
    public String sendMessage(MessageHeaders headers) {
        return "PONG";
    }
}
