package com.pfplaybackend.api.playlist.application;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.pfplaybackend.api.config.external.YoutubeService;
import com.pfplaybackend.api.playlist.model.entity.Playlist;
import com.pfplaybackend.api.playlist.model.entity.PlaylistMusic;
import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import com.pfplaybackend.api.playlist.exception.PlayListMusicLimitExceededException;
import com.pfplaybackend.api.playlist.application.dto.MusicListDto;
import com.pfplaybackend.api.playlist.application.dto.PlayListCreateDto;
import com.pfplaybackend.api.playlist.application.dto.SearchMusicListDto;
import com.pfplaybackend.api.playlist.presentaion.dto.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.response.MusicListAddResponse;
import com.pfplaybackend.api.playlist.presentaion.dto.response.MusicListResponse;
import com.pfplaybackend.api.playlist.presentaion.dto.response.SearchMusicListResponse;
import com.pfplaybackend.api.playlist.repository.MusicListRepository;
import com.pfplaybackend.api.playlist.repository.PlayListRepository;
import com.pfplaybackend.api.user.model.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayListService {
    private PlayListRepository playListRepository;
    private MusicListRepository musicListRepository;
    private YoutubeService youtubeService;

    public Playlist createPlayList(PlayListCreateRequest request, Member member) {
        List<Playlist> result = playListRepository.findByUserIdAndTypeOrderByOrderNumberDesc(member.getUserId(), PlaylistType.PLAYLIST);

        // TODO 플레이리스트 생성 권한 여부
        // TODO 플레이리스트 생성 조건
        // TODO 플레이리스트 생성 제약

//        if (result.size() > 0 && member.getWalletAddress() == null) {
//            throw new PlayListNoWalletException("생성 개수 제한 초과 (지갑 미연동)");
//        }
//        if (result.size() > 9 && member.getWalletAddress() != null) {
//            throw new PlayListLimitExceededException("생성 개수 제한 초과");
//        }

        PlayListCreateDto dto;
        if (!result.isEmpty()) {
            dto = PlayListCreateDto.builder()
                    .orderNumber(result.get(0).getOrderNumber() + 1)
                    .userId(member.getUserId())
                    .name(request.getName())
                    .type(PlaylistType.PLAYLIST)
                    .build();
        } else {
            dto = PlayListCreateDto.builder()
                    .orderNumber(1)
                    .userId(member.getUserId())
                    .name(request.getName())
                    .type(PlaylistType.PLAYLIST)
                    .build();
        }

        return playListRepository.save(dto.toEntity());
    }

//    public List<PlayListResponse> getPlayList(Member member) {
//        List<Tuple> result = playListClassRepository.findByUserId(member.getId());
//        List<PlayListResponse> dtoList = new ArrayList<>();
//        for (Tuple tuple : result) {
//            PlayListResponse dto = PlayListResponse.builder()
//                    .id(tuple.get(playList.id))
//                    .name(tuple.get(playList.name))
//                    .orderNumber(tuple.get(playList.orderNumber))
//                    .type(tuple.get(playList.type))
//                    .count(tuple.get(Expressions.numberPath(Long.class, "count")))
//                    .build();
//            dtoList.add(dto);
//        }
//        return dtoList;
//    }

    public MusicListResponse getMusicList(int page, int pageSize, Long playListId) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));

        int totalPage = (int) Math.ceil(musicListRepository.countByPlayListId(playListId) / pageSize);
        Page<PlaylistMusic> result = musicListRepository.findByPlayListIdOrderByOrderNumber(pageable, playListId);
        List<MusicListDto> dtoList = new ArrayList<>();
        for (PlaylistMusic playlistMusic : result) {
            MusicListDto dto = MusicListDto.builder()
                    .musicId(playlistMusic.getId())
                    .orderNumber(playlistMusic.getOrderNumber())
                    .name(playlistMusic.getName())
                    .duration(playlistMusic.getDuration())
                    .thumbnailImage(playlistMusic.getThumbnailImage())
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
            SearchListResponse searchResponse = youtubeService.getSearchList(q, pageToken);
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
                String duration = youtubeService.getVideoDuration(videoId);

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
        Optional<Playlist> playList = playListRepository.findById(playListId);

        if (!playList.isPresent()) {
            throw new NoSuchElementException("존재하지 않는 플레이리스트");
        }

        // 곡 추가 중복 여부 및 곡 순서 번호 체크 후 저장 처리
        List<PlaylistMusic> playlistMusic = musicListRepository.findAllByPlayListId(playListId);
        if (playlistMusic.size() > 100) {
            throw new PlayListMusicLimitExceededException("곡 개수 제한 초과");
        }

        int orderNumber = 1;
        for (PlaylistMusic music : playlistMusic) {
//            if (music.getUid().equals(request.getUid())) {
//                throw new DuplicateKeyException(""); // Global Exception으로 처리되어 입력한 메시지 전달 X
//            }
            if (music.getOrderNumber() > orderNumber) {
                orderNumber = music.getOrderNumber();
            }
        }

        int newOrderNumber = playlistMusic.isEmpty() ? orderNumber : orderNumber + 1;

        PlaylistMusic music = PlaylistMusic.builder()
                .playList(playList.get())
                .uid(request.getUid())
                .orderNumber(newOrderNumber)
                .name(request.getName())
                .duration(request.getDuration())
                .thumbnailImage(request.getThumbnailImage())
                .build();

        PlaylistMusic result =  musicListRepository.save(music);
        MusicListAddResponse response = MusicListAddResponse.builder()
                .playListId(playListId)
                .musicId(result.getId())
                .orderNumber(result.getOrderNumber())
                .name(request.getName())
                .duration(request.getDuration())
                .build();

        return response;
    }

    @Transactional
    public void deletePlayList(UUID userId, List<Long> listIds) {
//        List<Long> ids = playListClassRepository.findByUserIdAndListIdAndType(userId, listIds, PlayListType.PLAYLIST);
//        if (ids.size() != listIds.size()) {
//            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 플레이리스트");
//        }
//        try {
//            musicListClassRepository.deleteByPlayListIds(ids);
//            Long count = playListClassRepository.deleteByListIds(ids);
//            if (count != ids.size()) {
//                throw new InvalidDeleteRequestException("비정상적인 삭제 요청");
//            }
//        } catch (Exception e) {
//            if (e instanceof InvalidDeleteRequestException) {
//                throw e;
//            }
//            throw new RuntimeException(e);
//        }
    }

    @Transactional
    public void deleteMusicList(UUID userId, List<Long> listIds) {
        // 곡 보유자가 삭제 요청자 Id와 일치하는지 확인
        // TODO '리소스 쓰기 권한' 여부 확인 절차
//        MusicList musicList = musicListRepository.findById(listIds.get(0)).orElseThrow(() -> new NoSuchElementException("존재하지 않거나 유효하지 않은 곡"));
//        if (userId != musicList.getPlayList().getMember().getId()) {
//            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 곡");
//        }
//        try {
//            Long playListId = musicList.getPlayList().getId();
//            Long count = musicListClassRepository.deleteByIdsAndPlayListId(listIds, playListId);
//            if (count != listIds.size()) {
//                throw new InvalidDeleteRequestException("비정상적인 삭제 요청");
//            }
//        } catch (Exception e) {
//            if (e instanceof InvalidDeleteRequestException) {
//                throw e;
//            }
//            throw new RuntimeException(e);
//        }
    }

    public void renamePlayList(UUID userId, Long playListId, String name) {
        Playlist playList = playListRepository.findByIdAndUserIdAndType(playListId, userId, PlaylistType.PLAYLIST);
        if(playList == null) {
            throw new NoSuchElementException("존재하지 않는 플레이리스트");
        }
        playList.rename(name);
        playListRepository.save(playList);
    }
}
