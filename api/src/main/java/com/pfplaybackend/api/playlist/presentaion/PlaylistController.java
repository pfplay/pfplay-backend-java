package com.pfplaybackend.api.playlist.presentaion;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.presentaion.api.PlaylistApi;
import com.pfplaybackend.api.playlist.presentaion.dto.response.*;
import com.pfplaybackend.api.playlist.presentaion.dto.request.ListDeleteRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlaylistCreateRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlaylistRenameRequest;
import com.pfplaybackend.api.playlist.application.PlaylistService;
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
public class PlaylistController implements PlaylistApi {

    private final PlaylistService playlistService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlaylistCreateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();
        // Playlist playlist = playlistService.createPlaylist(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success("OK")
                        // PlaylistCreateResponse.toResponse(playlist))
                );
    }

    @GetMapping()
    public ResponseEntity<?> getPlaylist() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
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
        MusicListResponse list = playlistService.getMusicList(page, pageSize, listId);
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(list));
    }

    @GetMapping("/youtube/music")
    public ResponseEntity<?> getSearchList(@RequestParam("q") String q, @RequestParam("pageToken") Optional<String> pageToken) {
        SearchMusicListResponse result = playlistService.getSearchList(q, pageToken.orElse(null));
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
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

        // playlistService.deletePlaylist(member.getId(), request.getListIds());
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
        // playlistService.deleteMusicList(member.getId(), request.getListIds());
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
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();
        // playlistService.renamePlaylist(member.getId(), listId, request.getName());
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
