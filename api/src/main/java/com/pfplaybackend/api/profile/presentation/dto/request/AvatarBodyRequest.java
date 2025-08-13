package com.pfplaybackend.api.profile.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아바타 Body 요청")
public class AvatarBodyRequest {

    @NotBlank(message = "body.uri는 필수입니다.")
    @Pattern(regexp = "^https:\\/\\/firebasestorage\\.googleapis\\.com\\/v0\\/b\\/pfplay-firebase\\.appspot\\.com(?:\\/.*)?$",
            message = "올바른 body URI 형식이 아닙니다.")
    @JsonProperty("uri")
    @Schema(description = "Body 이미지 URI",
            example = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media",
            required = true)
    private String uri;
}
