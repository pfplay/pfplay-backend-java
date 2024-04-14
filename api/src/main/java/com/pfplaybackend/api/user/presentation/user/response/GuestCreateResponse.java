package com.pfplaybackend.api.user.presentation.user.response;

import com.pfplaybackend.api.common.enums.Authority;
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
}
