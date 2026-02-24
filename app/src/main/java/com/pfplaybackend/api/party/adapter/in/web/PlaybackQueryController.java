package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.service.PlaybackQueryService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackQueryController {

    private final PlaybackQueryService playbackQueryService;

    /**
     * 타겟 파티룸의 재생 이력을 최근순으로 20개만을 조회한다.
     * History 의 Playback 의 당시 Dj는 현재 파티룸을 이탈했을 수도 있다.
     * @param partyroomId
     * @return List<PlaybackHistory>
     */
    @GetMapping("/{partyroomId}/playbacks/histories")
    public ResponseEntity<ApiCommonResponse<List<PlaybackHistoryDto>>> playBackHistory(@PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                playbackQueryService.getRecentPlaybackHistory(new PartyroomId(partyroomId)))
        );
    }
}
