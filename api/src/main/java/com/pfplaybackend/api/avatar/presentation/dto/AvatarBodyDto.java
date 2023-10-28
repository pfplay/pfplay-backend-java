package com.pfplaybackend.api.avatar.presentation.dto;

import com.pfplaybackend.api.avatar.enums.AvatarType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Schema(description = "Avatar Body")
@Data
@Builder
public class AvatarBodyDto {
    @Schema(description = "Avatar id", example = "1", requiredMode = REQUIRED, type = "long")
    private final Long id;

    @Schema(description = "타입", example = "BASIC", requiredMode = REQUIRED, type = "string", allowableValues = {"BASIC", "DJ", "REF", "ROOM"})
    private final AvatarType type;

    @Schema(description = "이름", example = "도깨비불", requiredMode = REQUIRED, type = "string")
    private final String name;

    @Schema(description = "image url", example = "https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773", requiredMode = REQUIRED, type = "string")
    private final String image;

    @Schema(description = "해금 포인트", example = "0", requiredMode = REQUIRED, type = "integer")
    private final Long requiredPoint;

    @Schema(description = "보유 포인트", example = "0", requiredMode = REQUIRED, type = "integer")
    private final Long myPoint;

    @Schema(description = "사용 가능 여부", example = "true", requiredMode = REQUIRED, type = "boolean")
    private final Boolean isAvailable;
}
