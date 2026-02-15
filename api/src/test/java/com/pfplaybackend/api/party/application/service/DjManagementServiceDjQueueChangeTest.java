package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DjManagementServiceDjQueueChangeTest {

    @Mock private PartyroomRepository partyroomRepository;
    @Mock private PartyroomConverter partyroomConverter;
    @Mock private PartyroomDomainService partyroomDomainService;
    @Mock private CrewDomainService crewDomainService;
    @Mock private PartyroomInfoService partyroomInfoService;
    @Mock private PlaybackManagementService playbackManagementService;
    @Mock private MusicQueryPeerService musicQueryService;
    @Mock private RedisMessagePublisher messagePublisher;

    @InjectMocks
    private DjManagementService djManagementService;

    private UserId userId;
    private PartyroomId partyroomId;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        partyroomId = new PartyroomId(1L);

        PartyContext partyContext = mock(PartyContext.class);
        when(partyContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(partyContext);
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

        Crew crew = Crew.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .isQueueClosed(false)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(new HashSet<>());

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);
        PartyroomData savedPartyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(musicQueryService.isEmptyPlaylist(playlistId.getId())).thenReturn(false);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(savedPartyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(savedPartyroomData);
        when(partyroomConverter.toDomain(savedPartyroomData)).thenReturn(partyroom);
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(List.of(
                new DjWithProfileDto(1L, 1, "nick1", "icon1")
        ));

        // when
        djManagementService.enqueueDj(partyroomId, playlistId);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
    }

    @Test
    @DisplayName("dequeueDj(자진) - 대기 DJ 삭제 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void dequeueDjSelf_shouldPublishDjQueueChangeEvent() {
        // given
        Crew crew = Crew.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Dj dj = Dj.builder()
                .id(100L)
                .partyroomId(partyroomId)
                .userId(userId)
                .crewId(new CrewId(1L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew);
        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(djSet);

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(Collections.emptyList());

        // when
        djManagementService.dequeueDj(partyroomId);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        // 대기 DJ(orderNumber != 1)이므로 skipBySystem은 호출되지 않아야 한다
        verify(playbackManagementService, never()).skipBySystem(any());
    }

    @Test
    @DisplayName("dequeueDj(자진) - 현재 DJ 삭제 시 DJ_QUEUE_CHANGE 이벤트 발행 후 skipBySystem 호출")
    void dequeueDjSelf_currentDj_shouldPublishEventThenSkip() {
        // given
        Crew crew = Crew.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Dj dj = Dj.builder()
                .id(100L)
                .partyroomId(partyroomId)
                .userId(userId)
                .crewId(new CrewId(1L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(1)
                .isQueued(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew);
        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(djSet);

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(Collections.emptyList());

        // when
        djManagementService.dequeueDj(partyroomId);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(playbackManagementService).skipBySystem(partyroomId);
    }

    @Test
    @DisplayName("dequeueDj(관리자) - DJ 삭제 후 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void dequeueDjByAdmin_shouldPublishDjQueueChangeEvent() {
        // given — setUp()에서 설정된 userId를 관리자로 사용
        UserId adminUserId = userId;

        UserId targetUserId = new UserId(UUID.randomUUID());
        DjId djId = new DjId(100L);

        Dj targetDj = Dj.builder()
                .id(100L)
                .partyroomId(partyroomId)
                .userId(targetUserId)
                .crewId(new CrewId(2L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        Set<Dj> djSet = new HashSet<>();
        djSet.add(targetDj);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();
        partyroom.assignDjSet(djSet);
        partyroom.assignCrewSet(new HashSet<>());

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(crewDomainService.isBelowManagerGrade(partyroom, adminUserId)).thenReturn(false);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(Collections.emptyList());

        // when
        djManagementService.dequeueDj(partyroomId, djId);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
    }
}
