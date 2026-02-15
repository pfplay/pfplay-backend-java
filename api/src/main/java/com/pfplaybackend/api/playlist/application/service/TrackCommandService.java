package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.domain.exception.TrackException;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.presentation.payload.request.UpdateTrackOrderRequest;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.playlist.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackCommandService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistQueryService playlistQueryService;

    @Transactional
    public void addTrackInPlaylist(Long playlistId, AddTrackRequest request) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        // 플레이리스트 접근 권한 검사
        PlaylistData playlistData = playlistRepository.findByIdAndOwnerId(playlistId, playlistContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));
        // 트랙 중복 검사
        Optional<TrackData> optional = trackRepository.findByPlaylistDataIdAndLinkId(playlistData.getId(), request.getLinkId());
        if (optional.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);
        // 최대 보유 한계치 초과 검사
        PlaylistSummary playlistSummary = playlistQueryService.getPlaylist(playlistId);
        if (playlistSummary.getMusicCount() >= 15) throw ExceptionCreator.create(TrackException.EXCEEDED_TRACK_LIMIT);

        long nextMusicOrderNumber = playlistSummary.getMusicCount() == 0 ? 1 : playlistSummary.getMusicCount() + 1;

        TrackData trackData = TrackData.builder()
                .playlistData(playlistData)
                .name(request.getName())
                .linkId(request.getLinkId())
                .duration(request.getDuration())
                .orderNumber((int) nextMusicOrderNumber)
                .thumbnailImage(request.getThumbnailImage())
                .build();

        trackRepository.save(trackData);
    }

    @Transactional
    public void updateTrackOrderInPlaylist(Long playlistId, Long trackId, UpdateTrackOrderRequest request) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        // 플레이리스트 접근 권한 검사
        playlistRepository.findByIdAndOwnerId(playlistId, playlistContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        // 타겟 트랙 존재 여부 검사
        TrackData trackData = trackRepository.findByIdAndPlaylistDataId(trackId, playlistId)
                .orElseThrow(() -> ExceptionCreator.create(TrackException.NOT_FOUND_TRACK));

        Integer prevOrderNumber = trackData.getOrderNumber();
        Integer nextOrderNumber = request.getNextOrderNumber();
        // 이동할 순서 값에 대한 유효성 검증
        PlaylistSummary playlistSummary = playlistQueryService.getPlaylist(playlistId);
        if (prevOrderNumber < 1 || nextOrderNumber < 1
                || Objects.equals(prevOrderNumber, nextOrderNumber)
                || playlistSummary.getMusicCount() < nextOrderNumber)
            throw ExceptionCreator.create(TrackException.INVALID_TRACK_ORDER);

        if (prevOrderNumber < nextOrderNumber) {
            // prevOrderNumber < x <= nextOrderNumber 사이에 있는 Track 레코드의 order_number 를 -1씩 변경
            trackRepository.shiftUpOrderByDnD(playlistId, prevOrderNumber, nextOrderNumber);
        }
        if (prevOrderNumber > nextOrderNumber) {
            // nextOrderNumber <= x < prevOrderNumber  사이에 있는 Track 레코드의 order_number 를 +1씩 변경
            trackRepository.shiftDownOrderByDnD(playlistId, prevOrderNumber, nextOrderNumber);
        }

        trackData.setOrderNumber(nextOrderNumber);
        trackRepository.save(trackData);
    }

    @Transactional
    public void deleteTrackInPlaylist(Long playlistId, Long trackId) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        // 플레이리스트 접근 권한 검사
        playlistRepository.findByIdAndOwnerId(playlistId, playlistContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        // 타겟 트랙 존재 여부 검사
        TrackData trackData = trackRepository.findByIdAndPlaylistDataId(trackId, playlistId)
                .orElseThrow(() -> ExceptionCreator.create(TrackException.NOT_FOUND_TRACK));

        Integer deleteOrderNumber = trackData.getOrderNumber();
        trackRepository.shiftUpOrderByDelete(playlistId, deleteOrderNumber);
        trackRepository.delete(trackData);
    }
}
