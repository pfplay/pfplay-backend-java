package com.pfplaybackend.api.user.presentation.activity.dto;

import com.pfplaybackend.api.user.enums.PointType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema
@Builder
public class UserActivityGroupByDto {
    @Schema(implementation = PointType.class, description = "포인트 타입", example = "DJ", requiredMode = REQUIRED, type = "string", allowableValues = {"DJ", "REF", "ROOM"})
    private final PointType type;

    @Schema(description = "포인트 점수", example = "1", requiredMode = REQUIRED, type = "integer")
    private final Long point;
}
