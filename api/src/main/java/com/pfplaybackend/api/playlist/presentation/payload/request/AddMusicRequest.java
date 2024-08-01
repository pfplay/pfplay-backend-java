package com.pfplaybackend.api.playlist.presentation.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Music list")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddMusicRequest {
    @Schema(description = "곡 이름", example = "BLACKPINK - ‘Shut Down’ M/V", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String name;

    @Schema(description = "곡 링크 id", example = "POe9SOEKotk", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String linkId;

    @Schema(description = "곡 재생 시간", example = "03:01", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String duration;

    @Schema(description = "곡 썸네일 이미지", example = "https://i.ytimg.com/vi/POe9SOEKotk/mqdefault.jpg", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String thumbnailImage;
}
