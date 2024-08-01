package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchMusicResultDto;

public interface YoutubeSearchService {
    SearchMusicResultDto searchByWord(String query, int rows);
}
