package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.user.application.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DjPlaybackService {

    private final UserActivityService userActivityService;

    @Transactional
    public void complete() {
        userActivityService.updateDJPointScore(UUID.randomUUID(), 1);
        // Partyroom partyroom = new Partyroom();
        // partyroom.getNextDJ();
        // 다음 DJ 존재하면 start();
    }

    public void stop() {
        // Partyroom partyroom = new Partyroom();
        // 다음 DJ 존재하면 start();
        // 없으면 파티룸 비활성화
    }

    // 클라이언트에 의해 명시적인 시작은 발생할 수 없다. 자동으로 시작되기 때문이다.
    public void start() {

    }
}
