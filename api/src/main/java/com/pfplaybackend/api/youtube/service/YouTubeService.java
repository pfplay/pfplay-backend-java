package com.pfplaybackend.api.youtube.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.pfplaybackend.api.youtube.presentation.dto.MusicList;
import com.pfplaybackend.api.youtube.presentation.response.MusicListResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class YouTubeService {
    private static final String CLIENT_SECRETS = "/client_secret-local.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        try {
            InputStream in = YouTubeService.class.getResourceAsStream(CLIENT_SECRETS);
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                            .build();
            Credential credential =
                    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

            return credential;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        Long hours = duration.toHours();
        Long minutes = duration.toMinutes() % 60;
        Long seconds = duration.toSeconds() % 60;

        String hoursStr = String.format("%02d", hours);
        String minutesStr = minutes == 0 ? "0:" : String.format("%02d", minutes);
        String secondsStr = String.format("%02d", seconds);

        String formattedDuration = "";
        if (hours > 0) {
            formattedDuration += hoursStr + ":";
        }
        formattedDuration += minutesStr + ":";
        formattedDuration += secondsStr;

        return formattedDuration;
    }

    public MusicListResponse getSearchList() {
        try {
            YouTube youtubeService = getService();
            YouTube.Search.List searchRequest = youtubeService.search().list("snippet");
            SearchListResponse searchResponse = searchRequest.setQ("뉴진스").execute();
            /* nextPageToken 있는 경우 */
//            SearchListResponse response = request.setPageToken("CAUQAA")
//                    .setQ("뉴진스")
//                    .execute();

            List<MusicList> musicList = new ArrayList<>();

            for (SearchResult item : searchResponse.getItems()) {
                String videoId = item.getId().getVideoId();

                String decodedTitle = URLDecoder.decode(item.getSnippet().getTitle(), "UTF-8");
                String formattedTitle =
                        decodedTitle.replaceAll("&lt;", "<")
                                .replaceAll("&gt;", ">")
                                .replaceAll("&quot;", "\"")
                                .replaceAll("&apos;", "'")
                                .replaceAll("&amp;", "&")
                                .replaceAll("&#39;", "'");

                YouTube.Videos.List videoRequest = youtubeService.videos().list("contentDetails");
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
                    .musicList(musicList)
                    .nextPageToken(searchResponse.getNextPageToken())
                    .build();

            return musicListResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}