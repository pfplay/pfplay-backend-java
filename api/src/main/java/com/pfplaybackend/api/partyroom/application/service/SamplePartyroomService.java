package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SamplePartyroomService {

    private final RedisMessagePublisher redisMessagePublisher;

    public void method_a() {
        redisMessagePublisher.publish(MessageTopic.SAMPLE, new Object());
    }
}