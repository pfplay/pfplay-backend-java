package com.pfplaybackend.api.partyroom.event;

import com.pfplaybackend.api.partyroom.domain.model.enums.MessageTopic;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class SimpleEventPublisher implements EventPublisher {


    // TODO redisTemplate
    private RedisTemplate<ChannelTopic, Message> redisTemplate;

    /**
     *
     * @param topicType
     * @param object
     */
    @Override
    public void publish(MessageTopic topicType, Object object) {
        // TODO Convert To Enum(MessageTopic) to ChannelTopic Class
        // ChannelTopic channelTopic = new ChannelTopic("partyroom");
        // TODO Create Message
        // redisTemplate.convertAndSend();
    }



}
