package com.pfplaybackend.api.playlist.application.dto.search;

import java.util.List;

public record SearchResultDto(String message, List<SearchResultRawDto> data) {}
