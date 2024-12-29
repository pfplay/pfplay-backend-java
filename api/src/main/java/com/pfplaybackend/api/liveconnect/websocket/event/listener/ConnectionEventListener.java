package com.pfplaybackend.api.liveconnect.websocket.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class ConnectionEventListener implements ApplicationListener<SessionConnectEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ConnectionEventListener.class);

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("Received a new web socket connection: " + sessionId);
    }
}
