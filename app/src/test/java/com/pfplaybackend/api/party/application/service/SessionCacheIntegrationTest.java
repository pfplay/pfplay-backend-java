package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class SessionCacheIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Redis 컨테이너에 값을 저장하고 조회할 수 있다")
    void redisSetAndGet() {
        // given
        String key = "test:session:user1";
        String value = "session-data-123";

        // when
        redisTemplate.opsForValue().set(key, value);
        Object result = redisTemplate.opsForValue().get(key);

        // then
        assertThat(result).isEqualTo(value);

        // cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("Redis TTL 설정이 올바르게 동작한다")
    void redisSetWithTtl() {
        // given
        String key = "test:session:ttl";
        String value = "expiring-data";

        // when
        redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        // then
        assertThat(ttl).isGreaterThan(0)
                .isLessThanOrEqualTo(60);

        // cleanup
        redisTemplate.delete(key);
    }
}
