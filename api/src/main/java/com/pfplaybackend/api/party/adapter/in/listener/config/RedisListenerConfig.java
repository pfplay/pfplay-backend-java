package com.pfplaybackend.api.party.adapter.in.listener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.party.adapter.in.listener.*;
import com.pfplaybackend.realtime.sender.SimpMessageSender;
import com.pfplaybackend.api.party.application.service.CrewProfileService;
import com.pfplaybackend.api.party.application.service.PlaybackManagementService;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

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
        container.addMessageListener(new PartyroomClosedTopicListener(simpMessageSender, objectMapper), new ChannelTopic("partyroom_closed"));
        container.addMessageListener(new ReactionMotionTopicListener(simpMessageSender, objectMapper), new ChannelTopic("reaction_motion"));
        container.addMessageListener(new ReactionAggregationTopicListener(simpMessageSender, objectMapper), new ChannelTopic("reaction_aggregation"));
        container.addMessageListener(new CrewGradeTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_grade"));
        container.addMessageListener(new CrewPenaltyTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_penalty"));
        container.addMessageListener(new CrewProfileTopicListener(simpMessageSender, objectMapper), new ChannelTopic("crew_profile"));
        container.addMessageListener(new CrewProfilePreCheckTopicListener(objectMapper, distributedLockExecutor, crewProfileService), new ChannelTopic("crew_profile_pre_check"));
        container.addMessageListener(new DjQueueChangeTopicListener(simpMessageSender, objectMapper), new ChannelTopic("dj_queue_change"));
        container.addMessageListener(new PlaybackStartTopicListener(simpMessageSender, objectMapper), new ChannelTopic("playback_start"));
        container.addMessageListener(new PlaybackSkipTopicListener(simpMessageSender, objectMapper), new ChannelTopic("playback_skip"));
        container.addMessageListener(new PlaybackDurationWaitTopicListener(redisTemplate, objectMapper, distributedLockExecutor, playbackManagementService), new PatternTopic("__keyevent@*__:expired"));
        return container;
    }
}
