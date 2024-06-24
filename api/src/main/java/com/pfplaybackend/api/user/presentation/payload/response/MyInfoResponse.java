package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.User;
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

    public static MyInfoResponse from(User user) {
        // TODO Code Refactoring
        String email = null;
        if(!user.getAuthorityTier().equals(AuthorityTier.GT)) {
            Member member = (Member) user;
            email = member.getEmail();
        }
        return MyInfoResponse.builder()
                .uid(user.getUserId().getUid().toString())
                .email(email)
                .isProfileUpdated(user.isProfileUpdated())
                .registrationDate(user.getCreatedAt().toLocalDate())
                .authorityTier(user.getAuthorityTier())
                .build();
    }
}