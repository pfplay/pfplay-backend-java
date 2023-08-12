package com.pfplaybackend.api.guest.presentation.response;

import com.pfplaybackend.api.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GuestCreateResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    @Schema(implementation = Authority.class)
    private final Authority authority;
}
