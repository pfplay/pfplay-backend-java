package com.pfplaybackend.api.admin.application.dto.command;

public record AdminCreatePartyroomCommand(
        String hostUserId,
        String title,
        String introduction,
        String linkDomain,
        Integer playbackTimeLimit
) {
}
