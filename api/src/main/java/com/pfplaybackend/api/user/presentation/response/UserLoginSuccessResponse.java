package com.pfplaybackend.api.user.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginSuccessResponse {
    private final boolean registered;
    private final String authority;
    private final Long id;
    private final String name;
}
