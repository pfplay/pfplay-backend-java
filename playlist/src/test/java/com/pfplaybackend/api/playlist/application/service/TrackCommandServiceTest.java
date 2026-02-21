package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummaryDto;
import com.pfplaybackend.api.playlist.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.application.dto.command.AddTrackCommand;
import com.pfplaybackend.api.playlist.application.dto.command.MoveTrackCommand;
import com.pfplaybackend.api.playlist.application.dto.command.UpdateTrackOrderCommand;
import com.pfplaybackend.api.playlist.domain.port.PlaylistAggregatePort;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackCommandServiceTest {

    @Mock
    private PlaylistAggregatePort aggregatePort;
    @Mock
    private PlaylistQueryPort queryPort;
    @Mock
    private PlaylistQueryService playlistQueryService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TrackCommandService trackCommandService;

    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = new UserId(1L);
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        lenient().when(authContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    // ========== addTrackInPlaylist ==========

    @Test
    @DisplayName("트랙 추가 성공 — save 호출 및 orderNumber 정확")
    void addTrackInPlaylist_success() {
        // given
        Long playlistId = 1L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        AddTrackCommand command = new AddTrackCommand("song", "linkId1", "03:00", "thumb.jpg");

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(playlistId), "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummaryDto(playlistId, "test", 0, PlaylistType.PLAYLIST, 3L));

        // when
        trackCommandService.addTrackInPlaylist(playlistId, command);

        // then
        verify(aggregatePort, times(1)).saveTrack(argThat(track ->
                track.getOrderNumber() == 4 && "linkId1".equals(track.getLinkId())
        ));
    }

    @Test
    @DisplayName("트랙 추가 실패 — 플레이리스트가 존재하지 않으면 NotFoundException")
    void addTrackInPlaylist_playlistNotFound() {
        // given
        Long playlistId = 999L;
        AddTrackCommand command = new AddTrackCommand("song", "linkId1", "03:00", "thumb.jpg");
        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("트랙 추가 실패 — 중복 트랙이면 ConflictException")
    void addTrackInPlaylist_duplicateTrack() {
        // given
        Long playlistId = 1L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        AddTrackCommand command = new AddTrackCommand("song", "linkId1", "03:00", "thumb.jpg");
        TrackData existingTrack = TrackData.builder()
                .playlistId(new PlaylistId(playlistData.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(1).build();

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(playlistId), "linkId1")).thenReturn(Optional.of(existingTrack));

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, command))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("트랙 추가 실패 — 15개 초과 시 ConflictException")
    void addTrackInPlaylist_exceededLimit() {
        // given
        Long playlistId = 1L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        AddTrackCommand command = new AddTrackCommand("song", "linkId1", "03:00", "thumb.jpg");

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(playlistId), "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummaryDto(playlistId, "test", 0, PlaylistType.PLAYLIST, 15L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, command))
                .isInstanceOf(ConflictException.class);
    }

    // ========== deleteTrackInPlaylist ==========

    @Test
    @DisplayName("트랙 삭제 성공 — shiftUpTrackOrderByDelete 및 deleteTrack 호출")
    void deleteTrackInPlaylist_success() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(playlistData.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(3).build();

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(playlistId))).thenReturn(Optional.of(trackData));

        // when
        trackCommandService.deleteTrackInPlaylist(playlistId, trackId);

        // then
        verify(aggregatePort, times(1)).shiftUpTrackOrderByDelete(playlistId, 3);
        verify(aggregatePort, times(1)).deleteTrack(trackData);
    }

    @Test
    @DisplayName("트랙 삭제 실패 — 트랙이 존재하지 않으면 NotFoundException")
    void deleteTrackInPlaylist_trackNotFound() {
        // given
        Long playlistId = 1L;
        Long trackId = 999L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(playlistId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.deleteTrackInPlaylist(playlistId, trackId))
                .isInstanceOf(NotFoundException.class);
    }

    // ========== moveTrackToPlaylist ==========

    @Test
    @DisplayName("트랙 이동 성공 — shiftUpTrackOrderByDelete 호출 및 playlistId·orderNumber 변경")
    void moveTrackToPlaylist_success() {
        // given
        Long sourcePlaylistId = 1L;
        Long targetPlaylistId = 2L;
        Long trackId = 10L;

        PlaylistData sourcePlaylist = PlaylistData.builder()
                .id(sourcePlaylistId).ownerId(userId).name("source").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        PlaylistData targetPlaylist = PlaylistData.builder()
                .id(targetPlaylistId).ownerId(userId).name("target").type(PlaylistType.PLAYLIST).orderNumber(1).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(sourcePlaylist.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        MoveTrackCommand command = new MoveTrackCommand(targetPlaylistId);

        when(aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(aggregatePort.findPlaylistByIdAndOwner(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(sourcePlaylistId))).thenReturn(Optional.of(trackData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(targetPlaylistId), "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(targetPlaylistId))
                .thenReturn(new PlaylistSummaryDto(targetPlaylistId, "target", 1, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, command);

        // then
        verify(aggregatePort, times(1)).shiftUpTrackOrderByDelete(sourcePlaylistId, 2);
        verify(aggregatePort, times(1)).saveTrack(argThat(track ->
                track.getPlaylistId().equals(new PlaylistId(targetPlaylist.getId())) && track.getOrderNumber() == 6
        ));
    }

    @Test
    @DisplayName("트랙 이동 실패 — 소스 플레이리스트가 존재하지 않으면 NotFoundException")
    void moveTrackToPlaylist_sourceNotFound() {
        // given
        Long sourcePlaylistId = 999L;
        Long trackId = 10L;
        MoveTrackCommand command = new MoveTrackCommand(2L);

        when(aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("트랙 이동 실패 — 타겟 플레이리스트가 존재하지 않으면 NotFoundException")
    void moveTrackToPlaylist_targetNotFound() {
        // given
        Long sourcePlaylistId = 1L;
        Long targetPlaylistId = 999L;
        Long trackId = 10L;

        PlaylistData sourcePlaylist = PlaylistData.builder()
                .id(sourcePlaylistId).ownerId(userId).name("source").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        MoveTrackCommand command = new MoveTrackCommand(targetPlaylistId);

        when(aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(aggregatePort.findPlaylistByIdAndOwner(targetPlaylistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("트랙 이동 실패 — 타겟에 중복 트랙이면 ConflictException")
    void moveTrackToPlaylist_duplicateInTarget() {
        // given
        Long sourcePlaylistId = 1L;
        Long targetPlaylistId = 2L;
        Long trackId = 10L;

        PlaylistData sourcePlaylist = PlaylistData.builder()
                .id(sourcePlaylistId).ownerId(userId).name("source").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        PlaylistData targetPlaylist = PlaylistData.builder()
                .id(targetPlaylistId).ownerId(userId).name("target").type(PlaylistType.PLAYLIST).orderNumber(1).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(sourcePlaylist.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();
        TrackData duplicateTrack = TrackData.builder()
                .playlistId(new PlaylistId(targetPlaylist.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(1).build();

        MoveTrackCommand command = new MoveTrackCommand(targetPlaylistId);

        when(aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(aggregatePort.findPlaylistByIdAndOwner(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(sourcePlaylistId))).thenReturn(Optional.of(trackData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(targetPlaylistId), "linkId1")).thenReturn(Optional.of(duplicateTrack));

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, command))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("트랙 이동 실패 — 타겟 15개 초과 시 ConflictException")
    void moveTrackToPlaylist_exceededLimit() {
        // given
        Long sourcePlaylistId = 1L;
        Long targetPlaylistId = 2L;
        Long trackId = 10L;

        PlaylistData sourcePlaylist = PlaylistData.builder()
                .id(sourcePlaylistId).ownerId(userId).name("source").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        PlaylistData targetPlaylist = PlaylistData.builder()
                .id(targetPlaylistId).ownerId(userId).name("target").type(PlaylistType.PLAYLIST).orderNumber(1).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(sourcePlaylist.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        MoveTrackCommand command = new MoveTrackCommand(targetPlaylistId);

        when(aggregatePort.findPlaylistByIdAndOwner(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(aggregatePort.findPlaylistByIdAndOwner(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(sourcePlaylistId))).thenReturn(Optional.of(trackData));
        when(aggregatePort.findTrackByPlaylistAndLink(new PlaylistId(targetPlaylistId), "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(targetPlaylistId))
                .thenReturn(new PlaylistSummaryDto(targetPlaylistId, "target", 1, PlaylistType.PLAYLIST, 15L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, command))
                .isInstanceOf(ConflictException.class);
    }

    // ========== updateTrackOrderInPlaylist ==========

    @Test
    @DisplayName("트랙 순서 변경 성공 — 위에서 아래로 이동 시 shiftUpTrackOrderByDnD 호출")
    void updateTrackOrderInPlaylist_moveDown() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(playlistData.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        UpdateTrackOrderCommand command = new UpdateTrackOrderCommand(4);

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(playlistId))).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummaryDto(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, command);

        // then
        verify(aggregatePort, times(1)).shiftUpTrackOrderByDnD(playlistId, 2, 4);
        verify(aggregatePort, times(1)).saveTrack(argThat(track -> track.getOrderNumber() == 4));
    }

    @Test
    @DisplayName("트랙 순서 변경 성공 — 아래에서 위로 이동 시 shiftDownTrackOrderByDnD 호출")
    void updateTrackOrderInPlaylist_moveUp() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(playlistData.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(4).build();

        UpdateTrackOrderCommand command = new UpdateTrackOrderCommand(2);

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(playlistId))).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummaryDto(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, command);

        // then
        verify(aggregatePort, times(1)).shiftDownTrackOrderByDnD(playlistId, 4, 2);
        verify(aggregatePort, times(1)).saveTrack(argThat(track -> track.getOrderNumber() == 2));
    }

    @Test
    @DisplayName("트랙 순서 변경 실패 — 잘못된 순서 값이면 BadRequestException")
    void updateTrackOrderInPlaylist_invalidOrder() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(new PlaylistId(playlistData.getId())).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        // nextOrderNumber == prevOrderNumber → invalid
        UpdateTrackOrderCommand command = new UpdateTrackOrderCommand(2);

        when(aggregatePort.findPlaylistByIdAndOwner(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(aggregatePort.findTrackByIdAndPlaylist(trackId, new PlaylistId(playlistId))).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummaryDto(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, command))
                .isInstanceOf(BadRequestException.class);
    }
}
