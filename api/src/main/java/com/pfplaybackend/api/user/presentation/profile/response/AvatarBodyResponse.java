package com.pfplaybackend.api.user.presentation.profile.response;

import com.pfplaybackend.api.user.presentation.profile.dto.AvatarBodyDto;
import lombok.Data;

import java.util.List;

@Data
public class AvatarBodyResponse {
    private final List<AvatarBodyDto> avatarBodyList;
}
