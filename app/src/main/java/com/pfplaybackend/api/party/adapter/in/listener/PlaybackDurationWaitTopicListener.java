package com.pfplaybackend.api.party.adapter.in.listener;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDurationWaitDto;
import com.pfplaybackend.api.party.application.port.out.ExpirationTaskPort;
import com.pfplaybackend.api.party.application.service.PlaybackCommandService;
import com.pfplaybackend.api.party.application.service.lock.DistributedLockExecutor;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class PlaybackDurationWaitTopicListener implements MessageListener {

    private ExpirationTaskPort expirationTaskPort;
    private DistributedLockExecutor distributedLockExecutor;
    private PlaybackCommandService playbackCommandService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if(expiredKey.startsWith("TASK:WAIT")) {
            String taskId = expiredKey.split(":")[2];
            PlaybackDurationWaitDto deserialized = expirationTaskPort.getTaskArgs(taskId, PlaybackDurationWaitDto.class);
            String suffixId = deserialized.userId().getUid().toString();
            distributedLockExecutor.performTaskWithLock(suffixId, () -> {
                expirationTaskPort.clearTaskArgs(taskId);
                playbackCommandService.complete(deserialized.partyroomId(), deserialized.userId());
                return null;
            });
        }
    }
}
