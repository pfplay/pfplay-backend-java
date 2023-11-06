package com.pfplaybackend.api.partyroom.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartyRoomCreateRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣\\s]{1,30}$", message = "한글 30자, 영문30자 제한 / 특수문자 사용 불가")
    @Schema(description = "이름", example = "이름")
    private String name;

    @Pattern(regexp = "^(?:(?=\\S*[\\p{L}])[\\p{L}\\s]{1,50})$", message = "한/영 구분없이 띄어쓰기 포함 50자 제한")
    @Schema(description = "소개",  example = "소개")
    private String introduce;

    @Schema(description = "도메인", example = "song")
    private String domain;

    @Max(100)
    @Min(value = 3, message = "디제잉 1회 당 제한 시간은 3분 이상부터 가능해요")
    @Schema(description = "디제잉 제한 시간", example = "3")
    private int limit;

}
