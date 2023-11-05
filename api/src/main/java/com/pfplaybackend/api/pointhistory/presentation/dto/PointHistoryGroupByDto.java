package com.pfplaybackend.api.pointhistory.presentation.dto;

import com.pfplaybackend.api.pointhistory.enums.PointType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema
@Builder
public class PointHistoryGroupByDto {
    @Schema(implementation = PointType.class, description = "포인트 타입", example = "DJ", requiredMode = REQUIRED, type = "string", allowableValues = {"DJ", "REF", "ROOM"})
    private final PointType type;

    @Schema(description = "포인트 점수", example = "1", requiredMode = REQUIRED, type = "long")
    private final Long point;

}