package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.party.application.service.CrewProfileChangeHandler;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfilePreCheckMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
@AllArgsConstructor
public class CrewProfilePreCheckTopicListener implements MessageListener {

    private ObjectMapper objectMapper;
    private DistributedLockExecutor distributedLockExecutor;
    private CrewProfileChangeHandler crewProfileService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CrewProfilePreCheckMessage deserialized = objectMapper.readValue(new String(message.getBody()), CrewProfilePreCheckMessage.class);
            String suffixId = deserialized.getUserId().getUid().toString();
            distributedLockExecutor.performTaskWithLock(suffixId, () -> {
                crewProfileService.preCheck(deserialized);
                return null;
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", new String(message.getBody()), e);
            return;
        }
    }
}
