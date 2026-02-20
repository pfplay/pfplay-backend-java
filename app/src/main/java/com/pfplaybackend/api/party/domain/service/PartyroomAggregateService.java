package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.port.DjLoadPort;
import com.pfplaybackend.api.party.domain.port.PlaybackStatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomAggregateService {

    private final DjLoadPort djLoadPort;
    private final PlaybackStatePort playbackStatePort;

    /**
     * DJ큐에서 제거 + 순서 재배치 (atomic operation)
     */
    public void removeDjFromQueue(Long partyroomId, CrewId crewId) {
        List<DjData> queuedDjs = djLoadPort.findByPartyroomIdOrdered(partyroomId);
        List<DjData> toDelete = queuedDjs.stream()
                .filter(dj -> dj.getCrewId().equals(crewId))
                .toList();
        List<DjData> remaining = queuedDjs.stream()
                .filter(dj -> !dj.getCrewId().equals(crewId))
                .toList();

        djLoadPort.removeAll(toDelete);

        int order = 1;
        for (DjData dj : remaining) {
            dj.updateOrderNumber(order++);
        }
        djLoadPort.saveAll(remaining);
    }

    /**
     * DJ큐 로테이션 (1번→마지막, 나머지 -1)
     */
    public void rotateDjQueue(Long partyroomId) {
        List<DjData> queuedDjs = djLoadPort.findByPartyroomIdOrdered(partyroomId);
        int totalElements = queuedDjs.size();
        queuedDjs.forEach(dj -> {
            if (dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            } else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
        djLoadPort.saveAll(queuedDjs);
    }

    /**
     * 재생 비활성화 + 전체 DJ 일괄 삭제
     */
    public void deactivatePlayback(Long partyroomId) {
        PartyroomPlaybackData playbackState = playbackStatePort.findByPartyroomId(partyroomId);
        playbackState.deactivate();
        playbackStatePort.save(playbackState);
        List<DjData> queuedDjs = djLoadPort.findByPartyroomIdOrdered(partyroomId);
        djLoadPort.removeAll(queuedDjs);
    }

    /**
     * DJ큐에 활성 DJ가 존재하는지 확인
     */
    public boolean hasQueuedDjs(Long partyroomId) {
        return djLoadPort.existsByPartyroomId(partyroomId);
    }
}
