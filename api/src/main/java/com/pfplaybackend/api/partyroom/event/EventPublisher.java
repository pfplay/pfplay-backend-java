package com.pfplaybackend.api.partyroom.event;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;

public interface EventPublisher {

    public void publish(ChannelTopic topic, Message message);
}
