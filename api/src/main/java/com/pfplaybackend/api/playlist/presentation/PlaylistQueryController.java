package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistQueryController {

    private final PlaylistQueryService playlistQueryService;

    @GetMapping()
    public ResponseEntity<?> getPlaylist() {
        // CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        return ResponseEntity
                .ok()
                .body(ApiCommonResponse.success(
                        "OK"
//                        playlistService.getPlaylist(userAuthenticationDto.getUserId())
                ));
    }
}
