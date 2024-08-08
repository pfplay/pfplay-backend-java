package com.pfplaybackend.api.config.websocket.event;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.config.websocket.application.service.SessionEventService;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.InvalidPartymemberException;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener implements ApplicationListener<SessionSubscribeEvent> {
    //
    private final static Logger logger = LoggerFactory.getLogger(SubscriptionEventListener.class);
    private final SessionEventService sessionEventService;
    private final PartyroomInfoService partyroomInfoService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        CustomAuthentication authentication = (CustomAuthentication) headerAccessor.getUser();
        if (authentication == null) {
            logger.error("Invalid Partymember, UserId is null" + " Session ID: " + sessionId);
            throw new InvalidPartymemberException();
        }

        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        UUID uid = userCredentials.getUid();
        UserId userId = UserId.create(uid);

        Optional<PartyroomId> optional = partyroomInfoService.getPartyroomId(userId);
        if (optional.isPresent()) {
            PartyroomId partyroomId = optional.get();
            sessionEventService.saveSession(sessionId, userId, partyroomId);
            logger.info(
                    "Session ID: " + sessionId
                            + " UserID: " + uid
                            + " Partyroom ID: " + partyroomId
                            + "has subscribed"
            );
            return;
        }

        logger.error(
                "Invalid Partymember, PartyroomId is null, "
                        + "Session ID: " + sessionId
                        + " UserID: " + uid
        );
        throw new InvalidPartymemberException();
    }
}