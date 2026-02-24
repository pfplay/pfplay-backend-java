package com.pfplaybackend.api.playlist.application.service.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicSearchService {

    private final YoutubeSearchService youtubeSearchService;

    public SearchResultDto getSearchList(String q) {
        return youtubeSearchService.searchByWord(q, 10);
    }
}
