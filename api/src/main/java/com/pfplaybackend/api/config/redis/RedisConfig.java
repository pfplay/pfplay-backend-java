package com.pfplaybackend.api.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.application.service.CrewProfileService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.application.service.lock.DistributedLockExecutor;
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
import org.springframework.data.redis.listener.PatternTopic;
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

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }

    // TODO Create Service Registry and Register Method (24.09.22)

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        SimpMessageSender simpMessageSender,
                                                        RedisTemplate<String, Object> redisTemplate,
                                                        DistributedLockExecutor distributedLockExecutor,
                                                        CrewProfileService crewProfileService,
                                                        PlaybackManagementService playbackManagementService,
                                                        ObjectMapper objectMapper) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new ChatTopicListener(simpMessageSender, objectMapper), new ChannelTopic("chat"));
        container.addMessageListener(new PartyroomDeactivationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("partyroom_deactivation"));
        container.addMessageListener(new PartyroomAccessTopicListener(simpMessageSender, objectMapper), new ChannelTopic("partyroom_access"));
        container.addMessageListener(new PartyroomNoticeTopicListener(simpMessageSender, objectMapper), new ChannelTopic("partyroom_notice"));
        container.addMessageListener(new ReactionMotionTopicListener(simpMessageSender, objectMapper), new ChannelTopic("reaction_motion"));
        container.addMessageListener(new ReactionAggregationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("reaction_aggregation"));
        container.addMessageListener(new CrewGradeTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_grade"));
        container.addMessageListener(new CrewPenaltyTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_penalty"));
        container.addMessageListener(new CrewProfileTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_profile"));
        container.addMessageListener(new CrewProfilePreCheckTopicListener(objectMapper, distributedLockExecutor, crewProfileService), new ChannelTopic("crew_profile_pre_check"));
        container.addMessageListener(new PlaybackStartTopicListener(simpMessageSender, objectMapper), new ChannelTopic("playback_skip"));
        container.addMessageListener(new PlaybackSkipTopicListener(simpMessageSender, objectMapper), new ChannelTopic("playback_start"));
        container.addMessageListener(new PlaybackDurationWaitTopicListener(redisTemplate, objectMapper, distributedLockExecutor, playbackManagementService), new PatternTopic("__keyevent@*__:expired"));
        return container;
    }
}
