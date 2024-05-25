package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserAuthenticationDto;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.application.PartyroomChatService;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatMessageTypeException;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatRequestException;
import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.presentation.api.PartyroomChatApi;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PartyroomChatController implements PartyroomChatApi {
    private PartyroomChatService partyroomChatService;

    @MessageMapping("/chat/send")
    public ResponseEntity<?> sendChat(ChatDto chatDto) {
        try {
            CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
            UserAuthenticationDto userAuthenticationDto = (UserAuthenticationDto) authentication.getPrincipal();
            String userIdUid = userAuthenticationDto.getUserId().getUid().toString();
            String chatroomId = partyroomChatService.findChatroomId(userIdUid);
            chatDto.setChatroomId(chatroomId);
            partyroomChatService.sendChat(chatDto);
            return ResponseEntity.ok(ApiCommonResponse.success("OK"));
        } catch (UnsupportedChatMessageTypeException | UnsupportedChatRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
