package com.pfplaybackend.api.guest.presentation.response;

import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.presentation.dto.UserPermissionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GuestCreateResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    @Schema(implementation = Authority.class)
    private final Authority authority;
    private final String accessToken;
    @Schema(implementation = UserPermissionDto.class, description = "기능 접근 권한")
    private final UserPermissionDto userPermission;
}
