package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.playback.ReactPlaybackRequest;
import com.pfplaybackend.api.party.application.dto.playback.ReactionHistoryDto;
import com.pfplaybackend.api.party.application.service.PlaybackReactionCommandService;
import com.pfplaybackend.api.party.domain.exception.ReactionException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ Music Interaction API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackReactionCommandController {

    private final PlaybackReactionCommandService playbackReactionCommandService;

    @Operation(summary = "재생 리액션", description = "현재 재생 중인 곡에 대해 좋아요/싫어요/그랩 리액션을 보냅니다. 토글 방식으로 동작합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({ReactionException.class})
    @PostMapping("/{partyroomId}/playbacks/reaction")
    public ResponseEntity<ApiCommonResponse<ReactionHistoryDto>> reactToPlayback(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId,
            @RequestBody ReactPlaybackRequest request) {
        return ResponseEntity.ok().body(
                ApiCommonResponse.success(playbackReactionCommandService.reactToCurrentPlayback(
                        new PartyroomId(partyroomId), request.getReactionType())));
    }
}
