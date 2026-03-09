package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "Play list")
@Getter
public class CreatePlaylistRequest {
    @NotBlank(message = "name is required.")
    @Size(max = 100, message = "name must be 100 characters or less.")
    @Schema(description = "이름", example = "나의 플레이리스트 1", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String name;
}
