package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.application.service.lock.DistributedLockExecutor;
import com.pfplaybackend.api.partyroom.event.message.PlaybackDurationWaitMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.function.Supplier;

@AllArgsConstructor
public class PlaybackDurationWaitTopicListener implements MessageListener {

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;
    private DistributedLockExecutor distributedLockExecutor;
    private PlaybackManagementService playbackManagementService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if(expiredKey.startsWith("TASK:WAIT")) {
            String taskId = expiredKey.split(":")[2];
            String argsKey = "WAIT:ARGS:" + taskId;
            PlaybackDurationWaitMessage deserialized = objectMapper.convertValue(redisTemplate.opsForValue().get(argsKey), PlaybackDurationWaitMessage.class);
            String suffixId = deserialized.getUserId().getUid().toString();
            distributedLockExecutor.performTaskWithLock(suffixId, () -> {
                redisTemplate.delete(argsKey);
                playbackManagementService.complete(deserialized.getPartyroomId(), deserialized.getUserId());
                return null;
            });
        }
    }
}
