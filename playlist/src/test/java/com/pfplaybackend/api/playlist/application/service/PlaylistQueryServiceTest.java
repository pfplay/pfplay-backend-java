package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistQueryServiceTest {

    @Mock PlaylistQueryPort queryPort;
    @InjectMocks PlaylistQueryService playlistQueryService;

    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = new UserId(1L);
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("getPlaylists — 사용자의 전체 플레이리스트 목록을 반환한다")
    void getPlaylists_success() {
        // given
        List<PlaylistSummary> expected = List.of(
                new PlaylistSummary(1L, "My Playlist", 1, PlaylistType.PLAYLIST, 5L),
                new PlaylistSummary(2L, "Grab", 0, PlaylistType.GRABLIST, 3L)
        );
        when(queryPort.findAllByUserId(userId)).thenReturn(expected);

        // when
        List<PlaylistSummary> result = playlistQueryService.getPlaylists();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
        verify(queryPort).findAllByUserId(userId);
    }

    @Test
    @DisplayName("getPlaylists — 플레이리스트가 없으면 빈 목록을 반환한다")
    void getPlaylists_empty() {
        // given
        when(queryPort.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // when
        List<PlaylistSummary> result = playlistQueryService.getPlaylists();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPlaylist — 특정 플레이리스트를 ID로 조회한다")
    void getPlaylist_success() {
        // given
        Long playlistId = 1L;
        PlaylistSummary expected = new PlaylistSummary(playlistId, "My Playlist", 0, PlaylistType.PLAYLIST, 10L);
        when(queryPort.findByIdAndUserId(playlistId, userId)).thenReturn(expected);

        // when
        PlaylistSummary result = playlistQueryService.getPlaylist(playlistId);

        // then
        assertThat(result).isEqualTo(expected);
        verify(queryPort).findByIdAndUserId(playlistId, userId);
    }
}
