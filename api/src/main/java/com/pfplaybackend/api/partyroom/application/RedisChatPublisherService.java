package com.pfplaybackend.api.partyroom.application;

import com.pfplaybackend.api.partyroom.presentation.dto.PartyroomSocketDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatPublisherService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, PartyroomSocketDto partyroomSocketDto) {
        redisTemplate.convertAndSend(topic.getTopic(), partyroomSocketDto);
    }
}
