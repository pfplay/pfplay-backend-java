package com.pfplaybackend.api.youtube.controller;

import com.pfplaybackend.api.youtube.presentation.response.MusicListResponse;
import com.pfplaybackend.api.youtube.service.YouTubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
            @ApiResponse(responseCode = "200", description = "곡 검색 성공")
    })
    @PostMapping("/search")
    public List<MusicListResponse> getSearchList() {
        return youTubeService.getSearchList();
    }
}
