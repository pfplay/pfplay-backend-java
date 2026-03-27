package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
    public static class MusicData {
        @Schema(example = "dQw4w9WgXcQ") private String videoId;
        @Schema(example = "Rick Astley - Never Gonna Give You Up") private String videoTitle;
        @Schema(example = "3:33") private String runningTime;
        @Schema(example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg") private String thumbnailUrl;
    }
}