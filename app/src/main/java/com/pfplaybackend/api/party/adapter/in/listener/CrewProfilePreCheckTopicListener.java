package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfileMessage;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfilePreCheckMessage;
import com.pfplaybackend.api.party.application.dto.command.CrewProfilePreCheckCommand;
import com.pfplaybackend.api.party.application.service.CrewProfileChangeEventHandler;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
@AllArgsConstructor
public class CrewProfilePreCheckTopicListener implements MessageListener {

    private ObjectMapper objectMapper;
    private DistributedLockExecutor distributedLockExecutor;
    private CrewProfileChangeEventHandler crewProfileService;
    private RedisMessagePublisher messagePublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CrewProfilePreCheckMessage deserialized = objectMapper.readValue(new String(message.getBody()), CrewProfilePreCheckMessage.class);
            CrewProfilePreCheckCommand command = new CrewProfilePreCheckCommand(
                    deserialized.userId(), deserialized.nickname(),
                    deserialized.avatarFaceUri(), deserialized.avatarBodyUri(), deserialized.avatarIconUri(),
                    deserialized.avatarCompositionType(),
                    deserialized.combinePositionX(), deserialized.combinePositionY(),
                    deserialized.offsetX(), deserialized.offsetY(), deserialized.scale());
            String suffixId = deserialized.userId().getUid().toString();
            distributedLockExecutor.performTaskWithLock(suffixId, () -> {
                crewProfileService.preCheck(command).ifPresent(changed ->
                        messagePublisher.publish(MessageTopic.CREW_PROFILE.topic(),
                                CrewProfileMessage.from(changed)));
                return null;
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", new String(message.getBody()), e);
        }
    }
}
