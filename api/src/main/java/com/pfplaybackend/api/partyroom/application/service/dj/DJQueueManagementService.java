package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DJQueueManagementService {

    private final PartyroomRepository partyroomRepository;
    // private final RedisTemplate<String, Object> redisTemplate;
    private final PartyroomDomainService partyroomDomainService;
    private final DJPlaybackService djPlaybackService;

    // TODO
    // Q. DJQueue 를 '객체 수준'으로 관리할 것인가?
    //

    public void lockQueue() {
        Partyroom partyroom = new Partyroom();
        partyroom.lockQueue();
    }

    public void addDJToQueue() {
        // 비활성화였던 파티룸이 활성화되는 '트리거'를 충족시켰는가?
        if(partyroomDomainService.isChangedToActivation()) {
            djPlaybackService.start();
        }
    }

    public void removeDJFromQueue() {
        // 대기열이 잠겨 있어도 삭제는 된다.
    }
}
