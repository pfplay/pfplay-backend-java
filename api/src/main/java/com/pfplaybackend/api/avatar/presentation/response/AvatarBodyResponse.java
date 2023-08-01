package com.pfplaybackend.api.avatar.presentation.response;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import lombok.Data;

import java.util.List;

@Data
public class AvatarBodyResponse {
    private final List<AvatarBodyDto> avatarBodyList;
}
