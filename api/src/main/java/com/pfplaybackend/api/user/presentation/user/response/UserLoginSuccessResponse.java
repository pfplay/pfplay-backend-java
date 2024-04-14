package com.pfplaybackend.api.user.presentation.user.response;

import com.pfplaybackend.api.common.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginSuccessResponse {
    private final Long id;
    private final String name;
    @Schema(description = "회원가입 여부")
    private final boolean registered;
    @Schema(implementation = Authority.class)
    private final Authority authority;
    private final String accessToken;
}
