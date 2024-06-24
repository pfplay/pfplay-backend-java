package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignGuestResponse {
    private final Long id;
    private final String name;
    private final boolean registered;
    @Schema(implementation = AuthorityTier.class)
    private final AuthorityTier authorityTier;
    private final String accessToken;
}
