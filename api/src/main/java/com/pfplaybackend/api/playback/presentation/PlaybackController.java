package com.pfplaybackend.api.playback.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PlaybackInfoService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 재생 시작/종료와 같은 DJ 행위에 대한 표현 계층을 담당한다.
 * 현재 DJ의 곡 재생과 관련된 동작을 트리거하는 요청을 처리한다.
 */
@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackController {

    private final PlaybackManagementService playbackManagementService;
    private final PlaybackInfoService playbackInfoService;

    /**
     * 파티룸 운영진에 의해 호출되는 기능으로
     * 현재 DJ의 곡 재생에 대한 중단 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/playbacks/skip")
    public ResponseEntity<?> playBackSkip(@PathVariable Long partyroomId) {
        playbackManagementService.skipByManager(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }

    /**
     * 타겟 파티룸의 재생 이력을 최근순으로 20개만을 조회한다.
     * History 의 Playback 의 당시 Dj는 현재 파티룸을 이탈했을 수도 있다.
     * @param partyroomId
     * @return List<PlaybackHistory>
     */
    @GetMapping("/{partyroomId}/playbacks/histories")
    public ResponseEntity<?> playBackHistory(@PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                playbackInfoService.getRecentPlaybackHistory(new PartyroomId(partyroomId)))
        );
    }
}
