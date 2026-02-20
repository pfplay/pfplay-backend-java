package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultRawData;
import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Data
@Getter
@Builder
public class SearchMusicResponse {
    private List<MusicData> musicList;

    public static SearchMusicResponse from(SearchResultDto searchMusicResultDto) {
        return SearchMusicResponse.builder()
                .musicList(searchMusicResultDto.getData().stream().map(it -> MusicData.builder()
                        .videoId(it.getVideo_id())
                        .videoTitle(it.getVideo_title())
                        .runningTime(it.getRunning_time())
                        .thumbnailUrl(it.getThumbnail_url())
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