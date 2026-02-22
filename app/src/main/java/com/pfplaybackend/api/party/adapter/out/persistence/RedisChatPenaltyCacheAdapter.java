package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.application.port.out.ChatPenaltyCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisChatPenaltyCacheAdapter implements ChatPenaltyCachePort {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "PENALTY:CHAT_BAN:";

    @Override
    public void recordChatBan(Long crewId, int durationSeconds) {
        String key = KEY_PREFIX + crewId;
        redisTemplate.opsForValue().set(key, durationSeconds, Duration.ofSeconds(durationSeconds));
    }

    @Override
    public boolean isChatBanned(Long crewId) {
        String key = KEY_PREFIX + crewId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
