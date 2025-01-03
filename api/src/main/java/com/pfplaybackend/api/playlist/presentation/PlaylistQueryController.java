package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import com.pfplaybackend.api.playlist.presentation.payload.response.QueryPlaylistResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class PlaylistQueryController {

    private final PlaylistQueryService playlistQueryService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> getPlaylists() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                QueryPlaylistResponse.builder()
                        .playlists(playlistQueryService.getPlaylists())
                        .build()
        ));
    }
}
