package com.pfplaybackend.api.config.websocket;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.exception.InvalidJWTTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PartyroomChannelInterceptor implements ChannelInterceptor {
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        if(!isValidJwtToken(stompHeaderAccessor)) {
            throw new InvalidJWTTokenException("Invalid JWT Token");
        }
        return message;
    }

    private boolean isValidJwtToken(StompHeaderAccessor stompHeaderAccessor) {
        final CustomAuthentication authentication = (CustomAuthentication) stompHeaderAccessor.getHeader("simpUser");
        if (authentication == null) {
            return false;
        }

        final UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        final UUID uid = userCredentials.getUserId().getUid();
        if (uid == null) {
            return false;
        }

        return true;
    }
}
