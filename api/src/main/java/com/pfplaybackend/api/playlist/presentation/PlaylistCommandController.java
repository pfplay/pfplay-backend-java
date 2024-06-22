package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.presentation.payload.request.ListDeleteRequest;
import com.pfplaybackend.api.playlist.presentation.payload.request.PlaylistCreateRequest;
import com.pfplaybackend.api.playlist.presentation.payload.request.PlaylistRenameRequest;
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
public class PlaylistCommandController {

    private final PlaylistCommandService playlistCommandService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid PlaylistCreateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        Playlist playlist = playlistCommandService.createPlaylist(request.getName(), userCredentials.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success(playlist));
    }

    @DeleteMapping()
    public ResponseEntity<?> deletePlaylist(@RequestBody ListDeleteRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        playlistCommandService.deletePlaylist(userCredentials.getUserId(), request.getListIds());
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
        return null;
//        playlistService.renamePlaylist(userCredentials.getUserId(), listId, request.getName());
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiCommonResponse.success(
//                        "OK"
////                        PlaylistRenameResponse.builder()
////                        .id(listId)
////                        .name(request.getName())
////                        .build()
//                ));
    }
}
