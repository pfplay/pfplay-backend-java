package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "유저")
public class UserDto {
    private String email;
    private String nickname;
    @Schema(description = "소개")
    private String introduction;
    @Schema(implementation = Authority.class)
    private Authority authority;
    @Schema(description = "지갑 연동 주소")
    private String walletAddress;
    private Integer djScore;
    private Integer taskScore;
    private Integer bodyId;
    private String faceUrl;
}
