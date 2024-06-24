package com.pfplaybackend.api.playlist.presentation.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Play list")
@Getter
public class CreatePlaylistRequest {
    @Schema(description = "이름", example = "나의 플레이리스트 1", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String name;
}
