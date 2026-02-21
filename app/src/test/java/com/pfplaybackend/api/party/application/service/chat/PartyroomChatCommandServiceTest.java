package com.pfplaybackend.api.party.application.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.application.dto.chat.ChatMessageDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.realtime.port.SessionCachePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomChatCommandServiceTest {

    @Mock RedisTemplate<String, Object> redisTemplate;
    @Mock RedisMessagePublisher messagePublisher;
    @Mock SessionCachePort sessionCachePort;
    @Spy ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks PartyroomChatCommandService partyroomChatCommandService;

    @Test
    @DisplayName("유효한 세션으로 메시지를 전송하면 Redis에 publish된다")
    void sendMessage_validSession_publishesToRedis() {
        // given
        String sessionId = "session-1";
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("sessionId", sessionId);
        sessionMap.put("userId", Map.of("uid", 1L));
        sessionMap.put("partyroomId", Map.of("id", 10L));
        sessionMap.put("crewId", 5L);

        when(sessionCachePort.getSessionCache(sessionId)).thenReturn(Optional.of(sessionMap));
        when(redisTemplate.hasKey("PENALTY:CHAT_BAN:5")).thenReturn(false);

        // when
        partyroomChatCommandService.sendMessage(sessionId, "Hello!");

        // then
        verify(messagePublisher).publish(eq(MessageTopic.CHAT.topic()), any(ChatMessageDto.class));
    }

    @Test
    @DisplayName("세션 캐시가 없으면 예외가 발생한다")
    void sendMessage_noSession_throwsException() {
        // given
        when(sessionCachePort.getSessionCache("unknown")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> partyroomChatCommandService.sendMessage("unknown", "Hello!"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("채팅 밴 상태이면 메시지가 publish되지 않는다")
    void sendMessage_chatBanned_doesNotPublish() {
        // given
        String sessionId = "session-1";
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("sessionId", sessionId);
        sessionMap.put("userId", Map.of("uid", 1L));
        sessionMap.put("partyroomId", Map.of("id", 10L));
        sessionMap.put("crewId", 5L);

        when(sessionCachePort.getSessionCache(sessionId)).thenReturn(Optional.of(sessionMap));
        when(redisTemplate.hasKey("PENALTY:CHAT_BAN:5")).thenReturn(true);

        // when
        partyroomChatCommandService.sendMessage(sessionId, "Hello!");

        // then
        verify(messagePublisher, never()).publish(any(), any());
    }

    @Test
    @DisplayName("isPossibleChat — 채팅 밴 키가 없으면 true를 반환한다")
    void isPossibleChat_noBanKey_returnsTrue() {
        // given
        when(redisTemplate.hasKey("PENALTY:CHAT_BAN:5")).thenReturn(false);

        // when & then
        assertThat(partyroomChatCommandService.isPossibleChat(5L)).isTrue();
    }

    @Test
    @DisplayName("isPossibleChat — 채팅 밴 키가 있으면 false를 반환한다")
    void isPossibleChat_banKeyExists_returnsFalse() {
        // given
        when(redisTemplate.hasKey("PENALTY:CHAT_BAN:5")).thenReturn(true);

        // when & then
        assertThat(partyroomChatCommandService.isPossibleChat(5L)).isFalse();
    }
}
