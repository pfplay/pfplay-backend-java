package com.pfplaybackend.api.user.application.dto;

import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSaveDto {
    private String email;
    private AuthorityTier authorityTier;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .build();
    }
}
