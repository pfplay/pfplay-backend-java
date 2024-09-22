package com.pfplaybackend.api.config.websocket.event.listener;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.cache.SessionCacheManager;
import com.pfplaybackend.api.config.websocket.exception.SessionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UnsubscriptionEventListener implements ApplicationListener<SessionUnsubscribeEvent> {
    private final static Logger logger = LoggerFactory.getLogger(UnsubscriptionEventListener.class);
    private final SessionCacheManager sessionCacheManager;

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            logger.warn("Unauthorized session requested, UserId is null" + " Session ID: " + sessionId);
            throw ExceptionCreator.create(SessionException.UNAUTHORIZED_SESSION);
        }
        sessionCacheManager.deleteSessionCache(sessionId);

        logger.info("Session has unsubscribed, sessionId : " + sessionId);
    }
}
