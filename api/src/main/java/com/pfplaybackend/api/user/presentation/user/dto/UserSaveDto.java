package com.pfplaybackend.api.user.presentation.user.dto;

import com.pfplaybackend.api.common.enums.Authority;
import com.pfplaybackend.api.user.model.entity.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSaveDto {
    private String email;
    private Authority authority;

    public User toEntity() {
        return User.builder()
                .email(email)
                .authority(authority)
                .build();
    }
}
