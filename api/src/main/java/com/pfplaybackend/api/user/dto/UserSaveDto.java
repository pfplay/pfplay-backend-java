package com.pfplaybackend.api.user.dto;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
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
