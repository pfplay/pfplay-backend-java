package com.pfplaybackend.api.playlist.presentaion.dto.response;

import com.pfplaybackend.api.playlist.model.entity.Playlist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder
@Schema(description = "Playlist")
public class PlaylistCreateResponse {
    @Schema(description = "Playlist id", example = "1", requiredMode = REQUIRED, type = "long")
    private Long id;

    @Schema(description = "이름", example = "나의 플레이리스트 1", requiredMode = REQUIRED, type = "string")
    private String name;

    public static PlaylistCreateResponse toResponse(Playlist playlist) {
        return PlaylistCreateResponse
                .builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .build();
    }
}