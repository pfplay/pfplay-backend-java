package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.config.external.YoutubeService;
import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository musicListRepository;
    private YoutubeService youtubeService;

    @Transactional
    public Playlist createPlaylist(String name, UserId ownerId) {
        List<Playlist> result = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(ownerId, PlaylistType.PLAYLIST);
        // TODO 플레이리스트 생성 권한 여부
        // TODO 플레이리스트 생성 조건
        // TODO 플레이리스트 생성 제약

//        if (result.size() > 0 && member.getWalletAddress() == null) {
//            throw new PlaylistNoWalletException("생성 개수 제한 초과 (지갑 미연동)");
//        }
//        if (result.size() > 9 && member.getWalletAddress() != null) {
//            throw new PlaylistLimitExceededException("생성 개수 제한 초과");
//        }

        Playlist playlist;
        if (!result.isEmpty()) {
            playlist = Playlist.create(result.get(0).getOrderNumber() + 1, name, PlaylistType.PLAYLIST, ownerId);
        } else {
            playlist = Playlist.create(1, name, PlaylistType.PLAYLIST, ownerId);
        }
        playlistRepository.save(playlist.toData());
        return playlist;
    }

//    public List<PlaylistResponse> getPlaylist(UserId ownerId) {
//        List<Tuple> result = playlistClassRepository.findByOwnerId(ownerId);
//        List<PlaylistResponse> dtoList = new ArrayList<>();
//        for (Tuple tuple : result) {
//            PlaylistResponse dto = PlaylistResponse.builder()
//                    .id(tuple.get(playlist.id))
//                    .name(tuple.get(playlist.name))
//                    .orderNumber(tuple.get(playlist.orderNumber))
//                    .type(tuple.get(playlist.type))
//                    .count(tuple.get(Expressions.numberPath(Long.class, "count")))
//                    .build();
//            dtoList.add(dto);
//        }
//        return dtoList;
//    }
//
//    public PlaylistMusicResponse getPlaylistMusic(int page, int pageSize, Long playlistId) {
//        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
//
//        int totalPage = (int) Math.ceil(musicListRepository.countByPlaylistId(playlistId) / pageSize);
//        Page<PlaylistMusic> result = musicListRepository.findByPlaylistIdOrderByOrderNumber(pageable, playlistId);
//        List<PlaylistMusicDto> dtoList = new ArrayList<>();
//        for (PlaylistMusic playlistMusic : result) {
//            PlaylistMusicDto dto = PlaylistMusicDto.builder()
//                    .musicId(playlistMusic.getId())
//                    .orderNumber(playlistMusic.getOrderNumber())
//                    .name(playlistMusic.getName())
//                    .duration(playlistMusic.getDuration())
//                    .thumbnailImage(playlistMusic.getThumbnailImage())
//                    .build();
//            dtoList.add(dto);
//        }
//        PlaylistMusicResponse response = PlaylistMusicResponse.builder()
//                .totalPage(totalPage)
//                .musicList(dtoList)
//                .build();
//        return response;
//    }
//
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
//
//    public PlaylistMusicAddResponse addMusic(Long playlistId, PlaylistMusicAddRequest request) {
//        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
//
//        if (!playlist.isPresent()) {
//            throw new NoSuchElementException("존재하지 않는 플레이리스트");
//        }
//
//        // 곡 추가 중복 여부 및 곡 순서 번호 체크 후 저장 처리
//        List<PlaylistMusic> playlistMusic = musicListRepository.findAllByPlaylistId(playlistId);
//        if (playlistMusic.size() > 100) {
//            throw new PlaylistMusicLimitExceededException("곡 개수 제한 초과");
//        }
//
//        int orderNumber = 1;
//        for (PlaylistMusic music : playlistMusic) {
////            if (music.getUid().equals(request.getUid())) {
////                throw new DuplicateKeyException(""); // Global Exception으로 처리되어 입력한 메시지 전달 X
////            }
//            if (music.getOrderNumber() > orderNumber) {
//                orderNumber = music.getOrderNumber();
//            }
//        }
//
//        int newOrderNumber = playlistMusic.isEmpty() ? orderNumber : orderNumber + 1;
//
//        PlaylistMusic music = PlaylistMusic.builder()
//                .playlist(playlist.get())
//                .uid(request.getUid())
//                .orderNumber(newOrderNumber)
//                .name(request.getName())
//                .duration(request.getDuration())
//                .thumbnailImage(request.getThumbnailImage())
//                .build();
//
//        PlaylistMusic result =  musicListRepository.save(music);
//        PlaylistMusicAddResponse response = PlaylistMusicAddResponse.builder()
//                .playlistId(playlistId)
//                .musicId(result.getId())
//                .orderNumber(result.getOrderNumber())
//                .name(request.getName())
//                .duration(request.getDuration())
//                .build();
//
//        return response;
//    }
//
//    @Transactional
//    public void deletePlaylist(UserId ownerId, List<Long> listIds) {
////        List<Long> ids = playlistClassRepository.findByOwnerIdAndListIdAndType(ownerId, listIds, PlaylistType.PLAYLIST);
////        if (ids.size() != listIds.size()) {
////            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 플레이리스트");
////        }
////        try {
////            musicListClassRepository.deleteByPlaylistIds(ids);
////            Long count = playlistClassRepository.deleteByListIds(ids);
////            if (count != ids.size()) {
////                throw new InvalidDeleteRequestException("비정상적인 삭제 요청");
////            }
////        } catch (Exception e) {
////            if (e instanceof InvalidDeleteRequestException) {
////                throw e;
////            }
////            throw new RuntimeException(e);
////        }
//    }
//
//    @Transactional
//    public void deletePlaylistMusic(UserId ownerId, List<Long> listIds) {
//        // 곡 보유자가 삭제 요청자 Id와 일치하는지 확인
//        // TODO '리소스 쓰기 권한' 여부 확인 절차
////        PlaylistMusic musicList = musicListRepository.findById(listIds.get(0)).orElseThrow(() -> new NoSuchElementException("존재하지 않거나 유효하지 않은 곡"));
////        if (ownerId != musicList.getPlaylist().getMember().getId()) {
////            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 곡");
////        }
////        try {
////            Long playlistId = musicList.getPlaylist().getId();
////            Long count = musicListClassRepository.deleteByIdsAndPlaylistId(listIds, playlistId);
////            if (count != listIds.size()) {
////                throw new InvalidDeleteRequestException("비정상적인 삭제 요청");
////            }
////        } catch (Exception e) {
////            if (e instanceof InvalidDeleteRequestException) {
////                throw e;
////            }
////            throw new RuntimeException(e);
////        }
//    }
//
//    public void renamePlaylist(UserId ownerId, Long playlistId, String name) {
//        Playlist playlist = playlistRepository.findByIdAndOwnerIdAndType(playlistId, ownerId, PlaylistType.PLAYLIST);
//        if(playlist == null) {
//            throw new NoSuchElementException("존재하지 않는 플레이리스트");
//        }
//        playlist.rename(name);
//        playlistRepository.save(playlist);
//    }
}
