package com.pfplaybackend.api.playlist.application.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.domain.service.PlaylistDomainService;
import com.pfplaybackend.api.playlist.exception.PlaylistLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlaylistNoWalletException;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Playlist playlist = new Playlist(0, "그랩한 곡", PlaylistType.GRABLIST, userId);
        playlistRepository.save(playlist.toData());
    }

    @Transactional
    public Playlist createPlaylist(String playlistName) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        UserId userId = playlistContext.getUserId();
        List<PlaylistData> playlistDataList = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(userId, PlaylistType.PLAYLIST);

        // 권한 계층별 '생성 제약' 조건에 대한 위반 여부를 검증한다.
        playlistDomainService.checkWhetherExceedConstraints(playlistContext.getAuthorityTier(), playlistDataList.size());

        int nextOrderNumber = playlistDataList.isEmpty() ? 1 : playlistDataList.size();
        Playlist playlist = Playlist.create(nextOrderNumber, playlistName, PlaylistType.PLAYLIST, userId);
        playlistRepository.save(playlist.toData());
        return playlist;
    }

//    public void renamePlaylist(UserId ownerId, Long playlistId, String name) {
////        Playlist playlist = playlistRepository.findByIdAndOwnerIdAndType(playlistId, ownerId, PlaylistType.PLAYLIST);
////        if(playlist == null) {
////            throw new NoSuchElementException("존재하지 않는 플레이리스트");
////        }
////        playlist.rename(name);
////        playlistRepository.save(playlist);
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
}