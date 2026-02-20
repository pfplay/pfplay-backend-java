package com.pfplaybackend.api.playlist.application.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchResultDto {
    private String message;
    private List<SearchResultRawData> data;
}
