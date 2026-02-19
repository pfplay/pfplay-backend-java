package com.pfplaybackend.api.user.adapter.in.web.payload.response;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
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

    public static MyInfoResponse fromMember(MemberData member) {
        return MyInfoResponse.builder()
                .uid(member.getUserId().getUid().toString())
                .email(member.getEmail())
                .isProfileUpdated(member.isProfileUpdated())
                .registrationDate(member.getCreatedAt().toLocalDate())
                .authorityTier(member.getAuthorityTier())
                .build();
    }

    public static MyInfoResponse fromGuest(GuestData guest) {
        return MyInfoResponse.builder()
                .uid(guest.getUserId().getUid().toString())
                .email(null)
                .isProfileUpdated(guest.isProfileUpdated())
                .registrationDate(guest.getCreatedAt().toLocalDate())
                .authorityTier(guest.getAuthorityTier())
                .build();
    }
}
