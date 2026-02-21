package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.PlaybackReactionCommandService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.playback.ReactPlaybackRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "DJ Music Interaction API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PlaybackReactionCommandController {

    private final PlaybackReactionCommandService playbackReactionCommandService;

    @PostMapping("/{partyroomId}/playbacks/reaction")
    public ResponseEntity<ApiCommonResponse<Map<String, Boolean>>> reactToPlayback(
            @PathVariable Long partyroomId,
            @RequestBody ReactPlaybackRequest request) {
        return ResponseEntity.ok().body(
                ApiCommonResponse.success(playbackReactionCommandService.reactToCurrentPlayback(
                        new PartyroomId(partyroomId), request.getReactionType())));
    }
}