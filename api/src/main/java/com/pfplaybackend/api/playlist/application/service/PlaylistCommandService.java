package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.domain.service.PlaylistDomainService;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistCommandService {

    private final PlaylistDomainService playlistDomainService;
    private final PlaylistRepository playlistRepository;

    /**
     * 멤버 생성 시 기본적으로 생성되는 grab 용도의 '플레이리스트 레코드'를 생성키 위함이다.
     */
    @Transactional
    public void createDefaultPlaylist(UserId userId) {
        PlaylistData playlist = PlaylistData.create(0, "그랩한 곡", PlaylistType.GRABLIST, userId);
        playlistRepository.save(playlist);
    }

    @Transactional
    public PlaylistData createPlaylist(String playlistName) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        UserId userId = authContext.getUserId();
        List<PlaylistData> playlistDataList = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(userId, PlaylistType.PLAYLIST);

        // 권한 계층별 '생성 제약' 조건에 대한 위반 여부를 검증한다.
        playlistDomainService.checkWhetherExceedConstraints(authContext.getAuthorityTier(), playlistDataList.size());

        // Save
        int nextOrderNumber = playlistDataList.isEmpty() ? 1 : playlistDataList.size();
        PlaylistData playlist = PlaylistData.create(nextOrderNumber, playlistName, PlaylistType.PLAYLIST, userId);
        return playlistRepository.save(playlist);
    }

    @Transactional
    public PlaylistData renamePlaylist(Long playlistId, String name) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        UserId userId = authContext.getUserId();

        PlaylistData playlistData = playlistRepository.findByIdAndOwnerIdAndType(playlistId, userId, PlaylistType.PLAYLIST)
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        playlistData.rename(name);
        playlistRepository.save(playlistData);
        return playlistData;
    }

    @Transactional
    public void deletePlaylist(List<Long> playlistIds) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        UserId userId = authContext.getUserId();
        List<PlaylistData> playlistDataList = playlistRepository.findAllByOwnerId(userId);

        Set<Long> userPlaylistIdSet = playlistDataList.stream().map(PlaylistData::getId).collect(Collectors.toSet());
        if (!userPlaylistIdSet.containsAll(playlistIds)) {
            throw ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST);
        }

        Long count = playlistRepository.deleteByListIds(playlistIds);
        if (count != playlistIds.size()) {
            throw ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST);
        }
    }
}
