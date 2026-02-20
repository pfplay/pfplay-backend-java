package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomAggregateService {

    private final DjRepository djRepository;
    private final PartyroomRepository partyroomRepository;

    /**
     * DJ큐에서 제거 + 순서 재배치 (atomic operation)
     */
    public void removeDjFromQueue(Long partyroomId, CrewId crewId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        queuedDjs.stream()
                .filter(dj -> dj.getCrewId().equals(crewId))
                .forEach(DjData::applyDequeued);

        int order = 1;
        for (DjData dj : queuedDjs) {
            if (!dj.getCrewId().equals(crewId)) {
                dj.updateOrderNumber(order++);
            }
        }
        djRepository.saveAll(queuedDjs);
    }

    /**
     * DJ큐 로테이션 (1번→마지막, 나머지 -1)
     */
    public void rotateDjQueue(Long partyroomId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        int totalElements = queuedDjs.size();
        queuedDjs.forEach(dj -> {
            if (dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            } else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
        djRepository.saveAll(queuedDjs);
    }

    /**
     * 재생 비활성화 + 전체 DJ 일괄 dequeue
     */
    public void deactivatePlayback(PartyroomData partyroom) {
        partyroom.applyDeactivation();
        partyroomRepository.save(partyroom);
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        queuedDjs.forEach(DjData::applyDequeued);
        djRepository.saveAll(queuedDjs);
    }

    /**
     * 현재 DJ 여부 판별 (큐에서 1번 순서인지)
     */
    public boolean isCurrentDj(Long partyroomId, CrewId crewId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        return queuedDjs.stream()
                .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
    }

    /**
     * DJ큐에 활성 DJ가 존재하는지 확인
     */
    public boolean hasQueuedDjs(Long partyroomId) {
        return djRepository.existsByPartyroomDataIdAndIsQueuedTrue(partyroomId);
    }
}
