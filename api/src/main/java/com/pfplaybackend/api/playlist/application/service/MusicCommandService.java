package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.exception.TrackException;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddMusicRequest;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicCommandService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository playlistMusicRepository;
    private final PlaylistQueryService playlistQueryService;

    public void addMusicInPlaylist(Long playlistId, AddMusicRequest request) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();

        // TODO 플레이리스트 식별자 유효성 검증은 소유주 여부와 함께 체크
        // throw new NoSuchElementException("존재하지 않는 플레이리스트");

        // TODO 저장 한계치 초과 여부 체크
        // throw new PlaylistMusicLimitExceededException("곡 개수 제한 초과");

        // TODO Optional
        PlaylistSummary PlaylistSummary = playlistQueryService.getPlaylist(playlistId);
        PlaylistData playlistData = playlistRepository.findById(playlistId).orElseThrow();

        // 중복 검사
        Optional<PlaylistMusicData> optional = playlistMusicRepository.findByPlaylistDataIdAndLinkId(playlistData.getId(), request.getLinkId());
        if(optional.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);

        long nextMusicOrderNumber = PlaylistSummary.getMusicCount() == 0 ? 1 : PlaylistSummary.getMusicCount() + 1;

        PlaylistMusicData playlistMusicData = PlaylistMusicData.builder()
                .playlistData(playlistData)
                .name(request.getName())
                .linkId(request.getLinkId())
                .duration(request.getDuration())
                .orderNumber((int) nextMusicOrderNumber)
                .thumbnailImage(request.getThumbnailImage())
                .build();

        playlistMusicRepository.save(playlistMusicData);
    }

    @Transactional
    public void updateMusicMetadataInPlaylist(Long playlistId, AddMusicRequest request) {}

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
