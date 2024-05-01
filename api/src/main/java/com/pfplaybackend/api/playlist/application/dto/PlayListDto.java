package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema
@Builder
public class PlayListDto {
    @Schema(description = "Playlist id", example = "1", requiredMode = REQUIRED, type = "long")
    private final Long id;

    @Schema(description = "Playlist order number", example = "1", requiredMode = REQUIRED, type = "long")
    private final Integer orderNumber;
    @Schema(description = "Playlist name", example = "나의 플레이리스트 1", requiredMode = REQUIRED, type = "string")
    private final String name;

    @Schema(description = "Playlist type", example = "PLAYLIST", requiredMode = REQUIRED, type = "string", allowableValues = {"PLAYLIST", "GRABLIST"})
    private final PlaylistType type;
}
