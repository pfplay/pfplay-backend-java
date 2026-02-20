package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomPlaybackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomAggregateService {

    private final DjRepository djRepository;
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;

    /**
     * DJ큐에서 제거 + 순서 재배치 (atomic operation)
     */
    public void removeDjFromQueue(Long partyroomId, CrewId crewId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId);
        List<DjData> toDelete = queuedDjs.stream()
                .filter(dj -> dj.getCrewId().equals(crewId))
                .toList();
        List<DjData> remaining = queuedDjs.stream()
                .filter(dj -> !dj.getCrewId().equals(crewId))
                .toList();

        djRepository.deleteAll(toDelete);

        int order = 1;
        for (DjData dj : remaining) {
            dj.updateOrderNumber(order++);
        }
        djRepository.saveAll(remaining);
    }

    /**
     * DJ큐 로테이션 (1번→마지막, 나머지 -1)
     */
    public void rotateDjQueue(Long partyroomId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId);
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
     * 재생 비활성화 + 전체 DJ 일괄 삭제
     */
    public void deactivatePlayback(Long partyroomId) {
        PartyroomPlaybackData playbackState = partyroomPlaybackRepository.findById(partyroomId).orElseThrow();
        playbackState.deactivate();
        partyroomPlaybackRepository.save(playbackState);
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId);
        djRepository.deleteAll(queuedDjs);
    }

    /**
     * DJ큐에 활성 DJ가 존재하는지 확인
     */
    public boolean hasQueuedDjs(Long partyroomId) {
        return djRepository.existsByPartyroomDataId(partyroomId);
    }
}
