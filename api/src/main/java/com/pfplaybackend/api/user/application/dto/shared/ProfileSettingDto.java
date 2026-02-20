package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProfileSettingDto {
    private String nickname;
    private AvatarCompositionType avatarCompositionType;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;
}
