package com.pfplaybackend.api.playlist.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "Play list")
@Getter
public class PlayListDeleteRequest {
    @Schema(description = "이름", example = "나의 플레이리스트 1", required = true)
    private List<Long> playListIds;
}
