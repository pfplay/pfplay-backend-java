package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.playlist.application.GrabService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DJMusicInteractionService {

    // private final RedisTemplate<String, Object> redisTemplate;
    // private final TrackInteractionHistoryRepository trackInteractionHistoryRepository;
    // TODO Call Other BoundaryContext
    private final GrabService grabService;
    private final UserActivityService userActivityService;

    @Transactional
    public void reactToCurrentTrack() {
        // TODO Get CurrentTrack From Redis By PartyroomId
        // TODO 좋아요/싫어요 토글로 동작해야 한다.
        // '행위 이력'를 저장해야 한다는 말이다.
        // 행위 이력은 Redis 에만 기록한다.
        // → '행위 이력'를 저장해야 한다는 말이다.
        userActivityService.updateDJPointScore(UUID.randomUUID(), 1);
    }

    @Transactional
    public void grabCurrentTrack() {
        // TODO Get CurrentTrack From Redis By PartyroomId
        // TODO 그랩은 토글로 동작해야 한다. 즉, 취소가 가능해야 한다.
        // → '행위 이력'를 저장해야 한다는 말이다.
        grabService.grabThisMusic();
        userActivityService.updateDJPointScore(UUID.randomUUID(), 2);
    }
}