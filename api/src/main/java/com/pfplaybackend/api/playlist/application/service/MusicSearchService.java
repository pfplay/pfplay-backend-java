package com.pfplaybackend.api.playlist.application.service;

import com.google.api.services.youtube.model.SearchListResponse;
import com.pfplaybackend.api.playlist.application.dto.SearchPlaylistMusicDto;
import com.pfplaybackend.api.playlist.presentation.payload.response.SearchPlaylistMusicResponse;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class MusicSearchService {

    public SearchPlaylistMusicResponse getSearchList() {
        return null;
    }

//    public SearchPlaylistMusicResponse getSearchList(String q, String pageToken) {
//        try {
//            SearchListResponse searchResponse = youtubeService.getSearchList(q, pageToken);
//            List<SearchPlaylistMusicDto> musicList = new ArrayList<>();
//
//            for (SearchResult item : searchResponse.getItems()) {
//                // 검색 결과 encoding 된 title 을 decoding 및 formatting 처리
//                String decodedTitle = URLDecoder.decode(item.getSnippet().getTitle(), "UTF-8");
//                String formattedTitle =
//                        decodedTitle.replaceAll("&lt;", "<")
//                                .replaceAll("&gt;", ">")
//                                .replaceAll("&quot;", "\"")
//                                .replaceAll("&apos;", "'")
//                                .replaceAll("&amp;", "&")
//                                .replaceAll("&#39;", "'");
//
//                // video id 를 통해 video.list 를 호출하여 동영상 재생 시간을 조회
//                String videoId = item.getId().getVideoId();
//                String duration = youtubeService.getVideoDuration(videoId);
//
//                SearchPlaylistMusicDto music = SearchPlaylistMusicDto.builder()
//                        .id(videoId)
//                        .thumbnailLow(item.getSnippet().getThumbnails().getMedium().getUrl())
//                        .thumbnailMedium(item.getSnippet().getThumbnails().getMedium().getUrl())
//                        .thumbnailHigh(item.getSnippet().getThumbnails().getHigh().getUrl())
//                        .title(formattedTitle)
//                        .duration(duration)
//                        .build();
//                musicList.add(music);
//            }
//
//            SearchPlaylistMusicResponse musicListResponse = SearchPlaylistMusicResponse.builder()
//                    .nextPageToken(searchResponse.getNextPageToken())
//                    .musicList(musicList)
//                    .build();
//
//            return musicListResponse;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}