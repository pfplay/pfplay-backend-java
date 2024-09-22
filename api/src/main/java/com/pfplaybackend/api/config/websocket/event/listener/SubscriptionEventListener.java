package com.pfplaybackend.api.config.websocket.event.listener;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.websocket.cache.SessionCacheManager;
import com.pfplaybackend.api.config.websocket.exception.SessionException;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener implements ApplicationListener<SessionSubscribeEvent> {
    private final static Logger logger = LoggerFactory.getLogger(SubscriptionEventListener.class);
    private final SessionCacheManager sessionCacheManager;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            logger.warn("Unauthorized session requested, UserId is null" + " Session ID: " + sessionId);
            throw ExceptionCreator.create(SessionException.UNAUTHORIZED_SESSION);
        }
        UserId userId = UserId.create(UUID.fromString(principal.getName()));
        sessionCacheManager.saveSessionCache(sessionId, userId, destination);

        logger.info("Session has subscribed, sessionId : " + sessionId + ", userId : " + userId + ", destination : " + destination);
    }
}
