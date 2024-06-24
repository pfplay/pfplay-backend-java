package com.pfplaybackend.api.config.redis;

import com.pfplaybackend.api.partyroom.application.RedisChatSubscriberService;
import com.pfplaybackend.api.partyroom.event.listener.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        @Qualifier("chat") MessageListenerAdapter chatListenerAdapter,
                                                        @Qualifier("partyroomAccess") MessageListenerAdapter partyroomAccessListenerAdapter,
                                                        @Qualifier("partyroomNotice") MessageListenerAdapter partyroomNoticeListenerAdapter,
                                                        @Qualifier("partymemberRegulation") MessageListenerAdapter partymemberRegulationListenerAdapter,
                                                        @Qualifier("djPlayback") MessageListenerAdapter djPlaybackListenerAdapter,
                                                        @Qualifier("djQueue") MessageListenerAdapter djQueueListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatListenerAdapter, new ChannelTopic("chat"));
        container.addMessageListener(partyroomAccessListenerAdapter, new ChannelTopic("partyroomAccess"));
        container.addMessageListener(partyroomNoticeListenerAdapter, new ChannelTopic("partyroomNotice"));
        container.addMessageListener(partymemberRegulationListenerAdapter, new ChannelTopic("partymemberRegulation"));
        container.addMessageListener(djPlaybackListenerAdapter, new ChannelTopic("djPlayback"));
        container.addMessageListener(djQueueListenerAdapter, new ChannelTopic("djQueue"));

        return container;
    }

    @Bean
    @Qualifier("chat")
    public MessageListenerAdapter chatTopicListenerAdapter(RedisChatSubscriberService listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }

    @Bean
    @Qualifier("partyroomAccess")
    public MessageListenerAdapter partyroomAccessListenerAdapter(PartyroomAccessTopicListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }

    @Bean
    @Qualifier("partyroomNotice")
    public MessageListenerAdapter partyroomNoticeListenerAdapter(PartyroomNoticeTopicListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }

    @Bean
    @Qualifier("partymemberRegulation")
    public MessageListenerAdapter partymemberRegulationTopicListenerAdapter(PartymemberRegulationTopicListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }

    @Bean
    @Qualifier("djPlayback")
    public MessageListenerAdapter djPlaybackListenerAdapter(DJPlaybackListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }

    @Bean
    @Qualifier("djQueue")
    public MessageListenerAdapter djQueueListenerAdapter(DJQueueTopicListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }
}
