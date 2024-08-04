package com.pfplaybackend.api.config.websocket.event;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.InvalidPartymemberException;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final PartyroomInfoService partyroomInfoService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        CustomAuthentication authentication = (CustomAuthentication) headerAccessor.getUser();
        if (authentication == null) {
            logger.error("Uid: null" + " Session ID: " + sessionId + " Partyroom ID: null");
            throw new InvalidPartymemberException();
        }

        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        UUID uid = userCredentials.getUid();
        UserId userId = UserId.create(uid);
        Optional<PartyroomId> optional = partyroomInfoService.getPartyroomId(userId);
        if (optional.isPresent()) {
            PartyroomId partyroomId = optional.get();
            PartyroomSessionDto partyroomSessionDto = partyroomInfoService.saveSession(sessionId, uid, partyroomId);
            logger.info(
                    "Uid: " + partyroomSessionDto.getUserId().getUid().toString()
                            + " Session ID: " + partyroomSessionDto.getSessionId()
                            + " Partyroom ID: " + partyroomSessionDto.getPartyroomId().getId()
                            + "has subscribed"
            );
            return;
        }

        logger.error("Uid: " + uid.toString() + " Session ID: " + sessionId + " Partyroom ID: " + optional);
        throw new InvalidPartymemberException();
    }
}