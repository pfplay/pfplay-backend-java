package com.pfplaybackend.api.party.adapter.in.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.party.application.service.PlaybackManagementService;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import com.pfplaybackend.api.party.application.dto.PlaybackDurationWaitPayload;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

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
            PlaybackDurationWaitPayload deserialized = objectMapper.convertValue(redisTemplate.opsForValue().get(argsKey), PlaybackDurationWaitPayload.class);
            String suffixId = deserialized.userId().getUid().toString();
            distributedLockExecutor.performTaskWithLock(suffixId, () -> {
                redisTemplate.delete(argsKey);
                playbackManagementService.complete(deserialized.partyroomId(), deserialized.userId());
                return null;
            });
        }
    }
}
