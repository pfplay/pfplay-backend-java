package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.response.QueryPlaylistResponse;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class PlaylistQueryController {

    private final PlaylistQueryService playlistQueryService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<QueryPlaylistResponse>> getPlaylists() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                QueryPlaylistResponse.builder()
                        .playlists(playlistQueryService.getPlaylists())
                        .build()
        ));
    }
}
