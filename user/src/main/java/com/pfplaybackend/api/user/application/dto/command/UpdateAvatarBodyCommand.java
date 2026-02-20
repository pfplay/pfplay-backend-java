package com.pfplaybackend.api.user.application.dto.command;

import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;

public record UpdateAvatarBodyCommand(AvatarBodyUri avatarBodyUri) {}
