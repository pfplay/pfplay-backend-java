package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.user.domain.enums.AuthorityTier;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MyInfoResponse {
    private String uid;
    private AuthorityTier authorityTier;
    private boolean isProfileUpdated;
    private LocalDate registrationDate;
}
