package com.pfplaybackend.api.external.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;

@Service
public class YouTubeService {
    @Value("${google.youtube.api-key}")
    private String key;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // YouTube 객체 생성
    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request)
                    throws IOException {
            }
        })
                .setApplicationName("PFPlay")
                .build();
        return youtube;
    }

    // YouTube Data API의 동영상 길이를 포맷팅
    private String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;

        String hoursStr = String.format("%02d", hours);
        String minutesStr = minutes == 0 ? "0" : String.format("%02d", minutes);
        String secondsStr = String.format("%02d", seconds);

        String formattedDuration = "";
        if (hours > 0) {
            formattedDuration += hoursStr + ":";
        }
        formattedDuration += minutesStr + ":";
        formattedDuration += secondsStr;

        return formattedDuration;
    }

    // YouTube Data API search.list
    public SearchListResponse getSearchList(String q, String pageToken) {
        try {
            YouTube youtubeService = getService();
            YouTube.Search.List searchRequest = youtubeService.search().list("snippet").setKey(key);

            SearchListResponse searchResponse =
                    pageToken == null ?
                            searchRequest.setQ(q).setType("video").execute() :
                            searchRequest.setPageToken(pageToken).setQ(q).setType("video").execute();

            return searchResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // YouTube Data API video.list - contentDetails
    public String getVideoDuration(String videoId) {
        try {
            YouTube youtubeService = getService();
            YouTube.Videos.List videoRequest = youtubeService.videos().list("contentDetails").setKey(key);
            VideoListResponse videoResponse = videoRequest.setId(videoId).execute();
            String duration = formatDuration(videoResponse.getItems().get(0).getContentDetails().getDuration());
            return duration;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}