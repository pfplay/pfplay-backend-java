package com.pfplaybackend.api.playlist.service;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.external.youtube.YouTubeService;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.presentation.dto.MusicListDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.response.MusicListResponse;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.*;

@Service
public class PlayListService {
    private PlayListRepository playListRepository;
    private YouTubeService youTubeService;

    public PlayListService(PlayListRepository playListRepository, YouTubeService youTubeService) {
        this.playListRepository = playListRepository;
        this.youTubeService = youTubeService;
    }

    public PlayList createPlayList(PlayListCreateRequest request, User user) {
        List<PlayList> result = playListRepository.findByUserIdAndTypeOrderByOrderNumberDesc(user.getId(), PlayListType.PLAYLIST);
        if (result.size() > 0 && user.getWalletAddress() == null) {
            throw new RuntimeException("생성 개수 제한 초과 (지갑 미연동)");
        }
        if (result.size() > 9 && user.getWalletAddress() != null) {
            throw new RuntimeException("생성 개수 제한 초과");
        }

        PlayListCreateDto dto;
        if (!result.isEmpty()) {
            dto = PlayListCreateDto.builder()
                    .orderNumber(result.get(0).getOrderNumber() + 1)
                    .user(user)
                    .name(request.getName())
                    .type(PlayListType.PLAYLIST)
                    .build();
        } else {
            dto = PlayListCreateDto.builder()
                    .orderNumber(1)
                    .user(user)
                    .name(request.getName())
                    .type(PlayListType.PLAYLIST)
                    .build();
        }

        return playListRepository.save(dto.toEntity());
    }

    public List<PlayListDto> getPlayList(User user) {
        List<PlayList> result = playListRepository.findByUserIdOrderByTypeDescOrderNumberAsc(user.getId());
        List<PlayListDto> dtoList = new ArrayList<>();
        for (PlayList playList : result) {
            PlayListDto dto = PlayListDto.builder()
                    .id(playList.getId())
                    .name(playList.getName())
                    .orderNumber(playList.getOrderNumber())
                    .type(playList.getType()).build();
            dtoList.add(dto);
        }
        return dtoList;
    }

    public MusicListResponse getSearchList(String q, String pageToken) {
        try {
            SearchListResponse searchResponse = youTubeService.getSearchList(q, pageToken);
            List<MusicListDto> musicList = new ArrayList<>();

            for (SearchResult item : searchResponse.getItems()) {
                // 검색 결과 encoding 된 title 을 decoding 및 formatting 처리
                String decodedTitle = URLDecoder.decode(item.getSnippet().getTitle(), "UTF-8");
                String formattedTitle =
                        decodedTitle.replaceAll("&lt;", "<")
                                .replaceAll("&gt;", ">")
                                .replaceAll("&quot;", "\"")
                                .replaceAll("&apos;", "'")
                                .replaceAll("&amp;", "&")
                                .replaceAll("&#39;", "'");

                // video id 를 통해 video.list 를 호출하여 동영상 재생 시간을 조회
                String videoId = item.getId().getVideoId();
                String duration = youTubeService.getVideoDuration(videoId);

                MusicListDto music = MusicListDto.builder()
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
