package com.pfplaybackend.api.config.websocket.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public class UnsubscriptionEventListener implements ApplicationListener<SessionUnsubscribeEvent> {
    private final static Logger logger = LoggerFactory.getLogger(UnsubscriptionEventListener.class);


    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("Web socket connection closed: " + sessionId);
    }
}
