package com.pfplaybackend.api.playlist.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Music list")
@Getter
public class MusicListAddRequest {
    @Schema(description = "플레이리스트 id", example = "1", required = true, type = "long")
    private Long id;

    @Schema(description = "곡 고유 id", example = "POe9SOEKotk", required = true, type = "string")
    private String uid;

    @Schema(description = "곡 이름", example = "BLACKPINK - ‘Shut Down’ M/V", required = true, type = "string")
    private String name;

    @Schema(description = "곡 재생 시간", example = "03:01", required = true, type = "string")
    private String duration;
}
