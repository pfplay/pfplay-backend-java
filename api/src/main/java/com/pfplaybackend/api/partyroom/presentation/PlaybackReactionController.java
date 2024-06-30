package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.dj.PlaybackReactionService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.ReactCurrentPlaybackRequest;
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

    @PutMapping("/{partyroomId}/playback/reaction")
    public ResponseEntity<Void> reactToPlayback(
            @PathVariable Long partyroomId,
            @RequestBody ReactCurrentPlaybackRequest request) {
        // TODO 24.06.30 기준으로 (철회 불가능한) '좋아요'만 호출 가능
        playbackReactionService.reactToCurrentPlayback(new PartyroomId(partyroomId), request);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{partyroomId}/playback/grab")
//    public ResponseEntity<Void> grabPlayback(
//            @PathVariable Long partyroomId) {
//        playbackReactionService.grabCurrentPlayback();
//        return ResponseEntity.ok().build();
//    }
}
