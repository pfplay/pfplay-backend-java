package com.pfplaybackend.api.user.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAvatarFaceCommand {
    private String avatarFaceUri;
}
