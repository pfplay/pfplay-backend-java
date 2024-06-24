package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.application.service.MusicQueryService;
import com.pfplaybackend.api.playlist.presentation.payload.request.PaginationRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.QueryMusicListResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class MusicQueryController {

    private final MusicQueryService musicQueryService;

    @GetMapping("{playlistId}/musics")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> getMusics(@PathVariable Long playlistId, @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(
                        QueryMusicListResponse.from(musicQueryService.getMusics(playlistId, request.getPageNo(), request.getPageSize()))
                ));
    }
}
