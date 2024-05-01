package com.pfplaybackend.api.user.presentation.dto.response;

import com.pfplaybackend.api.user.model.enums.AuthorityTier;
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
    @Schema(implementation = AuthorityTier.class)
    private final AuthorityTier authorityTier;
    private final String accessToken;
}
