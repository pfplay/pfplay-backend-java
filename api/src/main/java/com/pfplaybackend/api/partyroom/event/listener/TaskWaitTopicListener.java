package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.application.service.task.TaskExecutorService;
import com.pfplaybackend.api.partyroom.event.message.TaskWaitMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
public class TaskWaitTopicListener implements MessageListener {

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;
    private TaskExecutorService taskExecutorService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if(expiredKey.startsWith("TASK:WAIT")) {
            String taskId = expiredKey.split(":")[2];
            String argsKey = "WAIT:ARGS:" + taskId;
            TaskWaitMessage deserialized = objectMapper.convertValue(redisTemplate.opsForValue().get(argsKey), TaskWaitMessage.class);
            redisTemplate.delete(argsKey);
            taskExecutorService.performTaskWithLock(deserialized.getPartyroomId(), deserialized.getUserId());
        }
    }
}
