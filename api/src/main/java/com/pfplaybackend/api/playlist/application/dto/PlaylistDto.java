package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema
public record PlaylistDto(
        @Schema(description = "Playlist id", example = "1", requiredMode = REQUIRED, type = "long")
        Long id,
        @Schema(description = "Playlist order number", example = "1", requiredMode = REQUIRED, type = "long")
        Integer orderNumber,
        @Schema(description = "Playlist name", example = "나의 플레이리스트 1", requiredMode = REQUIRED, type = "string")
        String name,
        @Schema(description = "Playlist type", example = "PLAYLIST", requiredMode = REQUIRED, type = "string", allowableValues = {"PLAYLIST", "GRABLIST"})
        PlaylistType type
) {}
