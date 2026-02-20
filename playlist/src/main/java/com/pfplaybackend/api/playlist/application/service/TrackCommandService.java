package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.application.dto.PlaybackTrackDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.domain.exception.TrackException;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.MoveTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.UpdateTrackOrderRequest;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.playlist.domain.port.PlaylistAggregatePort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackCommandService {

    private final PlaylistAggregatePort aggregatePort;
    private final PlaylistQueryPort queryPort;
    private final PlaylistQueryService playlistQueryService;

    @Transactional
    public void addTrackInPlaylist(Long playlistId, AddTrackRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        // 플레이리스트 접근 권한 검사
        PlaylistData playlistData = aggregatePort.findPlaylistByIdAndOwner(playlistId, authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));
        // 트랙 중복 검사
        Optional<TrackData> optional = aggregatePort.findTrackByPlaylistAndLink(playlistData.getId(), request.getLinkId());
        if (optional.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);
        // 최대 보유 한계치 초과 검사
        PlaylistSummary playlistSummary = playlistQueryService.getPlaylist(playlistId);
        if (playlistSummary.musicCount() >= 15) throw ExceptionCreator.create(TrackException.EXCEEDED_TRACK_LIMIT);

        long nextMusicOrderNumber = playlistSummary.musicCount() == 0 ? 1 : playlistSummary.musicCount() + 1;

        TrackData trackData = TrackData.builder()
                .playlistId(playlistData.getId())
                .name(request.getName())
                .linkId(request.getLinkId())
                .duration(Duration.fromString(request.getDuration()))
                .orderNumber((int) nextMusicOrderNumber)
                .thumbnailImage(request.getThumbnailImage())
                .build();

        aggregatePort.saveTrack(trackData);
    }

    @Transactional
    public void updateTrackOrderInPlaylist(Long playlistId, Long trackId, UpdateTrackOrderRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        // 플레이리스트 접근 권한 검사
        aggregatePort.findPlaylistByIdAndOwner(playlistId, authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        // 타겟 트랙 존재 여부 검사
        TrackData trackData = aggregatePort.findTrackByIdAndPlaylist(trackId, playlistId)
                .orElseThrow(() -> ExceptionCreator.create(TrackException.NOT_FOUND_TRACK));

        Integer prevOrderNumber = trackData.getOrderNumber();
        Integer nextOrderNumber = request.getNextOrderNumber();
        // 이동할 순서 값에 대한 유효성 검증
        PlaylistSummary playlistSummary = playlistQueryService.getPlaylist(playlistId);
        if (prevOrderNumber < 1 || nextOrderNumber < 1
                || Objects.equals(prevOrderNumber, nextOrderNumber)
                || playlistSummary.musicCount() < nextOrderNumber)
            throw ExceptionCreator.create(TrackException.INVALID_TRACK_ORDER);

        if (prevOrderNumber < nextOrderNumber) {
            // prevOrderNumber < x <= nextOrderNumber 사이에 있는 Track 레코드의 order_number 를 -1씩 변경
            aggregatePort.shiftUpTrackOrderByDnD(playlistId, prevOrderNumber, nextOrderNumber);
        }
        if (prevOrderNumber > nextOrderNumber) {
            // nextOrderNumber <= x < prevOrderNumber  사이에 있는 Track 레코드의 order_number 를 +1씩 변경
            aggregatePort.shiftDownTrackOrderByDnD(playlistId, prevOrderNumber, nextOrderNumber);
        }

        trackData.reorder(nextOrderNumber);
        aggregatePort.saveTrack(trackData);
    }

    @Transactional
    public void moveTrackToPlaylist(Long sourcePlaylistId, Long trackId, MoveTrackRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        UserId ownerId = authContext.getUserId();
        // 소스 플레이리스트 소유권 검증
        aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, ownerId)
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));
        // 타겟 플레이리스트 소유권 검증
        PlaylistData targetPlaylistData = aggregatePort.findPlaylistByIdAndOwner(request.getTargetPlaylistId(), ownerId)
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));
        // 소스에서 트랙 조회
        TrackData trackData = aggregatePort.findTrackByIdAndPlaylist(trackId, sourcePlaylistId)
                .orElseThrow(() -> ExceptionCreator.create(TrackException.NOT_FOUND_TRACK));
        // 타겟에 동일 linkId 중복 검사
        Optional<TrackData> duplicate = aggregatePort.findTrackByPlaylistAndLink(targetPlaylistData.getId(), trackData.getLinkId());
        if (duplicate.isPresent()) throw ExceptionCreator.create(TrackException.DUPLICATE_TRACK_IN_PLAYLIST);
        // 타겟 트랙 개수 15개 제한 검사
        PlaylistSummary targetSummary = playlistQueryService.getPlaylist(request.getTargetPlaylistId());
        if (targetSummary.musicCount() >= 15) throw ExceptionCreator.create(TrackException.EXCEEDED_TRACK_LIMIT);
        // 소스 orderNumber 재정렬
        aggregatePort.shiftUpTrackOrderByDelete(sourcePlaylistId, trackData.getOrderNumber());
        // 트랙을 타겟 플레이리스트로 이동
        int nextOrderNumber = (int) (targetSummary.musicCount() + 1);
        trackData.moveToPlaylist(targetPlaylistData.getId(), nextOrderNumber);
        aggregatePort.saveTrack(trackData);
    }

    @Transactional
    public void deleteTrackInPlaylist(Long playlistId, Long trackId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        // 플레이리스트 접근 권한 검사
        aggregatePort.findPlaylistByIdAndOwner(playlistId, authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        // 타겟 트랙 존재 여부 검사
        TrackData trackData = aggregatePort.findTrackByIdAndPlaylist(trackId, playlistId)
                .orElseThrow(() -> ExceptionCreator.create(TrackException.NOT_FOUND_TRACK));

        Integer deleteOrderNumber = trackData.getOrderNumber();
        aggregatePort.shiftUpTrackOrderByDelete(playlistId, deleteOrderNumber);
        aggregatePort.deleteTrack(trackData);
    }

    @Transactional
    public PlaybackTrackDto getFirstTrack(Long playlistId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "orderNumber"));
        Page<PlaylistTrackDto> page = queryPort.getTracksWithPagination(playlistId, pageable);
        rotateTrackOrder(playlistId, page.getTotalElements());
        PlaylistTrackDto dto = page.getContent().get(0);
        return new PlaybackTrackDto(
                dto.linkId(),
                dto.name(),
                dto.thumbnailImage(),
                dto.duration(),
                dto.orderNumber()
        );
    }

    public void rotateTrackOrder(Long playlistId, long totalCount) {
        aggregatePort.rotateTrackOrder(playlistId, totalCount);
    }
}
