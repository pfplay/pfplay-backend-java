package com.pfplaybackend.api.playlist.application.dto.search;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicRawData {
    private String video_id;
    private String video_title;
    private String watch_url;
    private String running_time;
    private String thumbnail_url;
}
