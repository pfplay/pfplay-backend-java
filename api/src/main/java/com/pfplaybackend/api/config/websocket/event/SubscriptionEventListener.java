package com.pfplaybackend.api.config.websocket.event;

import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.user.domain.value.UserId;
import io.lettuce.core.ScriptOutputType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener implements ApplicationListener<SessionSubscribeEvent> {

    //
    private final PartyroomInfoService partyroomInfoService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        Principal user = headerAccessor.getUser();
        assert user != null;
        UserId userId = UserId.create(UUID.fromString(user.getName()));
        ActivePartyroomWithMemberDto dto = partyroomInfoService.getMyActivePartyroomWithMemberId(userId).orElseThrow();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("partyroomId", dto.getId());
        sessionData.put("uid", user.getName());
        sessionData.put("memberId", dto.getMemberId());
        assert sessionId != null;
        redisTemplate.opsForValue().set(sessionId, sessionData);
        System.out.println("Session ID: " + sessionId + " has subscribed to " + destination);
    }
}