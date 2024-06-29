package com.pfplaybackend.api.user.application.dto.shared;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileSettingDto {
    private String nickname;
    private String avatarBodyUri;
    private String avatarFaceUri;
}
