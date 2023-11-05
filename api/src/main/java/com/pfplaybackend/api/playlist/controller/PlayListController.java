package com.pfplaybackend.api.playlist.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.enums.PlayListOrder;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.response.PlayListCreateResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListResponse;
import com.pfplaybackend.api.playlist.service.PlayListService;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.NoSuchElementException;
import java.util.Optional;

@Tag(name = "playlist", description = "playlist api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/play-list")
public class PlayListController {
    private final UserService userService;
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
        JwtTokenInfo jwtTokenInfo = new JwtTokenInfo(SecurityContextHolder.getContext().getAuthentication());
        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                .orElseThrow(NoSuchElementException::new);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(
                        PlayListCreateResponse.toResponse(playListService.createPlayList(request, user)))
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
        JwtTokenInfo jwtTokenInfo = new JwtTokenInfo(SecurityContextHolder.getContext().getAuthentication());
        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                .orElseThrow(NoSuchElementException::new);

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(playListService.getPlayList(user)));
    }
}
