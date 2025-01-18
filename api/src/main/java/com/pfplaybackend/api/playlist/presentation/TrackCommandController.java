package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.TrackCommandService;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddMusicRequest;
import com.pfplaybackend.api.playlist.presentation.payload.request.UpdateOrderRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class TrackCommandController {

    private final TrackCommandService trackCommandService;

    @PostMapping("{playlistId}/musics")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> addMusic(@PathVariable Long playlistId, @RequestBody AddMusicRequest request) {
        trackCommandService.addMusicInPlaylist(playlistId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success("OK"));
    }

    @DeleteMapping("{playlistId}/musics/{trackId}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long playlistId, @PathVariable Long trackId) {
        trackCommandService.deleteTrackInPlaylist(playlistId, trackId);
        return ResponseEntity.accepted().build();
    }

    /**
     * 드래그&드롭으로 순서 변경
     * @return
     */
    @PutMapping("{playlistId}/musics/{trackId}")
    public ResponseEntity<?> updateMusicOrder(@PathVariable Long playlistId, @PathVariable Long trackId,
                                              @RequestBody UpdateOrderRequest request) {
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, request);
        return ResponseEntity.accepted().build();
    }

}
