package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.MoveTrackRequest;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.UpdateTrackOrderRequest;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackCommandServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private PlaylistQueryService playlistQueryService;

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

        AddTrackRequest request = new AddTrackRequest("song", "linkId1", "03:00", "thumb.jpg");

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByPlaylistIdAndLinkId(playlistId, "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummary(playlistId, "test", 0, PlaylistType.PLAYLIST, 3L));

        // when
        trackCommandService.addTrackInPlaylist(playlistId, request);

        // then
        verify(trackRepository, times(1)).save(argThat(track ->
                track.getOrderNumber() == 4 && "linkId1".equals(track.getLinkId())
        ));
    }

    @Test
    @DisplayName("트랙 추가 실패 — 플레이리스트가 존재하지 않으면 NotFoundException")
    void addTrackInPlaylist_playlistNotFound() {
        // given
        Long playlistId = 999L;
        AddTrackRequest request = new AddTrackRequest("song", "linkId1", "03:00", "thumb.jpg");
        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("트랙 추가 실패 — 중복 트랙이면 ConflictException")
    void addTrackInPlaylist_duplicateTrack() {
        // given
        Long playlistId = 1L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        AddTrackRequest request = new AddTrackRequest("song", "linkId1", "03:00", "thumb.jpg");
        TrackData existingTrack = TrackData.builder()
                .playlistId(playlistData.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(1).build();

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByPlaylistIdAndLinkId(playlistId, "linkId1")).thenReturn(Optional.of(existingTrack));

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("트랙 추가 실패 — 15개 초과 시 ConflictException")
    void addTrackInPlaylist_exceededLimit() {
        // given
        Long playlistId = 1L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        AddTrackRequest request = new AddTrackRequest("song", "linkId1", "03:00", "thumb.jpg");

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByPlaylistIdAndLinkId(playlistId, "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummary(playlistId, "test", 0, PlaylistType.PLAYLIST, 15L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.addTrackInPlaylist(playlistId, request))
                .isInstanceOf(ConflictException.class);
    }

    // ========== deleteTrackInPlaylist ==========

    @Test
    @DisplayName("트랙 삭제 성공 — shiftUpOrderByDelete 및 delete 호출")
    void deleteTrackInPlaylist_success() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(playlistData.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(3).build();

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByIdAndPlaylistId(trackId, playlistId)).thenReturn(Optional.of(trackData));

        // when
        trackCommandService.deleteTrackInPlaylist(playlistId, trackId);

        // then
        verify(trackRepository, times(1)).shiftUpOrderByDelete(playlistId, 3);
        verify(trackRepository, times(1)).delete(trackData);
    }

    @Test
    @DisplayName("트랙 삭제 실패 — 트랙이 존재하지 않으면 NotFoundException")
    void deleteTrackInPlaylist_trackNotFound() {
        // given
        Long playlistId = 1L;
        Long trackId = 999L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByIdAndPlaylistId(trackId, playlistId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.deleteTrackInPlaylist(playlistId, trackId))
                .isInstanceOf(NotFoundException.class);
    }

    // ========== moveTrackToPlaylist ==========

    @Test
    @DisplayName("트랙 이동 성공 — shiftUpOrderByDelete 호출 및 playlistData·orderNumber 변경")
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
                .playlistId(sourcePlaylist.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        MoveTrackRequest request = new MoveTrackRequest(targetPlaylistId);

        when(playlistRepository.findByIdAndOwnerId(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(playlistRepository.findByIdAndOwnerId(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(trackRepository.findByIdAndPlaylistId(trackId, sourcePlaylistId)).thenReturn(Optional.of(trackData));
        when(trackRepository.findByPlaylistIdAndLinkId(targetPlaylistId, "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(targetPlaylistId))
                .thenReturn(new PlaylistSummary(targetPlaylistId, "target", 1, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, request);

        // then
        verify(trackRepository, times(1)).shiftUpOrderByDelete(sourcePlaylistId, 2);
        verify(trackRepository, times(1)).save(argThat(track ->
                track.getPlaylistId().equals(targetPlaylist.getId()) && track.getOrderNumber() == 6
        ));
    }

    @Test
    @DisplayName("트랙 이동 실패 — 소스 플레이리스트가 존재하지 않으면 NotFoundException")
    void moveTrackToPlaylist_sourceNotFound() {
        // given
        Long sourcePlaylistId = 999L;
        Long trackId = 10L;
        MoveTrackRequest request = new MoveTrackRequest(2L);

        when(playlistRepository.findByIdAndOwnerId(sourcePlaylistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, request))
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

        MoveTrackRequest request = new MoveTrackRequest(targetPlaylistId);

        when(playlistRepository.findByIdAndOwnerId(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(playlistRepository.findByIdAndOwnerId(targetPlaylistId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, request))
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
                .playlistId(sourcePlaylist.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();
        TrackData duplicateTrack = TrackData.builder()
                .playlistId(targetPlaylist.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(1).build();

        MoveTrackRequest request = new MoveTrackRequest(targetPlaylistId);

        when(playlistRepository.findByIdAndOwnerId(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(playlistRepository.findByIdAndOwnerId(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(trackRepository.findByIdAndPlaylistId(trackId, sourcePlaylistId)).thenReturn(Optional.of(trackData));
        when(trackRepository.findByPlaylistIdAndLinkId(targetPlaylistId, "linkId1")).thenReturn(Optional.of(duplicateTrack));

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, request))
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
                .playlistId(sourcePlaylist.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        MoveTrackRequest request = new MoveTrackRequest(targetPlaylistId);

        when(playlistRepository.findByIdAndOwnerId(sourcePlaylistId, userId)).thenReturn(Optional.of(sourcePlaylist));
        when(playlistRepository.findByIdAndOwnerId(targetPlaylistId, userId)).thenReturn(Optional.of(targetPlaylist));
        when(trackRepository.findByIdAndPlaylistId(trackId, sourcePlaylistId)).thenReturn(Optional.of(trackData));
        when(trackRepository.findByPlaylistIdAndLinkId(targetPlaylistId, "linkId1")).thenReturn(Optional.empty());
        when(playlistQueryService.getPlaylist(targetPlaylistId))
                .thenReturn(new PlaylistSummary(targetPlaylistId, "target", 1, PlaylistType.PLAYLIST, 15L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.moveTrackToPlaylist(sourcePlaylistId, trackId, request))
                .isInstanceOf(ConflictException.class);
    }

    // ========== updateTrackOrderInPlaylist ==========

    @Test
    @DisplayName("트랙 순서 변경 성공 — 위에서 아래로 이동 시 shiftUpOrderByDnD 호출")
    void updateTrackOrderInPlaylist_moveDown() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(playlistData.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        UpdateTrackOrderRequest request = new UpdateTrackOrderRequest(4);

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByIdAndPlaylistId(trackId, playlistId)).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummary(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, request);

        // then
        verify(trackRepository, times(1)).shiftUpOrderByDnD(playlistId, 2, 4);
        verify(trackRepository, times(1)).save(argThat(track -> track.getOrderNumber() == 4));
    }

    @Test
    @DisplayName("트랙 순서 변경 성공 — 아래에서 위로 이동 시 shiftDownOrderByDnD 호출")
    void updateTrackOrderInPlaylist_moveUp() {
        // given
        Long playlistId = 1L;
        Long trackId = 10L;
        PlaylistData playlistData = PlaylistData.builder()
                .id(playlistId).ownerId(userId).name("test").type(PlaylistType.PLAYLIST).orderNumber(0).build();

        TrackData trackData = TrackData.builder()
                .playlistId(playlistData.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(4).build();

        UpdateTrackOrderRequest request = new UpdateTrackOrderRequest(2);

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByIdAndPlaylistId(trackId, playlistId)).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummary(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when
        trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, request);

        // then
        verify(trackRepository, times(1)).shiftDownOrderByDnD(playlistId, 4, 2);
        verify(trackRepository, times(1)).save(argThat(track -> track.getOrderNumber() == 2));
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
                .playlistId(playlistData.getId()).name("song").linkId("linkId1").duration(Duration.fromString("03:00")).orderNumber(2).build();

        // nextOrderNumber == prevOrderNumber → invalid
        UpdateTrackOrderRequest request = new UpdateTrackOrderRequest(2);

        when(playlistRepository.findByIdAndOwnerId(playlistId, userId)).thenReturn(Optional.of(playlistData));
        when(trackRepository.findByIdAndPlaylistId(trackId, playlistId)).thenReturn(Optional.of(trackData));
        when(playlistQueryService.getPlaylist(playlistId))
                .thenReturn(new PlaylistSummary(playlistId, "test", 0, PlaylistType.PLAYLIST, 5L));

        // when & then
        assertThatThrownBy(() -> trackCommandService.updateTrackOrderInPlaylist(playlistId, trackId, request))
                .isInstanceOf(BadRequestException.class);
    }
}
