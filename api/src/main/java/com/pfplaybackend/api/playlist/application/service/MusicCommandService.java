package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.playlist.presentation.payload.request.PlaylistMusicAddRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.PlaylistMusicAddResponse;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicCommandService {

    public PlaylistMusicAddResponse addMusicInPlaylist(Long playlistId, PlaylistMusicAddRequest request) {
        return null;
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
    }

    @Transactional
    public void updateMusicMetadataInPlaylist(Long playlistId, PlaylistMusicAddRequest request) {}

    @Transactional
    public void deleteMusicFromPlaylist() {}

    @Transactional
    public void deletePlaylistMusic(UserId ownerId, List<Long> listIds) {
        // 곡 보유자가 삭제 요청자 Id와 일치하는지 확인
        // TODO '리소스 쓰기 권한' 여부 확인 절차
//        PlaylistMusic musicList = musicListRepository.findById(listIds.get(0)).orElseThrow(() -> new NoSuchElementException("존재하지 않거나 유효하지 않은 곡"));
//        if (ownerId != musicList.getPlaylist().getMember().getId()) {
//            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 곡");
//        }
//        try {
//            Long playlistId = musicList.getPlaylist().getId();
//            Long count = musicListClassRepository.deleteByIdsAndPlaylistId(listIds, playlistId);
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
}
