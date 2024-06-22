package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.MusicQueryService;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.presentation.payload.request.PaginationRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.PlaylistMusicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class MusicQueryController {

    private final MusicQueryService musicQueryService;

    @GetMapping("{listId}")
    public ResponseEntity<?> getMusicList(@PathVariable Long listId, @ModelAttribute @Valid PaginationRequest request) {
        return null;
        //        PlaylistMusicResponse list = musicQueryService.getPlaylistMusic(request.getPage(), request.getPageSize(), listId);
//        return ResponseEntity
//                .ok()
//                .body(ApiCommonResponse.success(list));
    }
}
