package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessServiceDjQueueChangeTest {

    @Mock private RedisMessagePublisher messagePublisher;
    @Mock private PartyroomRepository partyroomRepository;
    @Mock private PartyroomConverter partyroomConverter;
    @Mock private PartyroomDomainService partyroomDomainService;
    @Mock private PartyroomInfoService partyroomInfoService;
    @Mock private UserProfilePeerService userProfileService;
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
        partyroomAccessService.exit(partyroomId);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PARTYROOM_ACCESS), any());
    }

    @Test
    @DisplayName("exit - DJ 대기열에 없던 사용자가 퇴장하면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void exit_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
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
                .isPlaybackActivated(false)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(new HashSet<>());

        PartyroomDataDto partyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData partyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(partyroomId)).thenReturn(Optional.of(partyroomDataDto));
        when(partyroomConverter.toEntity(partyroomDataDto)).thenReturn(partyroomData);
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);

        // when
        partyroomAccessService.exit(partyroomId);

        // then
        verify(messagePublisher, never()).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PARTYROOM_ACCESS), any());
    }

    @Test
    @DisplayName("expel - DJ 대기열에 있던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void expel_userInDjQueue_shouldPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        Crew targetCrew = Crew.builder()
                .id(2L)
                .partyroomId(partyroomId)
                .userId(targetUserId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Dj dj = Dj.builder()
                .id(100L)
                .partyroomId(partyroomId)
                .userId(targetUserId)
                .crewId(new CrewId(2L))
                .playlistId(new PlaylistId(10L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(targetCrew);
        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(true)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(djSet);

        PartyroomData partyroomData = mock(PartyroomData.class);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);
        when(partyroomInfoService.getDjs(any(Partyroom.class))).thenReturn(Collections.emptyList());

        // when
        partyroomAccessService.expel(partyroom, targetCrew, false);

        // then
        verify(messagePublisher).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PARTYROOM_ACCESS), any());
    }

    @Test
    @DisplayName("expel - DJ 대기열에 없던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void expel_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        Crew targetCrew = Crew.builder()
                .id(2L)
                .partyroomId(partyroomId)
                .userId(targetUserId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(targetCrew);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isPlaybackActivated(false)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(new HashSet<>());

        PartyroomData partyroomData = mock(PartyroomData.class);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(partyroomData);
        when(partyroomRepository.save(any(PartyroomData.class))).thenReturn(partyroomData);

        // when
        partyroomAccessService.expel(partyroom, targetCrew, false);

        // then
        verify(messagePublisher, never()).publish(eq(MessageTopic.DJ_QUEUE_CHANGE), any());
        verify(messagePublisher).publish(eq(MessageTopic.PARTYROOM_ACCESS), any());
    }
}
