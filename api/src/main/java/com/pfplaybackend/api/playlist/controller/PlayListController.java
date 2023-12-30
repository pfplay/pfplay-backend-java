package com.pfplaybackend.api.playlist.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.presentation.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.response.MusicListAddResponse;
import com.pfplaybackend.api.playlist.presentation.response.MusicListResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListCreateResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListResponse;
import com.pfplaybackend.api.playlist.service.PlayListService;
import com.pfplaybackend.api.user.service.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "playlist", description = "playlist api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/play-list")
public class PlayListController {
    private final CustomUserDetailService customUserDetailService;
    private final PlayListService playListService;

    @Operation(summary = "플레이리스트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "플레이리스트 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayListCreateResponse.class))
            )
    })
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlayListCreateRequest request) {
        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        User user = jwtTokenInfo.getUser();
        PlayList playList = playListService.createPlayList(request, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(
                        PlayListCreateResponse.toResponse(playList))
                );
    }

    @Operation(summary = "플레이리스트 / 그랩 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 / 그랩 리스트 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayListResponse.class))
            )
    })
    @GetMapping()
    public ResponseEntity<?> getPlayList() {
        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(playListService.getPlayList(jwtTokenInfo.getUser())));
    }


    @Operation(summary = "유튜브 곡 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유튜브 곡 검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MusicListResponse.class))
            ),
            @ApiResponse(responseCode = "500",
                    description = "유튜브 곡 검색 실패"
            )
    })
    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@RequestParam("q") String q, @RequestParam("pageToken") Optional<String> pageToken) {
        MusicListResponse result = playListService.getSearchList(q, pageToken.orElse(null));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(result));
    }

    @Operation(summary = "곡 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "곡 추가 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MusicListAddResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "곡 개수 제한 초과",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"곡 개수 제한 초과\"}"
                            ))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 플레이리스트",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"존재하지 않는 플레이리스트\"}"
                            ))
            ),
            @ApiResponse(responseCode = "409", description = "이미 추가된 곡",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"이미 존재하는 값입니다.\"}"
                            ))
            )
    })
    @PostMapping("/music-list")
    public ResponseEntity<?> addMusic(@RequestBody MusicListAddRequest request) {
        MusicListAddResponse response = playListService.addMusic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(response));
    }
}
