package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.presentation.payload.request.GrabMusicRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.MusicReactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partyrooms")
@RequiredArgsConstructor
public class DJMusicInteractionController {

    @PutMapping("/{partyroomId}/dj/current-music/reaction")
    public ResponseEntity<Void> reactToMusic(
            @PathVariable Long partyroomId,
            @RequestBody MusicReactionRequest musicReactionRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{partyroomId}/dj/current-music/grab")
    public ResponseEntity<Void> grabMusic(
            @PathVariable Long partyroomId,
            @RequestBody GrabMusicRequest grabMusicRequest) {
        return ResponseEntity.ok().build();
    }
}
