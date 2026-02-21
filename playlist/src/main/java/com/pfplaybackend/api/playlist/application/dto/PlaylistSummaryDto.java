package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;

public record PlaylistSummaryDto(Long id, String name, Integer orderNumber, PlaylistType type, Long musicCount) {}
