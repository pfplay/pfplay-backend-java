package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.application.service.CrewProfileService;
import com.pfplaybackend.api.partyroom.application.service.lock.DistributedLockExecutor;
import com.pfplaybackend.api.partyroom.event.message.CrewProfilePreCheckMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class CrewProfilePreCheckTopicListener implements MessageListener {

    private ObjectMapper objectMapper;
    private DistributedLockExecutor distributedLockExecutor;
    private CrewProfileService crewProfileService;

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
            throw new RuntimeException(e);
        }
    }
}
