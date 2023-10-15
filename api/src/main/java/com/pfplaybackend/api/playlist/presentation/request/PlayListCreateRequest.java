package com.pfplaybackend.api.playlist.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Play list")
@Getter
//@NoArgsConstructor
//@AllArgsConstructor
public class PlayListCreateRequest {
    @Schema(description = "이름", example = "도깨비불", required = true, type = "string")
    private String name;

    @Schema(description = "타입 (playlist or grab)", example = "playlist", required = true, type = "string")
    private String type;
}
