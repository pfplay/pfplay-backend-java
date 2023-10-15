package com.pfplaybackend.api.avatar.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Schema(description = "Avatar Body")
@Data
public class AvatarBodyDto {
    @Schema(description = "Avatar id", example = "1", requiredMode = REQUIRED, type = "long")
    private final Long id;

    @Schema(description = "타입 (basic, dj, ref, room)", example = "basic", requiredMode = REQUIRED, type = "string")
    private final String type;

    @Schema(description = "이름", example = "도깨비불", requiredMode = REQUIRED, type = "string")
    private final String name;

    @Schema(description = "image url", example = "https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773", requiredMode = REQUIRED, type = "string")
    private final String image;

    @Schema(description = "해금 포인트", example = "0", requiredMode = REQUIRED, type = "integer")
    private final Integer point;
}
