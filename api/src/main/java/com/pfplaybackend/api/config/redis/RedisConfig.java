package com.pfplaybackend.api.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.listener.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        //
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        //
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        SimpMessageSender simpMessageSender, ObjectMapper objectMapper) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new ChatTopicListener(simpMessageSender, objectMapper), new ChannelTopic("chat"));
        container.addMessageListener(new PartyroomAccessTopicListener(simpMessageSender, objectMapper), new ChannelTopic("access"));
        container.addMessageListener(new MotionTopicListener(simpMessageSender, objectMapper), new ChannelTopic("motion"));
        container.addMessageListener(new AggregationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("aggregation"));
        container.addMessageListener(new PartyroomNoticeTopicListener(simpMessageSender, objectMapper), new ChannelTopic("regulation"));
        container.addMessageListener(new PartyroomRegulationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("notice"));
        container.addMessageListener(new PlaybackTopicListener(simpMessageSender, objectMapper), new ChannelTopic("playback"));
        container.addMessageListener(new DeactivationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("deactivation"));
        return container;
    }
}
