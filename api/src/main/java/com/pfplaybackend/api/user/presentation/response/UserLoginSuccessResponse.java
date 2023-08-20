package com.pfplaybackend.api.user.presentation.response;

import com.pfplaybackend.api.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginSuccessResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    @Schema(implementation = Authority.class)
    private final Authority authority;
    private final String accessToken;
}
