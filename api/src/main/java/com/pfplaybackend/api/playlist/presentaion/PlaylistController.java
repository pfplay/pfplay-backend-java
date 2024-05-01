package com.pfplaybackend.api.playlist.presentaion;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.presentaion.api.PlayListApi;
import com.pfplaybackend.api.playlist.presentaion.dto.response.*;
import com.pfplaybackend.api.playlist.presentaion.dto.request.ListDeleteRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlayListRenameRequest;
import com.pfplaybackend.api.playlist.application.PlayListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController implements PlayListApi {

    private final PlayListService playListService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlayListCreateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();
        // PlayList playList = playListService.createPlayList(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success("OK")
                        // PlayListCreateResponse.toResponse(playList))
                );
    }

    @GetMapping()
    public ResponseEntity<?> getPlayList() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

        // List<PlayListResponse> list = playListService.getPlayList();
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(
                        "OK"
                        // list
                ));
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
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

        // playListService.deletePlayList(member.getId(), request.getListIds());
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
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();
        // playListService.deleteMusicList(member.getId(), request.getListIds());
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
    public ResponseEntity<?> modifyPlayListName(@PathVariable Long listId, @RequestBody PlayListRenameRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();
        // playListService.renamePlayList(member.getId(), listId, request.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiCommonResponse.success(
                        "OK"
//                        PlayListRenameResponse.builder()
//                        .id(listId)
//                        .name(request.getName())
//                        .build()
                ));
    }
}
