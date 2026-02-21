package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.playlist.application.dto.command.AddTrackCommand;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.domain.port.PlaylistAggregatePort;
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

    @Mock PlaylistAggregatePort aggregatePort;
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

        when(aggregatePort.findFirstTrackByLink(linkId)).thenReturn(track);
        when(aggregatePort.findPlaylistByOwnerAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(aggregatePort.findTrackByPlaylistAndLink(grablist.getId(), linkId)).thenReturn(Optional.empty());

        // when
        grabTrackService.grabTrack(userId, linkId);

        // then
        verify(trackCommandService).addTrackInPlaylist(eq(grablist.getId()), any(AddTrackCommand.class));
    }

    @Test
    @DisplayName("grabTrack — 존재하지 않는 트랙으로 그랩 시 NotFoundException이 발생한다")
    void grabTrack_trackNotFound() {
        // given
        when(aggregatePort.findFirstTrackByLink(linkId)).thenReturn(null);

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

        when(aggregatePort.findFirstTrackByLink(linkId)).thenReturn(track);
        when(aggregatePort.findPlaylistByOwnerAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(aggregatePort.findTrackByPlaylistAndLink(grablist.getId(), linkId))
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

        when(aggregatePort.findFirstTrackByLink(linkId)).thenReturn(track);
        when(aggregatePort.findPlaylistByOwnerAndType(userId, PlaylistType.GRABLIST)).thenReturn(grablist);
        when(aggregatePort.findTrackByPlaylistAndLink(grablist.getId(), linkId)).thenReturn(Optional.empty());

        ArgumentCaptor<AddTrackCommand> captor = ArgumentCaptor.forClass(AddTrackCommand.class);

        // when
        grabTrackService.grabTrack(userId, linkId);

        // then
        verify(trackCommandService).addTrackInPlaylist(eq(grablist.getId()), captor.capture());
        AddTrackCommand command = captor.getValue();
        assertThat(command.name()).isEqualTo("Test Song");
        assertThat(command.linkId()).isEqualTo(linkId);
        assertThat(command.thumbnailImage()).isEqualTo("https://img.example.com/thumb.jpg");
    }
}
