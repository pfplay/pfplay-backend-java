package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.application.PartyroomChatService;

import com.pfplaybackend.api.partyroom.exception.InvalidPartyroomIdRequestException;
import com.pfplaybackend.api.partyroom.exception.UnsupportedSocketRequestException;
import com.pfplaybackend.api.partyroom.presentation.api.PartyroomChatApi;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Chat API")
@RestController
@RequiredArgsConstructor
public class PartyroomChatController implements PartyroomChatApi {
    private final PartyroomChatService partyroomSocketService;

    @Operation(summary = "Send a message to WebSocket topic")
    @MessageMapping("/partyroom/api/v1/send/chat")
    public ResponseEntity<?> sendMessage(MessageHeaders headers, ChatDto chatDto) {
        try {
            //TODO; uid 파싱 부분 AOP로 분리
            final UUID uid = ((UserCredentials)((CustomAuthentication) headers.get("simpUser")).getPrincipal()).getUid();
            chatDto.getFromUser().setUserId(new UserId(uid));

            final String partyroomId = chatDto.getFromUser().getPartyroomId();
            if (!partyroomSocketService.getPartyroomId(uid).equals(partyroomId)) {
                 throw new InvalidPartyroomIdRequestException("Invalid Partyroom Id");
            }
            partyroomSocketService.sendMessage(chatDto);
            return ResponseEntity.ok(ApiCommonResponse.success("OK"));
        } catch (UnsupportedSocketRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
