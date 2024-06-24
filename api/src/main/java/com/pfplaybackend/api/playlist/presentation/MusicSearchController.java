package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.MusicSearchService;
import com.pfplaybackend.api.playlist.presentation.payload.request.SearchMusicListRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.SearchMusicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Playlist API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music-search")
public class MusicSearchController {

    private final MusicSearchService musicSearchService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> getSearchList(@ModelAttribute @Valid SearchMusicListRequest request) {
        SearchMusicResponse searchMusicResponse = SearchMusicResponse.from(musicSearchService.getSearchList(request.getQ(), "10"));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(searchMusicResponse));
    }
}