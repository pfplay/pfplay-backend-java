package com.pfplaybackend.api.playlist.application.service;

import com.google.api.services.youtube.model.SearchListResponse;
import com.pfplaybackend.api.config.external.YoutubeService;
import com.pfplaybackend.api.playlist.application.dto.SearchPlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.exception.PlaylistMusicLimitExceededException;
import com.pfplaybackend.api.playlist.presentation.payload.request.PlaylistMusicAddRequest;
import com.pfplaybackend.api.playlist.presentation.payload.response.PlaylistMusicAddResponse;
import com.pfplaybackend.api.playlist.presentation.payload.response.SearchPlaylistMusicResponse;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaylistCommandService {

    private final PlaylistRepository playlistRepository;

    /**
     * 멤버 생성 시 기본적으로 생성되는 grab 용도의 '플레이리스트 레코드'를 생성키 위함이다.
     */
    @Transactional
    public void createDefaultPlaylist(UserId userId) {
        Playlist playlist = new Playlist(0, "그랩한 곡", PlaylistType.GRABLIST, userId);
        playlistRepository.save(playlist.toData());
    }

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

    public void renamePlaylist(UserId ownerId, Long playlistId, String name) {
//        Playlist playlist = playlistRepository.findByIdAndOwnerIdAndType(playlistId, ownerId, PlaylistType.PLAYLIST);
//        if(playlist == null) {
//            throw new NoSuchElementException("존재하지 않는 플레이리스트");
//        }
//        playlist.rename(name);
//        playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(UserId ownerId, List<Long> listIds) {
//        List<Long> ids = playlistClassRepository.findByOwnerIdAndListIdAndType(ownerId, listIds, PlaylistType.PLAYLIST);
//        if (ids.size() != listIds.size()) {
//            throw new NoSuchElementException("존재하지 않거나 유효하지 않은 플레이리스트");
//        }
//        try {
//            musicListClassRepository.deleteByPlaylistIds(ids);
//            Long count = playlistClassRepository.deleteByListIds(ids);
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
}
