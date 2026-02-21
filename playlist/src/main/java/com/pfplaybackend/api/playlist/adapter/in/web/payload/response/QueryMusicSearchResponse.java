package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultRawDto;
import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Data
@Getter
@Builder
public class QueryMusicSearchResponse {
    private List<MusicData> musicList;

    public static QueryMusicSearchResponse from(SearchResultDto searchMusicResultDto) {
        return QueryMusicSearchResponse.builder()
                .musicList(searchMusicResultDto.data().stream().map(it -> MusicData.builder()
                        .videoId(it.video_id())
                        .videoTitle(it.video_title())
                        .runningTime(it.running_time())
                        .thumbnailUrl(it.thumbnail_url())
                        .build()).toList())
                .build();
    }

    @Getter
    @Builder
    static class MusicData {
        private String videoId;
        private String videoTitle;
        private String runningTime;
        private String thumbnailUrl;
    }
}