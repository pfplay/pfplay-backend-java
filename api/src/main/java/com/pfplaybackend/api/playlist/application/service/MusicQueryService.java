package com.pfplaybackend.api.playlist.application.service;

import com.google.api.services.youtube.model.SearchListResponse;
import com.pfplaybackend.api.playlist.application.dto.SearchPlaylistMusicDto;
import com.pfplaybackend.api.playlist.presentation.payload.response.PlaylistMusicResponse;
import com.pfplaybackend.api.playlist.presentation.payload.response.SearchPlaylistMusicResponse;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 뮤직 재생 관련 기능
 */
@Service
public class MusicQueryService {

    public PlaylistMusicResponse getPlaylistMusic() {
        return null;
    }

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
}
