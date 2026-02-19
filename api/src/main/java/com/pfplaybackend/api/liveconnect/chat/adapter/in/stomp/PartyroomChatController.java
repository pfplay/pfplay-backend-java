package com.pfplaybackend.api.liveconnect.chat.adapter.in.stomp;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.chat.PartyroomChatService;

import com.pfplaybackend.api.party.adapter.in.web.api.PartyroomChatApi;
import com.pfplaybackend.api.party.adapter.in.web.dto.IncomingGroupChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chat API")
@RestController
@RequiredArgsConstructor
public class PartyroomChatController implements PartyroomChatApi {
    private final PartyroomChatService partyroomChatService;

    @Operation(summary = "Send a message to WebSocket topic")
    @MessageMapping("/groups/{chatroomId}/send")
    public ResponseEntity<?> sendGroupMessage(@Header("simpSessionId") String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) {
        // chatroomId == partyroomId
        partyroomChatService.sendMessage(sessionId, incomingGroupChatMessage);
        return ResponseEntity.ok(ApiCommonResponse.success("OK"));
    }

    @Operation(summary = "Send a message to WebSocket topic")
    @MessageMapping("/private/{chatroomId}/send")
    public ResponseEntity<?> sendPrivateMessage(@Header("simpSessionId") String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) {
        // TODO
        return ResponseEntity.ok(ApiCommonResponse.success("OK"));
    }
}