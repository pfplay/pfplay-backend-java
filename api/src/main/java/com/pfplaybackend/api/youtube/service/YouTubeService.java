package com.pfplaybackend.api.youtube.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.pfplaybackend.api.youtube.presentation.dto.MusicList;
import com.pfplaybackend.api.youtube.presentation.response.MusicListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    // YouTube Data API v3의 동영상 길이를 포맷팅
    public static String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        Long hours = duration.toHours();
        Long minutes = duration.toMinutes() % 60;
        Long seconds = duration.toSeconds() % 60;

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

    // YouTube Data API search.list & videos.list 호출
    public MusicListResponse getSearchList(String q, String pageToken) {
        try {
            YouTube youtubeService = getService();
            YouTube.Search.List searchRequest = youtubeService.search().list("snippet").setKey(key);

            // pagination 처리
            SearchListResponse searchResponse =
                    pageToken == null ?
                    searchRequest.setQ(q).setType("video").execute() :
                    searchRequest.setPageToken(pageToken).setQ(q).setType("video").execute();

            List<MusicList> musicList = new ArrayList<>();

            // 검색 결과에 대해 videos.list를 호출하여 동영상 재생 시간을 조회
            for (SearchResult item : searchResponse.getItems()) {
                String videoId = item.getId().getVideoId();
                String decodedTitle = URLDecoder.decode(item.getSnippet().getTitle(), "UTF-8");

                // YouTube Data API의 특수문자 처리
                String formattedTitle =
                        decodedTitle.replaceAll("&lt;", "<")
                                .replaceAll("&gt;", ">")
                                .replaceAll("&quot;", "\"")
                                .replaceAll("&apos;", "'")
                                .replaceAll("&amp;", "&")
                                .replaceAll("&#39;", "'");

                YouTube.Videos.List videoRequest = youtubeService.videos().list("contentDetails").setKey(key);
                VideoListResponse videoResponse = videoRequest.setId(videoId).execute();

                String duration = formatDuration(videoResponse.getItems().get(0).getContentDetails().getDuration());

                MusicList music = MusicList.builder()
                        .id(videoId)
                        .thumbnailLow(item.getSnippet().getThumbnails().getMedium().getUrl())
                        .thumbnailMedium(item.getSnippet().getThumbnails().getMedium().getUrl())
                        .thumbnailHigh(item.getSnippet().getThumbnails().getHigh().getUrl())
                        .title(formattedTitle)
                        .duration(duration)
                        .build();
                musicList.add(music);
            }

            MusicListResponse musicListResponse = MusicListResponse.builder()
                    .nextPageToken(searchResponse.getNextPageToken())
                    .musicList(musicList)
                    .build();

            return musicListResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}