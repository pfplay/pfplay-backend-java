package com.pfplaybackend.api.partyroom.event;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class SimpleEventPublisher implements EventPublisher {

    // TODO redisTemplate

    /**
     * 이 시점에서는 Redis 토픽만 사용할 수 있음에 주의한다.
     * @param topic
     * @param message
     */
    @Override
    public void publish(ChannelTopic topic, Message message) {
        // TODO redisTemplate.convertAndSend(topic, message);
    }
}
