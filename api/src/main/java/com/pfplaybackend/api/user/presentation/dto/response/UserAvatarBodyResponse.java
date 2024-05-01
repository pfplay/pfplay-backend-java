package com.pfplaybackend.api.user.presentation.dto.response;

import com.pfplaybackend.api.user.application.dto.AvatarBodyDto;

import java.util.List;

public record UserAvatarBodyResponse(List<AvatarBodyDto> avatarBodyList) {
}
