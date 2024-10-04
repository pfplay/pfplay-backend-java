package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PlaybackReactionService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.playback.ReactCurrentPlaybackRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ Music Interaction API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackReactionController {

    private final PlaybackReactionService playbackReactionService;

    @PostMapping("/{partyroomId}/playbacks/reaction")
    public ResponseEntity<?> reactToPlayback(
            @PathVariable Long partyroomId,
            @RequestBody ReactCurrentPlaybackRequest request) {
        return ResponseEntity.ok().body(
                ApiCommonResponse.success(playbackReactionService.reactToCurrentPlayback(
                        new PartyroomId(partyroomId), request.getReactionType())));
    }
}