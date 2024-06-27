package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.DjId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DJManagementService {

    private final PartyroomRepository partyroomRepository;
    // private final RedisTemplate<String, Object> redisTemplate;
    private final PartyroomDomainService partyroomDomainService;
    private final DjPlaybackService djPlaybackService;

    // TODO
    // Q. DJQueue 를 '객체 수준'으로 관리할 것인가?
    //

    @Transactional
    public void enqueueDj(PartyroomId partyroomId) {
        // 비활성화였던 파티룸이 활성화되는 '트리거'를 충족시켰는가?
//        if(partyroomDomainService.isChangedToActivation()) {
//            djPlaybackService.start();
//        }

        // TODO 대기열 잠금 상태 여부 조사
        // TODO 파티룸 활성화 상태 여부 조사
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId, DjId djId) {
        // 대기열이 잠겨 있어도 삭제는 된다.
    }
}
