package com.pfplaybackend.api.playlist.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.controller.api.PlayListApi;
import com.pfplaybackend.api.playlist.model.PlayList;
import com.pfplaybackend.api.playlist.presentation.request.ListDeleteRequest;
import com.pfplaybackend.api.playlist.presentation.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListRenameRequest;
import com.pfplaybackend.api.playlist.presentation.response.*;
import com.pfplaybackend.api.playlist.service.PlayListService;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "playlist", description = "playlist api")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/play-list")
public class PlayListController implements PlayListApi {

    private final PlayListService playListService;
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlayListCreateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        PlayList playList = playListService.createPlayList(request, user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(
                        PlayListCreateResponse.toResponse(playList))
                );
    }

    @GetMapping()
    public ResponseEntity<?> getPlayList() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        List<PlayListResponse> list = playListService.getPlayList(user);
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }


    @GetMapping("{listId}")
    public ResponseEntity<?> getMusicList(@PathVariable Long listId,
                                          @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                                          @RequestParam(required = false, defaultValue = "20", value = "pageSize") int pageSize) {
        MusicListResponse list = playListService.getMusicList(page, pageSize, listId);
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }

    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@RequestParam("q") String q, @RequestParam("pageToken") Optional<String> pageToken) {
        SearchMusicListResponse result = playListService.getSearchList(q, pageToken.orElse(null));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(result));
    }

    @PostMapping("{listId}")
    public ResponseEntity<?> addMusic(@PathVariable Long listId, @RequestBody MusicListAddRequest request) {
        MusicListAddResponse response = playListService.addMusic(listId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(response));
    }

    @DeleteMapping()
    public ResponseEntity<?> deletePlayList(@RequestBody ListDeleteRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        playListService.deletePlayList(user.getId(), request.getListIds());
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        ApiCommonResponse.success(
                                ListDeleteResponse.builder()
                                        .listIds(request.getListIds())
                                        .build()
                        )
                );
    }

    @DeleteMapping("/music")
    public ResponseEntity<?> deleteMusicList(@RequestBody ListDeleteRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        playListService.deleteMusicList(user.getId(), request.getListIds());
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        ApiCommonResponse.success(
                                ListDeleteResponse.builder()
                                        .listIds(request.getListIds())
                                        .build()
                        )
                );
    }

    @PatchMapping("{listId}")
    public ResponseEntity<?> modifyPlayListName(@PathVariable Long listId, @RequestBody PlayListRenameRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        playListService.renamePlayList(user.getId(), listId, request.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(
                        PlayListRenameResponse.builder()
                        .id(listId)
                        .name(request.getName())
                        .build()));
    }
}
