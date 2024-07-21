package com.pfplaybackend.api.config.websocket.event;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.config.oauth2.dto.CustomUserPrincipal;
import com.pfplaybackend.api.config.websocket.service.SessionEventService;
import com.pfplaybackend.api.partyroom.exception.InvalidJWTTokenException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SessionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
    private final SessionEventService sessionEventService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("Received a new web socket connection: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("Web socket connection closed: " + sessionId);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) throws InvalidJWTTokenException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        CustomAuthentication authentication = (CustomAuthentication) headerAccessor.getUser();
        if (authentication == null) {
            throw new InvalidJWTTokenException("Invalid JWT Token requested to socket session");
        }
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        UUID uid = userCredentials.getUid();

        sessionEventService.saveSession(sessionId, destination, uid);

        logger.info("Uid: " + uid.toString() + " Session ID: " + sessionId + " has subscribed to " + destination);
    }
}
