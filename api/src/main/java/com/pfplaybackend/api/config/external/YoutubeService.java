package com.pfplaybackend.api.config.external;

import com.google.api.services.youtube.model.SearchListResponse;
import org.springframework.stereotype.Service;

@Service
public interface YoutubeService {
    public SearchListResponse getSearchList(String q, String pageToken);
    public String getVideoDuration(String videoId);
}
