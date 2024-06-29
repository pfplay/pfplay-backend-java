package com.pfplaybackend.api.user.application.dto.command;

import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAvatarBodyCommand {
    private AvatarBodyUri avatarBodyUri;
}
