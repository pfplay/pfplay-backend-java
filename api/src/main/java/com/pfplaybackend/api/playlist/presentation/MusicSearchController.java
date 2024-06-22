package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.MusicSearchService;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.presentation.payload.request.SearchListRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.SearchPlaylistMusicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class MusicSearchController {

    private final MusicSearchService musicSearchService;

    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@ModelAttribute @Valid SearchListRequest request) {
        return null;
        //        SearchPlaylistMusicResponse result = musicSearchService.getSearchList(request.getQ(), request.getPageToken());
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiCommonResponse.success(result));
    }
}
