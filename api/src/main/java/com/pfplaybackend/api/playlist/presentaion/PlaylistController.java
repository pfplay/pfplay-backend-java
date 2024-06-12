package com.pfplaybackend.api.playlist.presentaion;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.presentaion.api.PlaylistApi;
import com.pfplaybackend.api.playlist.presentaion.dto.request.*;
import com.pfplaybackend.api.playlist.presentaion.dto.response.*;
import com.pfplaybackend.api.playlist.application.PlaylistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController implements PlaylistApi {
    private final PlaylistService playlistService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlaylistCreateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(
                        "OK"
//                         PlaylistCreateResponse.toResponse(playlistService.createPlaylist(request, userAuthenticationDto.getUserId())))
                ));
    }

    @GetMapping()
    public ResponseEntity<?> getPlaylist() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(
                        "OK"
//                        playlistService.getPlaylist(userAuthenticationDto.getUserId())
                ));
    }

    @GetMapping("{listId}")
    public ResponseEntity<?> getMusicList(@PathVariable Long listId, @ModelAttribute @Valid PaginationRequest request) {
        MusicListResponse list = playlistService.getMusicList(request.getPage(), request.getPageSize(), listId);
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }

    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@ModelAttribute @Valid SearchListRequest request) {
        SearchMusicListResponse result = playlistService.getSearchList(request.getQ(), request.getPageToken());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(result));
    }

    @PostMapping("{listId}")
    public ResponseEntity<?> addMusic(@PathVariable Long listId, @RequestBody MusicListAddRequest request) {
        MusicListAddResponse response = playlistService.addMusic(listId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(response));
    }

    @DeleteMapping()
    public ResponseEntity<?> deletePlaylist(@RequestBody ListDeleteRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        playlistService.deletePlaylist(userCredentials.getUserId(), request.getListIds());
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        ApiCommonResponse.success(
                                "OK"
//                                ListDeleteResponse.builder()
//                                        .listIds(request.getListIds())
//                                        .build()
                        )
                );
    }

    @DeleteMapping("/music")
    public ResponseEntity<?> deleteMusicList(@RequestBody ListDeleteRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        playlistService.deleteMusicList(userCredentials.getUserId(), request.getListIds());
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        ApiCommonResponse.success(
                                "OK"
//                                ListDeleteResponse.builder()
//                                        .listIds(request.getListIds())
//                                        .build()
                        )
                );
    }

    @PatchMapping("{listId}")
    public ResponseEntity<?> modifyPlaylistName(@PathVariable Long listId, @RequestBody PlaylistRenameRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        playlistService.renamePlaylist(userCredentials.getUserId(), listId, request.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(
                        "OK"
//                        PlaylistRenameResponse.builder()
//                        .id(listId)
//                        .name(request.getName())
//                        .build()
                ));
    }
}
