package com.pfplaybackend.api.party.adapter.in.listener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.adapter.in.listener.CrewProfilePreCheckTopicListener;
import com.pfplaybackend.api.party.adapter.in.listener.GroupBroadcastTopicListener;
import com.pfplaybackend.api.party.adapter.in.listener.PlaybackDurationWaitTopicListener;
import com.pfplaybackend.api.party.adapter.in.listener.message.*;
import com.pfplaybackend.api.party.application.service.CrewProfileChangeHandler;
import com.pfplaybackend.api.party.application.service.PlaybackManagementService;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import com.pfplaybackend.realtime.sender.SimpMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Map;

import static java.util.Map.entry;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        SimpMessageSender simpMessageSender,
                                                        RedisTemplate<String, Object> redisTemplate,
                                                        DistributedLockExecutor distributedLockExecutor,
                                                        CrewProfileChangeHandler crewProfileService,
                                                        PlaybackManagementService playbackManagementService,
                                                        RedisMessagePublisher messagePublisher,
                                                        ObjectMapper objectMapper) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Standard broadcast listeners: deserialize → sendToGroup
        Map.<String, Class<? extends GroupBroadcastMessage>>ofEntries(
                entry("chat", OutgoingGroupChatMessage.class),
                entry("partyroom_deactivation", PartyroomDeactivationMessage.class),
                entry("partyroom_access", PartyroomAccessMessage.class),
                entry("partyroom_closed", PartyroomClosedMessage.class),
                entry("reaction_motion", ReactionMotionMessage.class),
                entry("reaction_aggregation", ReactionAggregationMessage.class),
                entry("crew_grade", CrewGradeMessage.class),
                entry("crew_penalty", CrewPenaltyMessage.class),
                entry("crew_profile", CrewProfileMessage.class),
                entry("dj_queue_change", DjQueueChangeMessage.class),
                entry("playback_start", PlaybackStartMessage.class)
        ).forEach((topic, type) ->
                container.addMessageListener(
                        new GroupBroadcastTopicListener<>(simpMessageSender, objectMapper, type),
                        new ChannelTopic(topic)));

        // Special listeners with business logic
        container.addMessageListener(new CrewProfilePreCheckTopicListener(objectMapper, distributedLockExecutor, crewProfileService, messagePublisher), new ChannelTopic("crew_profile_pre_check"));
        container.addMessageListener(new PlaybackDurationWaitTopicListener(redisTemplate, objectMapper, distributedLockExecutor, playbackManagementService), new PatternTopic("__keyevent@*__:expired"));
        return container;
    }
}
