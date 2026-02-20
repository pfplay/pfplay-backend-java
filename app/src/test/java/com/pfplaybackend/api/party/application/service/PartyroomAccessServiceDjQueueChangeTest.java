package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.CrewAccessedEvent;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessServiceDjQueueChangeTest {

    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PartyroomRepository partyroomRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private DjRepository djRepository;
    @Mock private PartyroomAggregateService partyroomAggregateService;
    @Mock private PartyroomInfoService partyroomInfoService;
    @Mock private PlaybackManagementService playbackManagementService;

    @InjectMocks
    private PartyroomAccessService partyroomAccessService;

    private UserId userId;
    private PartyroomId partyroomId;

    @BeforeEach
    void setUp() {
        userId = new UserId();
        partyroomId = new PartyroomId(1L);

        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("exit - DJ 대기열에 있던 사용자가 퇴장하면 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void exit_userInDjQueue_shouldPublishDjQueueChangeEvent() {
        // given
        CrewData crew = CrewData.builder()
                .id(1L)
                .userId(userId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        DjData dj = DjData.builder()
                .id(100L)
                .userId(userId)
                .crewId(new CrewId(1L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId)).thenReturn(Optional.of(crew));
        when(djRepository.findByPartyroomDataIdAndCrewId(partyroomId.getId(), new CrewId(1L))).thenReturn(Optional.of(dj));
        when(partyroomAggregateService.isCurrentDj(partyroomId.getId(), new CrewId(1L))).thenReturn(false);

        // when
        partyroomAccessService.exit(partyroomId);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId.getId(), new CrewId(1L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("exit - DJ 대기열에 없던 사용자가 퇴장하면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void exit_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
        CrewData crew = CrewData.builder()
                .id(1L)
                .userId(userId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isPlaybackActivated(false)
                .build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId)).thenReturn(Optional.of(crew));
        when(djRepository.findByPartyroomDataIdAndCrewId(partyroomId.getId(), new CrewId(1L))).thenReturn(Optional.empty());

        // when
        partyroomAccessService.exit(partyroomId);

        // then
        verify(eventPublisher, never()).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("expel - DJ 대기열에 있던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void expel_userInDjQueue_shouldPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        CrewData targetCrew = CrewData.builder()
                .id(2L)
                .userId(targetUserId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        DjData dj = DjData.builder()
                .id(100L)
                .userId(targetUserId)
                .crewId(new CrewId(2L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();

        when(djRepository.findByPartyroomDataIdAndCrewId(partyroomData.getId(), new CrewId(2L))).thenReturn(Optional.of(dj));
        when(partyroomAggregateService.isCurrentDj(partyroomData.getId(), new CrewId(2L))).thenReturn(false);

        // when
        partyroomAccessService.expel(partyroomData, targetCrew, false);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomData.getId(), new CrewId(2L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("expel - DJ 대기열에 없던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void expel_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        CrewData targetCrew = CrewData.builder()
                .id(2L)
                .userId(targetUserId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isPlaybackActivated(false)
                .build();

        when(djRepository.findByPartyroomDataIdAndCrewId(partyroomData.getId(), new CrewId(2L))).thenReturn(Optional.empty());

        // when
        partyroomAccessService.expel(partyroomData, targetCrew, false);

        // then
        verify(eventPublisher, never()).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }
}
