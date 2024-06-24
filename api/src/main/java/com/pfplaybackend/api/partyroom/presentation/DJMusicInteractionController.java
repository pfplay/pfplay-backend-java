package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.presentation.payload.request.GrabMusicRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.MusicReactionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
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
