package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.dj.DjMusicReactionService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.ReactCurrentPlaybackRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ Music Interaction API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DjMusicReactionController {

    private final DjMusicReactionService DJMusicInteractionService;

    @PutMapping("/{partyroomId}/dj/current-playback/reaction")
    public ResponseEntity<Void> reactToPlayback(
            @PathVariable Long partyroomId,
            @RequestBody ReactCurrentPlaybackRequest request) {
        DJMusicInteractionService.reactToCurrentTrack();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{partyroomId}/dj/current-playback/grab")
    public ResponseEntity<Void> grabPlayback(
            @PathVariable Long partyroomId) {
        DJMusicInteractionService.grabCurrentTrack();
        return ResponseEntity.ok().build();
    }
}
