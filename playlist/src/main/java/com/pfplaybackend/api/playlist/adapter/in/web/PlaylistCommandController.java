package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.DeletePlaylistListRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.CreatePlaylistRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.UpdatePlaylistNameRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.response.CreatePlaylistResponse;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.response.DeletePlaylistListResponse;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.response.UpdatePlaylistNameResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class PlaylistCommandController {

    private final PlaylistCommandService playlistCommandService;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> create(@RequestBody @Valid CreatePlaylistRequest request) {
        PlaylistData playlist = playlistCommandService.createPlaylist(request.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(CreatePlaylistResponse.from(playlist)));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> deletePlaylist(@RequestBody DeletePlaylistListRequest request) {
        playlistCommandService.deletePlaylist(request.getPlaylistIds());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(DeletePlaylistListResponse.from(request.getPlaylistIds())));
    }

    @PatchMapping("{playlistId}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> modifyPlaylistName(@PathVariable Long playlistId, @RequestBody UpdatePlaylistNameRequest request) {
        PlaylistData playlist = playlistCommandService.renamePlaylist(playlistId, request.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(UpdatePlaylistNameResponse.from(playlist)));
    }
}
