package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.value.*;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DjManagementServiceDjQueueChangeTest {

    @Mock private PartyroomRepository partyroomRepository;
    @Mock private DjRepository djRepository;
    @Mock private PlaybackManagementService playbackManagementService;
    @Mock private PlaylistQueryPort playlistQueryPort;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PartyroomAggregateService partyroomAggregateService;
    @Mock private PartyroomInfoService partyroomInfoService;

    @InjectMocks
    private DjManagementService djManagementService;

    private UserId userId;
    private PartyroomId partyroomId;

    @BeforeEach
    void setUp() {
        userId = new UserId();
        partyroomId = new PartyroomId(1L);

        AuthContext authContext = mock(AuthContext.class);
        when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("enqueueDj - DJ 등록 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void enqueueDj_shouldPublishDjQueueChangeEvent() {
        // given
        PlaylistId playlistId = new PlaylistId(10L);

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
                .isPlaybackActivated(true)
                .isQueueClosed(false)
                .build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(playlistQueryPort.isEmptyPlaylist(playlistId.getId())).thenReturn(false);
        when(djRepository.existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(partyroomId.getId(), userId)).thenReturn(false);
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), userId)).thenReturn(crew);
        when(djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId.getId())).thenReturn(Collections.emptyList());
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);

        // when
        djManagementService.enqueueDj(partyroomId, playlistId);

        // then
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
    }

    @Test
    @DisplayName("dequeueDj(자진) - 대기 DJ 삭제 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void dequeueDjSelf_shouldPublishDjQueueChangeEvent() {
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
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), userId)).thenReturn(crew);
        when(partyroomAggregateService.isCurrentDj(partyroomId.getId(), new CrewId(1L))).thenReturn(false);

        // when
        djManagementService.dequeueDj(partyroomId);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId.getId(), new CrewId(1L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        // 대기 DJ(orderNumber != 1)이므로 skipBySystem은 호출되지 않아야 한다
        verify(playbackManagementService, never()).skipBySystem(any());
    }

    @Test
    @DisplayName("dequeueDj(자진) - 현재 DJ 삭제 시 DJ_QUEUE_CHANGE 이벤트 발행 후 skipBySystem 호출")
    void dequeueDjSelf_currentDj_shouldPublishEventThenSkip() {
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
                .orderNumber(1)
                .isQueued(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), userId)).thenReturn(crew);
        when(partyroomAggregateService.isCurrentDj(partyroomId.getId(), new CrewId(1L))).thenReturn(true);

        // when
        djManagementService.dequeueDj(partyroomId);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId.getId(), new CrewId(1L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        verify(playbackManagementService).skipBySystem(partyroomId);
    }

    @Test
    @DisplayName("dequeueDj(관리자) - DJ 삭제 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void dequeueDjByAdmin_shouldPublishDjQueueChangeEvent() {
        // given — setUp()에서 설정된 userId를 관리자로 사용
        UserId adminUserId = userId;

        UserId targetUserId = new UserId();
        DjId djId = new DjId(100L);

        DjData targetDj = DjData.builder()
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

        CrewData adjusterCrew = CrewData.builder()
                .id(1L)
                .userId(adminUserId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.MODERATOR)
                .isActive(true)
                .build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), adminUserId)).thenReturn(adjusterCrew);
        when(djRepository.findById(djId.getId())).thenReturn(Optional.of(targetDj));
        when(partyroomAggregateService.isCurrentDj(partyroomId.getId(), new CrewId(2L))).thenReturn(false);

        // when
        djManagementService.dequeueDj(partyroomId, djId);

        // then
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
    }
}
