package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 플레이리스트 CRUD
 */
@Service
@RequiredArgsConstructor
public class PlaylistManagementService {

    private final PlaylistRepository playlistRepository;

    /**
     * 멤버 생성 시 기본적으로 생성되는 grab 용도의 '플레이리스트 레코드'를 생성키 위함이다.
     */
    @Transactional
    public void createDefaultPlaylist(UserId userId) {
        Playlist playlist = new Playlist(0, "그랩한 곡", PlaylistType.GRABLIST, userId);
        playlistRepository.save(playlist.toData());
    }
}