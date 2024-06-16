package com.pfplaybackend.api.partyroom.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 이 클래스는 재생 시작/종료와 같은 DJ 행위에 대한 표현 계층을 담당한다.
 * 현재 DJ의 곡 재생과 관련된 동작을 트리거하는 요청을 처리한다.
 */
@Tag(name = "DJ Playback API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DJPlaybackController {

    /**
     * 현재 DJ의 곡 재생에 대한 시작 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/dj/playback/start")
    void playBackStart(@PathVariable Long partyroomId) {
        
    }

    /**
     * 현재 DJ의 곡 재생에 대한 완료 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/dj/playback/complete")
    void playBackComplete(@PathVariable Long partyroomId) {

    }

    /**
     * 관리자에 의해 호출되는 기능으로
     * 현재 DJ의 곡 재생에 대한 중단 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/dj/playback/stop")
    void playBackStop(@PathVariable Long partyroomId) {

    }
}
