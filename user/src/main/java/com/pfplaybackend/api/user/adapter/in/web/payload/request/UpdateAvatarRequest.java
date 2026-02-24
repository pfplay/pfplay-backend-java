package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 아바타 설정 요청")
public class UpdateAvatarRequest {

    @NotNull(message = "avatarCompositionType은 필수입니다.")
    @JsonProperty("avatarCompositionType")
    @Schema(description = "아바타 구성 타입", example = "BODY_WITH_FACE", requiredMode = Schema.RequiredMode.REQUIRED)
    private AvatarCompositionType avatarCompositionType;

    @NotNull(message = "body 정보는 필수입니다.")
    @Valid
    @JsonProperty("body")
    @Schema(description = "아바타 Body 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private AvatarBody body;

    @Valid
    @JsonProperty("face")
    @Schema(description = "아바타 Face 정보 (BODY_WITH_FACE 타입일 때만 필요)")
    private AvatarFace face;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "아바타 Body 요청")
    public static class AvatarBody {

        @NotBlank(message = "body.uri는 필수입니다.")
        @Pattern(regexp = "^https:\\/\\/firebasestorage\\.googleapis\\.com\\/v0\\/b\\/pfplay-firebase\\.appspot\\.com(?:\\/.*)?$",
                message = "올바른 body URI 형식이 아닙니다.")
        @JsonProperty("uri")
        @Schema(description = "Body 이미지 URI",
                example = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String uri;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "아바타 Face 요청")
    public static class AvatarFace {

        @NotNull(message = "face.sourceType은 필수입니다.")
        @JsonProperty("sourceType")
        @Schema(description = "Face 리소스 타입", example = "INTERNAL_IMAGE", requiredMode = Schema.RequiredMode.REQUIRED)
        private FaceSourceType sourceType;

        @NotBlank(message = "face.uri는 필수입니다.")
        @JsonProperty("uri")
        @Schema(description = "Face 이미지 URI",
                example = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_face%2Fava_face_001.png?alt=media",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String uri;

        @NotNull(message = "face.transform은 필수입니다.")
        @Valid
        @JsonProperty("transform")
        @Schema(description = "Face 변형 정보", requiredMode = Schema.RequiredMode.REQUIRED)
        private FaceTransform transform;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Face 변형 정보")
    public static class FaceTransform {

        @NotNull(message = "transform.offsetX는 필수입니다.")
        @JsonProperty("offsetX")
        @Schema(description = "X축 오프셋", example = "12.5", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double offsetX;

        @NotNull(message = "transform.offsetY는 필수입니다.")
        @JsonProperty("offsetY")
        @Schema(description = "Y축 오프셋", example = "-8.0", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double offsetY;

        @NotNull(message = "transform.scale은 필수입니다.")
        @DecimalMin(value = "0.0", message = "scale은 0 이상이어야 합니다.")
        @DecimalMax(value = "200.0", message = "scale은 200 이하여야 합니다.")
        @JsonProperty("scale")
        @Schema(description = "크기 비율 (%)", example = "85.0", minimum = "0", maximum = "200", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double scale;
    }
}
