package com.pfplaybackend.api.youtube.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListResponse;
import com.pfplaybackend.api.youtube.presentation.response.MusicListResponse;
import com.pfplaybackend.api.youtube.service.YouTubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "youtube", description = "youtube api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/youtube")
public class YouTubeController {
    private final YouTubeService youTubeService;

    @Operation(summary = "곡 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "곡 검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MusicListResponse.class))
            )
    })
    @PostMapping("/search")
    public ResponseEntity<?> getSearchList() {
        MusicListResponse result = youTubeService.getSearchList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(result));
    }
}
