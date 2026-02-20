package com.pfplaybackend.api.playlist.application.dto.search;

public record SearchResultRawData(
        String video_id,
        String video_title,
        String watch_url,
        String running_time,
        String thumbnail_url
) {}
