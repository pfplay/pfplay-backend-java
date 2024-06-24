package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.playlist.application.dto.search.MusicRawData;
import com.pfplaybackend.api.playlist.application.dto.search.SearchMusicResultDto;
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

    public static SearchMusicResponse from(SearchMusicResultDto searchMusicResultDto) {
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