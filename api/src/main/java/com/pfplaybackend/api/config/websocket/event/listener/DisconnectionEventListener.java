package com.pfplaybackend.api.config.websocket.event.listener;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.manager.SessionCacheManager;
import com.pfplaybackend.api.config.websocket.exception.SessionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class DisconnectionEventListener implements ApplicationListener<SessionDisconnectEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ConnectionEventListener.class);
    private final SessionCacheManager sessionCacheManager;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal principal = headerAccessor.getUser();
        sessionCacheManager.deleteSessionCache(sessionId);
        logger.info("Web socket connection closed: " + sessionId);

        CloseStatus closeStatus = event.getCloseStatus();
        Integer closeStatusCode = closeStatus.getCode();
        logger.info(closeStatus.getReason());

        switch (closeStatusCode) {
            case 1000:
                logger.info("WebSocket session {} was closed normally (CLOSE_NORMAL).", sessionId);
                break;
            case 1001:
                logger.info("WebSocket session {} was closed because the client is going away (CLOSE_GOING_AWAY).", sessionId);
                break;
            case 1002:
                logger.warn("WebSocket session {} was closed due to protocol error (CLOSE_PROTOCOL_ERROR).", sessionId);
                break;
            case 1003:
                logger.warn("WebSocket session {} was closed due to unacceptable data (CLOSE_NOT_ACCEPTABLE).", sessionId);
                break;
            case 1006:
                logger.error("WebSocket session {} was closed abnormally (CLOSE_ABNORMAL).", sessionId);
                break;
            default:
                logger.warn("WebSocket session {} was closed with an unexpected status code: {}", sessionId, closeStatusCode);
                break;
        }
    }
}
