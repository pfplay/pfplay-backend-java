package com.pfplaybackend.api.playlist.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.playlist.application.service.MusicCommandService;
import com.pfplaybackend.api.playlist.presentation.payload.request.DeletePlaylistListRequest;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddMusicRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.AddMusicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "playlist", description = "playlist api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class MusicCommandController {

    private final MusicCommandService musicCommandService;

    @PostMapping("{playlistId}/musics")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> addMusic(@PathVariable Long playlistId, @RequestBody AddMusicRequest request) {
        musicCommandService.addMusicInPlaylist(playlistId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiCommonResponse.success("OK"));
    }

        @DeleteMapping("{playlistId}/musics")
    public ResponseEntity<?> deleteMusic(@PathVariable String playlistId, @RequestBody DeletePlaylistListRequest request) {
        return null;
//        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
//        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
//        musicCommandService.deletePlaylistMusic(userCredentials.getUserId(), request.getListIds());
//        return ResponseEntity
//                .status(HttpStatus.OK).body(
//                        ApiCommonResponse.success(
//                                "OK"
////                                ListDeleteResponse.builder()
////                                        .listIds(request.getListIds())
////                                        .build()
//                        )
//                );
    }

    /**
     * 드래그&드롭으로 순서 변경
     * @return
     */
    public ResponseEntity<?> updateMusicOrder() {
        // TODO 필요한 인자는 다음과 같다.
        // 변경 대상의 MusicId
        // 변경 전: orderNumber
        // 변경 후: orderNumber
        // 위 기능을 수행하려면 'Music 목록의 사이즈'는 'orderNumber 의 최댓값'임을 보장해야 한다.
        // 이 말은 orderNumber 가 할당되는 빈틈이 없어야 한다는 것이다.

        // 위 값을 적용했을 때 사실상 모든 개체의 orderNumber 가 조정된다.
        // 우리는 LinkedList 로 이것을 구현할 것이다.

        return null;
    }

}
