package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.PlaybackCommandService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackCommandController {

    private final PlaybackCommandService playbackCommandService;

    /**
     * 파티룸 운영진에 의해 호출되는 기능으로
     * 현재 DJ의 곡 재생에 대한 중단 동작을 트리거 한다.
     * @param partyroomId
     */
    @PostMapping("/{partyroomId}/playbacks/skip")
    public ResponseEntity<ApiCommonResponse<Void>> playBackSkip(@PathVariable Long partyroomId) {
        playbackCommandService.skipByManager(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.ok());
    }
}
