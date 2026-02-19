package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.domain.service.PlaylistDomainService;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.common.domain.value.UserId;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistCommandServiceTest {

    @Mock
    private PlaylistDomainService playlistDomainService;
    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private PlaylistCommandService playlistCommandService;

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

    // ========== createPlaylist ==========

    @Test
    @DisplayName("플레이리스트 생성 성공 — save 호출")
    void createPlaylist_success() {
        // given
        String playlistName = "My Playlist";
        when(playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(userId, PlaylistType.PLAYLIST))
                .thenReturn(Collections.emptyList());
        doNothing().when(playlistDomainService).checkWhetherExceedConstraints(AuthorityTier.FM, 0);

        PlaylistData savedData = PlaylistData.builder()
                .ownerId(userId).name(playlistName).type(PlaylistType.PLAYLIST).orderNumber(1).build();
        savedData.setId(1L);
        when(playlistRepository.save(any(PlaylistData.class))).thenReturn(savedData);

        // when
        PlaylistData result = playlistCommandService.createPlaylist(playlistName);

        // then
        verify(playlistRepository, times(1)).save(any(PlaylistData.class));
        assertThat(result.getName()).isEqualTo(playlistName);
    }

    @Test
    @DisplayName("플레이리스트 생성 실패 — 제약 위반 시 예외 전파")
    void createPlaylist_exceedConstraints() {
        // given
        String playlistName = "My Playlist";
        List<PlaylistData> existingPlaylists = Collections.nCopies(10,
                PlaylistData.builder().ownerId(userId).name("pl").type(PlaylistType.PLAYLIST).orderNumber(0).build());
        when(playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(userId, PlaylistType.PLAYLIST))
                .thenReturn(existingPlaylists);
        doThrow(new ConflictException("PLL-002", "")).when(playlistDomainService)
                .checkWhetherExceedConstraints(AuthorityTier.FM, 10);

        // when & then
        assertThatThrownBy(() -> playlistCommandService.createPlaylist(playlistName))
                .isInstanceOf(ConflictException.class);
    }

    // ========== renamePlaylist ==========

    @Test
    @DisplayName("플레이리스트 이름 변경 성공 — 반환된 이름 검증")
    void renamePlaylist_success() {
        // given
        Long playlistId = 1L;
        String newName = "Renamed Playlist";

        PlaylistData playlistData = PlaylistData.builder()
                .ownerId(userId).name("Old Name").type(PlaylistType.PLAYLIST).orderNumber(0).build();
        playlistData.setId(playlistId);

        when(playlistRepository.findByIdAndOwnerIdAndType(playlistId, userId, PlaylistType.PLAYLIST))
                .thenReturn(Optional.of(playlistData));
        when(playlistRepository.save(any(PlaylistData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PlaylistData result = playlistCommandService.renamePlaylist(playlistId, newName);

        // then
        verify(playlistRepository, times(1)).save(any(PlaylistData.class));
        assertThat(result.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("플레이리스트 이름 변경 실패 — 미존재 시 NotFoundException")
    void renamePlaylist_notFound() {
        // given
        Long playlistId = 999L;
        when(playlistRepository.findByIdAndOwnerIdAndType(playlistId, userId, PlaylistType.PLAYLIST))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> playlistCommandService.renamePlaylist(playlistId, "New Name"))
                .isInstanceOf(NotFoundException.class);
    }

    // ========== deletePlaylist ==========

    @Test
    @DisplayName("플레이리스트 삭제 성공 — deleteByListIds 호출")
    void deletePlaylist_success() {
        // given
        List<Long> playlistIds = List.of(1L, 2L);

        PlaylistData pd1 = PlaylistData.builder().ownerId(userId).name("p1").type(PlaylistType.PLAYLIST).orderNumber(0).build();
        pd1.setId(1L);
        PlaylistData pd2 = PlaylistData.builder().ownerId(userId).name("p2").type(PlaylistType.PLAYLIST).orderNumber(1).build();
        pd2.setId(2L);

        when(playlistRepository.findAllByOwnerId(userId)).thenReturn(List.of(pd1, pd2));
        when(playlistRepository.deleteByListIds(playlistIds)).thenReturn(2L);

        // when
        playlistCommandService.deletePlaylist(playlistIds);

        // then
        verify(playlistRepository, times(1)).deleteByListIds(playlistIds);
    }

    @Test
    @DisplayName("플레이리스트 삭제 실패 — 소유하지 않은 ID 포함 시 NotFoundException")
    void deletePlaylist_notOwned() {
        // given
        List<Long> playlistIds = List.of(1L, 999L);

        PlaylistData pd1 = PlaylistData.builder().ownerId(userId).name("p1").type(PlaylistType.PLAYLIST).orderNumber(0).build();
        pd1.setId(1L);

        when(playlistRepository.findAllByOwnerId(userId)).thenReturn(List.of(pd1));

        // when & then
        assertThatThrownBy(() -> playlistCommandService.deletePlaylist(playlistIds))
                .isInstanceOf(NotFoundException.class);
    }
}
