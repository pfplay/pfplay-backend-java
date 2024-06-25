package com.pfplaybackend.api.partyroom.event;

import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    /**
     *
     * @param topicType
     * @param object
     */
    public void publish(MessageTopic topicType, Object object) {
        //
        redisTemplate.convertAndSend(topicType.toString().toLowerCase(), object);
    }
}