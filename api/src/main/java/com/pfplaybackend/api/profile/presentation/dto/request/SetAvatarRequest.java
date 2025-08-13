package com.pfplaybackend.api.profile.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 아바타 설정 요청")
public class SetAvatarRequest {

    @NotNull(message = "avatarCompositionType은 필수입니다.")
    @JsonProperty("avatarCompositionType")
    @Schema(description = "아바타 구성 타입", example = "BODY_WITH_FACE", required = true)
    private AvatarCompositionType avatarCompositionType;

    @NotNull(message = "body 정보는 필수입니다.")
    @Valid
    @JsonProperty("body")
    @Schema(description = "아바타 Body 정보", required = true)
    private AvatarBodyRequest body;

    @Valid
    @JsonProperty("face")
    @Schema(description = "아바타 Face 정보 (BODY_WITH_FACE 타입일 때만 필요)", required = false)
    private AvatarFaceRequest face;
}
