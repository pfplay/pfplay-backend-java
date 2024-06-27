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
    private final PartyroomDomainService partyroomDomainService;
    private final DjPlaybackService djPlaybackService;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId) {
        // 비활성화였던 파티룸이 활성화되는 '트리거'를 충족시켰는가?
//        if(partyroomDomainService.isChangedToActivation()) {
//            djPlaybackService.start();
//        }

        // TODO 대기열 잠금 상태 여부 조사
        // TODO 파티룸 활성화 상태 여부 조사
    }

    /**
     * 대기열에 등록된 자신을 제거한다. (무효화한다.)
     * @param partyroomId
     */
    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
    }

    /**
     * 대기열에 등록된 특정 Dj를 제거한다. (무효화한다.)
     * @param partyroomId
     * @param djId
     */
    @Transactional
    public void dequeueDj(PartyroomId partyroomId, DjId djId) {
        // TODO 관리자 등급 여부를 체크
    }
}
