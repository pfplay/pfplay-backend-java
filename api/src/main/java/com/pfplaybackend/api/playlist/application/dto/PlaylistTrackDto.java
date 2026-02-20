package com.pfplaybackend.api.playlist.application.dto;

public record PlaylistTrackDto(
        Long trackId,
        String linkId,
        String name,
        Integer orderNumber,
        String duration,
        String thumbnailImage
) {}
