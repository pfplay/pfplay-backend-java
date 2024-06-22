package com.pfplaybackend.api.playlist.presentation.payload.request;

import com.pfplaybackend.api.user.domain.value.UserId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Music list")
@Getter
public class PlaylistMusicAddRequest {
    @Schema(description = "곡 고유 id", example = "POe9SOEKotk", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private UserId ownerId;

    @Schema(description = "곡 이름", example = "BLACKPINK - ‘Shut Down’ M/V", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String name;

    @Schema(description = "곡 재생 시간", example = "03:01", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String duration;

    @Schema(description = "곡 썸네일 이미지", example = "https://i.ytimg.com/vi/POe9SOEKotk/mqdefault.jpg", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String thumbnailImage;
}
