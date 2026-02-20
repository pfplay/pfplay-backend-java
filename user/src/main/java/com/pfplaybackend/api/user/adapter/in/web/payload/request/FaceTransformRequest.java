package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Face 변형 정보")
public class FaceTransformRequest {

    @NotNull(message = "transform.offsetX는 필수입니다.")
    @JsonProperty("offsetX")
    @Schema(description = "X축 오프셋", example = "12.5", required = true)
    private Double offsetX;

    @NotNull(message = "transform.offsetY는 필수입니다.")
    @JsonProperty("offsetY")
    @Schema(description = "Y축 오프셋", example = "-8.0", required = true)
    private Double offsetY;

    @NotNull(message = "transform.scale은 필수입니다.")
    @DecimalMin(value = "0.0", message = "scale은 0 이상이어야 합니다.")
    @DecimalMax(value = "200.0", message = "scale은 200 이하여야 합니다.")
    @JsonProperty("scale")
    @Schema(description = "크기 비율 (%)", example = "85.0", minimum = "0", maximum = "200", required = true)
    private Double scale;
}

