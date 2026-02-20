package com.pfplaybackend.api.user.adapter.in.web.payload.response;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MyInfoResponse {
    private String uid;
    private String email;
    private AuthorityTier authorityTier;
    private boolean isProfileUpdated;
    private LocalDate registrationDate;

    public static MyInfoResponse from(UserAccountData user) {
        return MyInfoResponse.builder()
                .uid(user.getUserId().getUid().toString())
                .email(user.getEmail())
                .isProfileUpdated(user.isProfileUpdated())
                .registrationDate(user.getCreatedAt().toLocalDate())
                .authorityTier(user.getAuthorityTier())
                .build();
    }
}
