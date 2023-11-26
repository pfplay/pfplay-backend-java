package com.pfplaybackend.api.user.presentation.response;

import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.presentation.dto.UserPermissionDto;
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
    @Schema(implementation = UserPermissionDto.class, description = "기능 접근 권한")
    private final UserPermissionDto userPermission;
    @Schema(description = "유저 프로필 업데이트 여부", defaultValue = "false")
    private final boolean profileUpdated;
}
