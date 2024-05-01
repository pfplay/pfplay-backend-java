package com.pfplaybackend.api.user.application.dto;

import com.pfplaybackend.api.user.model.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema
@Builder
public class ActivityGroupByDto {
    @Schema(implementation = ActivityType.class, description = "포인트 타입", example = "DJ", requiredMode = REQUIRED, type = "string", allowableValues = {"DJ", "REF", "ROOM"})
    private final ActivityType type;

    @Schema(description = "포인트 점수", example = "1", requiredMode = REQUIRED, type = "integer")
    private final Long point;
}
