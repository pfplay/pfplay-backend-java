package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PlaybackManagementService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 현재 DJ의 곡 재생에 대한 완료 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/playback/complete")
    public ResponseEntity<?> playBackComplete(@PathVariable Long partyroomId) {
        playbackManagementService.complete(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }

    /**
     * 파티룸 운영진에 의해 호출되는 기능으로
     * 현재 DJ의 곡 재생에 대한 중단 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/playback/skip")
    public ResponseEntity<?> playBackSkip(@PathVariable Long partyroomId) {
        playbackManagementService.skip(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
