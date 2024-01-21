package com.pfplaybackend.api.playlist.service;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.pfplaybackend.api.entity.MusicList;
import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.external.youtube.YouTubeService;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.playlist.exception.PlayListLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlayListMusicLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlayListNoWalletException;
import com.pfplaybackend.api.playlist.presentation.dto.MusicListDto;
import com.pfplaybackend.api.playlist.presentation.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.presentation.dto.SearchMusicListDto;
import com.pfplaybackend.api.playlist.presentation.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentation.request.PlayListDeleteRequest;
import com.pfplaybackend.api.playlist.presentation.response.MusicListAddResponse;
import com.pfplaybackend.api.playlist.presentation.response.MusicListResponse;
import com.pfplaybackend.api.playlist.presentation.response.PlayListResponse;
import com.pfplaybackend.api.playlist.presentation.response.SearchMusicListResponse;
import com.pfplaybackend.api.playlist.repository.MusicListRepository;
import com.pfplaybackend.api.playlist.repository.PlayListClassRepository;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.*;

import static com.pfplaybackend.api.entity.QPlayList.playList;

@Service
public class PlayListService {
    private PlayListRepository playListRepository;
    private PlayListClassRepository playListClassRepository;
    private MusicListRepository musicListRepository;
    private YouTubeService youTubeService;

    public PlayListService(PlayListRepository playListRepository, PlayListClassRepository playListClassRepository, MusicListRepository musicListRepository, YouTubeService youTubeService) {
        this.playListRepository = playListRepository;
        this.playListClassRepository = playListClassRepository;
        this.musicListRepository = musicListRepository;
        this.youTubeService = youTubeService;
    }

    public PlayList createPlayList(PlayListCreateRequest request, User user) {
        List<PlayList> result = playListRepository.findByUserIdAndTypeOrderByOrderNumberDesc(user.getId(), PlayListType.PLAYLIST);
        if (result.size() > 0 && user.getWalletAddress() == null) {
            throw new PlayListNoWalletException("생성 개수 제한 초과 (지갑 미연동)");
        }
        if (result.size() > 9 && user.getWalletAddress() != null) {
            throw new PlayListLimitExceededException("생성 개수 제한 초과");
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

    public List<PlayListResponse> getPlayList(User user) {
        List<Tuple> result = playListClassRepository.findByUserId(user.getId());
        List<PlayListResponse> dtoList = new ArrayList<>();
        for (Tuple tuple : result) {
            PlayListResponse dto = PlayListResponse.builder()
                    .id(tuple.get(playList.id))
                    .name(tuple.get(playList.name))
                    .orderNumber(tuple.get(playList.orderNumber))
                    .type(tuple.get(playList.type))
                    .count(tuple.get(Expressions.numberPath(Long.class, "count")))
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }

    public MusicListResponse getMusicList(int page, int pageSize, Long playListId) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));

        int totalPage = (int) Math.ceil(musicListRepository.countByPlayListId(playListId) / pageSize);
        Page<MusicList> result = musicListRepository.findByPlayListIdOrderByOrderNumber(pageable, playListId);
        List<MusicListDto> dtoList = new ArrayList<>();
        for (MusicList musicList : result) {
            MusicListDto dto = MusicListDto.builder()
                    .musicId(musicList.getId())
                    .uid(musicList.getUid())
                    .orderNumber(musicList.getOrderNumber())
                    .name(musicList.getName())
                    .duration(musicList.getDuration())
                    .thumbnailImage(musicList.getThumbnailImage())
                    .build();
            dtoList.add(dto);
        }
        MusicListResponse response = MusicListResponse.builder()
                .totalPage(totalPage)
                .musicList(dtoList)
                .build();
        return response;
    }

    public SearchMusicListResponse getSearchList(String q, String pageToken) {
        try {
            SearchListResponse searchResponse = youTubeService.getSearchList(q, pageToken);
            List<SearchMusicListDto> musicList = new ArrayList<>();

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

                SearchMusicListDto music = SearchMusicListDto.builder()
                        .id(videoId)
                        .thumbnailLow(item.getSnippet().getThumbnails().getMedium().getUrl())
                        .thumbnailMedium(item.getSnippet().getThumbnails().getMedium().getUrl())
                        .thumbnailHigh(item.getSnippet().getThumbnails().getHigh().getUrl())
                        .title(formattedTitle)
                        .duration(duration)
                        .build();
                musicList.add(music);
            }

            SearchMusicListResponse musicListResponse = SearchMusicListResponse.builder()
                    .nextPageToken(searchResponse.getNextPageToken())
                    .musicList(musicList)
                    .build();

            return musicListResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MusicListAddResponse addMusic(Long playListId, MusicListAddRequest request) {
        Optional<PlayList> playList = playListRepository.findById(playListId);

        if (!playList.isPresent()) {
            throw new NoSuchElementException("존재하지 않는 플레이리스트");
        }

        // 곡 추가 중복 여부 및 곡 순서 번호 체크 후 저장 처리
        List<MusicList> musicList = musicListRepository.findAllByPlayListId(playListId);
        if (musicList.size() > 100) {
            throw new PlayListMusicLimitExceededException("곡 개수 제한 초과");
        }

        int orderNumber = 1;
        for (MusicList music : musicList) {
            if (music.getUid().equals(request.getUid())) {
                throw new DuplicateKeyException(""); // Global Exception으로 처리되어 입력한 메시지 전달 X
            }
            if (music.getOrderNumber() > orderNumber) {
                orderNumber = music.getOrderNumber();
            }
        }

        int newOrderNumber = musicList.isEmpty() ? orderNumber : orderNumber + 1;

        MusicList music = MusicList.builder()
                .playList(playList.get())
                .uid(request.getUid())
                .orderNumber(newOrderNumber)
                .name(request.getName())
                .duration(request.getDuration())
                .thumbnailImage(request.getThumbnailImage())
                .build();

        MusicList result =  musicListRepository.save(music);
        MusicListAddResponse response = MusicListAddResponse.builder()
                .playListId(playListId)
                .musicId(result.getId())
                .orderNumber(result.getOrderNumber())
                .name(request.getName())
                .duration(request.getDuration())
                .build();

        return response;
    }

    public ResponseEntity<?> deletePlayList(Long userId, PlayListDeleteRequest request) {

        return null;
    }
}
