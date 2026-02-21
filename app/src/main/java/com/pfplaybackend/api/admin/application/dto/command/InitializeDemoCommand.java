package com.pfplaybackend.api.admin.application.dto.command;

public record InitializeDemoCommand(
        Integer playbackTimeLimit,
        String titlePrefix,
        String introduction,
        Boolean registerDjs
) {
}
