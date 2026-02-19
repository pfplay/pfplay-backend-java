package com.pfplaybackend.api.profile.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아바타 Face 요청")
public class AvatarFaceRequest {

    @NotNull(message = "face.sourceType은 필수입니다.")
    @JsonProperty("sourceType")
    @Schema(description = "Face 리소스 타입", example = "INTERNAL_IMAGE", required = true)
    private FaceSourceType sourceType;

    @NotBlank(message = "face.uri는 필수입니다.")
    @JsonProperty("uri")
    @Schema(description = "Face 이미지 URI",
            example = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_face%2Fava_face_001.png?alt=media",
            required = true)
    private String uri;

    @NotNull(message = "face.transform은 필수입니다.")
    @Valid
    @JsonProperty("transform")
    @Schema(description = "Face 변형 정보", required = true)
    private FaceTransformRequest transform;
}
