package com.pfplaybackend.api.playlist.application.dto.command;

public record AddTrackCommand(String name, String linkId, String duration, String thumbnailImage) {
}
