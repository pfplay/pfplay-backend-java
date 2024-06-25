package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.dj.DJMusicInteractionService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.TrackReactionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ Music Interaction API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DJMusicInteractionController {

    private final DJMusicInteractionService DJMusicInteractionService;

    @PutMapping("/{partyroomId}/dj/current-track/reaction")
    public ResponseEntity<Void> reactToTrack(
            @PathVariable Long partyroomId,
            @RequestBody TrackReactionRequest trackReactionRequest) {
        DJMusicInteractionService.reactToCurrentTrack();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{partyroomId}/dj/current-track/grab")
    public ResponseEntity<Void> grabTrack(
            @PathVariable Long partyroomId) {
        DJMusicInteractionService.grabCurrentTrack();
        return ResponseEntity.ok().build();
    }
}
