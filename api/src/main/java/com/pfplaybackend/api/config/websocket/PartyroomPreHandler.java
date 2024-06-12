package com.pfplaybackend.api.config.websocket;

import com.pfplaybackend.api.config.jwt.JwtValidator;
import com.pfplaybackend.api.config.jwt.dto.UserAuthenticationDto;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.repository.PartyroomUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@AllArgsConstructor
@Slf4j
public class PartyroomPreHandler implements ChannelInterceptor {
    private final PartyroomUserRepository partyroomUserRepository;
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
//        StompCommand stompCommand = stompHeaderAccessor.getCommand();
//        String sessionId = stompHeaderAccessor.getSessionId();
//
//        if (stompCommand == StompCommand.CONNECT) {
//            // todo put sessionId and jwt token into redis cache
//            CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null) {
//                log.error("login first");
//            }
//
//            UserAuthenticationDto userAuthenticationDto = (UserAuthenticationDto) authentication.getPrincipal();
//            partyroomUserRepository.save(PartyroomUser.builder()
//                            .userId(userAuthenticationDto.getUserId())
//                            .sessionId(sessionId)
//                    .build());
//
//
//
//            return message;
//        }
//
//        if (stompCommand == StompCommand.DISCONNECT) {
//            // todo delete sessionId and jwt token which in redis cache
//
//            return message;
//
//        }

        return message;
    }
}
