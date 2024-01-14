package com.pfplaybackend.api.playlist.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.presentation.dto.MusicListDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import com.pfplaybackend.api.playlist.presentation.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListDeleteRequest;
import com.pfplaybackend.api.playlist.presentation.response.MusicListAddResponse;
import com.pfplaybackend.api.playlist.presentation.response.MusicListResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListCreateResponse;
import com.pfplaybackend.api.playlist.presentation.response.SearchMusicListResponse;
import com.pfplaybackend.api.playlist.service.PlayListService;
import com.pfplaybackend.api.user.service.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
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
            ),
            @ApiResponse(responseCode = "400", description = "생성 개수 제한 초과 (지갑 미연동) or 생성 개수 제한 초과",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"errorCode\": \"BR001 or BR002\", \"message\": \"생성 개수 제한 초과 (지갑 미연동) or 생성 개수 제한 초과\"}"
                            ))
            )
            // 동일 status code로 한개의 swagger response 예시만 표시 가능하여 위처럼 처리 (가능하긴 하나 커스텀어노테이션 생성이 필요하고 복잡함)
//            @ApiResponse(responseCode = "400", description = "생성 개수 제한 초과",
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(
//                                    value = "{\"errorCode\": \"BR002\", \"message\": \"생성 개수 제한 초과\"}"
//                            ))
//            ),
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

    @Operation(summary = "플레이리스트 & 그랩리스트 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 & 그랩리스트 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PlayListDto.class)
                            ))
            )
    })
    @GetMapping()
    public ResponseEntity<?> getPlayList() {
        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        User user = jwtTokenInfo.getUser();
        List<PlayListDto> list = playListService.getPlayList(user);

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }

    @Operation(summary = "플레이리스트/그랩리스트 곡 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 곡 조회",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = MusicListDto.class)
                            ))
            )
    })
    @GetMapping("{listId}")
    public ResponseEntity<?> getMusicList(@PathVariable Long listId,
                                          @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                                          @RequestParam(required = false, defaultValue = "20", value = "pageSize") int pageSize) {
        MusicListResponse list = playListService.getMusicList(page, pageSize, listId);

        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }

    @Operation(summary = "유튜브 곡 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유튜브 곡 검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SearchMusicListResponse.class))
            ),
            @ApiResponse(responseCode = "500",
                    description = "유튜브 곡 검색 실패"
            )
    })
    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@RequestParam("q") String q, @RequestParam("pageToken") Optional<String> pageToken) {
        SearchMusicListResponse result = playListService.getSearchList(q, pageToken.orElse(null));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(result));
    }

    @Operation(summary = "플레이리스트 곡 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "플레이리스트 곡 추가 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MusicListAddResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "플레이리스트 곡 개수 제한 초과",
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
    @PostMapping("{playListId}")
    public ResponseEntity<?> addMusic(@RequestParam Long playListId, @RequestBody MusicListAddRequest request) {
        MusicListAddResponse response = playListService.addMusic(playListId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(response));
    }

//    @DeleteMapping()
//    public ResponseEntity<?> deletePlayList(@RequestBody PlayListDeleteRequest request) {
//        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
//        User user = jwtTokenInfo.getUser();
//        Object response = playListService.deletePlayList(user.getId(), request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiCommonResponse.success(response));
//    }
}
