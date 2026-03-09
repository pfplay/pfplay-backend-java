package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.PlaybackCommandService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "재생 스킵", description = "현재 재생 중인 곡을 스킵합니다. 파티룸 운영진(COMMUNITY_MANAGER 이상)만 호출 가능합니다.")
    @ApiResponse(responseCode = "204", description = "재생 스킵 성공")
    @SecurityRequirement(name = "cookieAuth")
    @DeleteMapping("/{partyroomId}/playbacks/current")
    public ResponseEntity<Void> playBackSkip(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        playbackCommandService.skipByManager(new PartyroomId(partyroomId));
        return ResponseEntity.noContent().build();
    }
}
