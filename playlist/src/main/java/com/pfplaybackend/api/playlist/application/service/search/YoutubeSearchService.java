package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;

public interface YoutubeSearchService {
    SearchResultDto searchByWord(String query, int rows);
}
