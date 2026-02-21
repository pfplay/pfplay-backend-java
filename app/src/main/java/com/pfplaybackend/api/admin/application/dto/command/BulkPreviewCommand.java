package com.pfplaybackend.api.admin.application.dto.command;

public record BulkPreviewCommand(
        Integer partyroomCount,
        Integer usersPerRoom,
        String titlePrefix,
        String introduction,
        String linkDomainPrefix,
        Integer playbackTimeLimit
) {
}
