package com.pfplaybackend.api.user.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class UserInfoResponse {

    private UUID uid;

    @Builder
    public UserInfoResponse(UUID uid) {
        this.uid = uid;
    }
}
