package com.pfplaybackend.api.user.presentation.dto.response;

import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GuestCreateResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    @Schema(implementation = AuthorityTier.class)
    private final AuthorityTier authorityTier;
    private final String accessToken;
}
