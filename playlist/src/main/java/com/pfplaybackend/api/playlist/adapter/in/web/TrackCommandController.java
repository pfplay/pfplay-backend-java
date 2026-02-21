package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.dto.command.AddTrackCommand;
import com.pfplaybackend.api.playlist.application.dto.command.MoveTrackCommand;
import com.pfplaybackend.api.playlist.application.dto.command.UpdateTrackOrderCommand;
import com.pfplaybackend.api.playlist.application.service.TrackCommandService;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.MoveTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.UpdateTrackOrderRequest;
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

    @PostMapping("{playlistId}/tracks")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<Void>> addTrack(@PathVariable Long playlistId, @RequestBody AddTrackRequest request) {
        AddTrackCommand command = new AddTrackCommand(request.getName(), request.getLinkId(), request.getDuration(), request.getThumbnailImage());
        trackCommandService.addTrackInPlaylist(playlistId, command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.ok());
    }

    @DeleteMapping("{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long playlistId, @PathVariable Long trackId) {
        trackCommandService.deleteTrackInPlaylist(playlistId, trackId);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("{playlistId}/tracks/{trackId}/move")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<Void> moveTrack(@PathVariable Long playlistId, @PathVariable Long trackId,
                                          @RequestBody MoveTrackRequest request) {
        MoveTrackCommand command = new MoveTrackCommand(request.getTargetPlaylistId());
        trackCommandService.moveTrackToPlaylist(playlistId, trackId, command);
        return ResponseEntity.accepted().build();
    }

    /**
     * 드래그&드롭으로 순서 변경
     * @return
     */
    @PutMapping("{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> updateMusicOrder(@PathVariable Long playlistId, @PathVariable Long trackId,
                                                 @RequestBody UpdateTrackOrderRequest request) {
        UpdateTrackOrderCommand command = new UpdateTrackOrderCommand(request.getNextOrderNumber());
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, command);
        return ResponseEntity.accepted().build();
    }

}
