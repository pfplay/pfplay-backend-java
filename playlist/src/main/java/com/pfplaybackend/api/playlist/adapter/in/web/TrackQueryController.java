package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.TrackQueryService;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.response.QueryTrackListResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class TrackQueryController {

    private final TrackQueryService trackQueryService;

    @GetMapping("{playlistId}/tracks")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<QueryTrackListResponse>> getAllTracks(
            @PathVariable Long playlistId,
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(
                        QueryTrackListResponse.from(trackQueryService.getTracks(playlistId, pageNumber, pageSize))
                ));
    }
}
