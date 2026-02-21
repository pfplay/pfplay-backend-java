package com.pfplaybackend.api.user.application.dto.result;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;

import java.time.LocalDate;

public record MyInfoResult(
        String uid,
        String email,
        AuthorityTier authorityTier,
        boolean isProfileUpdated,
        LocalDate registrationDate
) {
    public static MyInfoResult from(UserAccountData user) {
        return new MyInfoResult(
                user.getUserId().getUid().toString(),
                user.getEmail(),
                user.getAuthorityTier(),
                user.isProfileUpdated(),
                user.getCreatedAt().toLocalDate()
        );
    }
}
