package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.playlist.adapter.in.web.payload.request.AddTrackRequest;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrabTrackServiceTest {

    @Mock PlaylistRepository playlistRepository;
    @Mock TrackRepository trackRepository;
    @Mock TrackCommandService trackCommandService;

    @InjectMocks GrabTrackService grabTrackService;

    private final UserId userId = new UserId(1L);
    private final String linkId = "abc123";

    private TrackData createTrackData() {
        return TrackData.builder()
                .name("Test Song")
                .linkId(linkId)
                .duration(Duration.fromString("3:30"))
                .thumbnailImage("https://img.example.com/thumb.jpg")
                .build();
    }

    private PlaylistData createGrablist() {
        return PlaylistData.builder()
                .id(100L).ownerId(userId).orderNumber(0).name("Grab").type(PlaylistType.GRABLIST).build();
    }

    @Test
    @DisplayName("grabTrack — 정상 그랩 시 트랙이 GRABLIST에 추가된다")
    void grabTrack_success() {
        // given
        TrackData track = createTrackData();
        PlaylistData grablist = createGrablist();

        when(trackRepository.findFirstByLinkId(linkId)).thenReturn(track);
        when(playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(trackRepository.findByPlaylistIdAndLinkId(grablist.getId(), linkId)).thenReturn(Optional.empty());

        // when
        grabTrackService.grabTrack(userId, linkId);

        // then
        verify(trackCommandService).addTrackInPlaylist(eq(grablist.getId()), any(AddTrackRequest.class));
    }

    @Test
    @DisplayName("grabTrack — 존재하지 않는 트랙으로 그랩 시 NotFoundException이 발생한다")
    void grabTrack_trackNotFound() {
        // given
        when(trackRepository.findFirstByLinkId(linkId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> grabTrackService.grabTrack(userId, linkId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("grabTrack — GRABLIST에 이미 동일한 linkId가 있으면 ConflictException이 발생한다")
    void grabTrack_duplicateTrack() {
        // given
        TrackData track = createTrackData();
        PlaylistData grablist = createGrablist();

        when(trackRepository.findFirstByLinkId(linkId)).thenReturn(track);
        when(playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(trackRepository.findByPlaylistIdAndLinkId(grablist.getId(), linkId))
                .thenReturn(Optional.of(track));

        // when & then
        assertThatThrownBy(() -> grabTrackService.grabTrack(userId, linkId))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("grabTrack — 정상 그랩 시 원본 트랙의 메타데이터가 복사된다")
    void grabTrack_copiesMetadata() {
        // given
        TrackData track = createTrackData();
        PlaylistData grablist = createGrablist();

        when(trackRepository.findFirstByLinkId(linkId)).thenReturn(track);
        when(playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(trackRepository.findByPlaylistIdAndLinkId(grablist.getId(), linkId)).thenReturn(Optional.empty());

        ArgumentCaptor<AddTrackRequest> captor = ArgumentCaptor.forClass(AddTrackRequest.class);

        // when
        grabTrackService.grabTrack(userId, linkId);

        // then
        verify(trackCommandService).addTrackInPlaylist(eq(grablist.getId()), captor.capture());
        AddTrackRequest request = captor.getValue();
        assertThat(request.getName()).isEqualTo("Test Song");
        assertThat(request.getLinkId()).isEqualTo(linkId);
        assertThat(request.getThumbnailImage()).isEqualTo("https://img.example.com/thumb.jpg");
    }
}
