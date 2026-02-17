package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessServiceTest {

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

        PartyContext partyContext = mock(PartyContext.class);
        when(partyContext.getUserId()).thenReturn(userId);
        when(partyContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(partyContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("같은 룸 재진입 시 publishAccessChangedEvent가 호출되어야 한다")
    void tryEnter_sameRoomReEntry_shouldPublishAccessEvent() {
        // given
        Crew crew = Crew.builder()
                .id(10L)
                .partyroomId(partyroomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Set<Crew> crewSet = new HashSet<>();
        crewSet.add(crew);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomId)
                .isTerminated(false)
                .build();
        partyroom.assignCrewSet(crewSet);
        partyroom.assignDjSet(new HashSet<>());

        PartyroomData partyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findById(partyroomId.getId())).thenReturn(Optional.of(partyroomData));
        when(partyroomConverter.toDomain(partyroomData)).thenReturn(partyroom);

        // 같은 룸에 이미 active
        ActivePartyroomWithCrewDto activeRoomInfo = mock(ActivePartyroomWithCrewDto.class);
        when(activeRoomInfo.getId()).thenReturn(1L);
        when(partyroomInfoService.getMyActivePartyroomWithCrewId(userId)).thenReturn(Optional.of(activeRoomInfo));
        when(partyroomDomainService.isActiveInAnotherRoom(any(), any())).thenReturn(false);

        ProfileSettingDto profileSettingDto = mock(ProfileSettingDto.class);
        when(userProfileService.getUserProfileSetting(userId)).thenReturn(profileSettingDto);

        // when
        partyroomAccessService.tryEnter(partyroomId);

        // then — 재진입 시에도 이벤트가 발행되어야 함
        verify(messagePublisher, times(1)).publish(any(), any());
    }

    @Test
    @DisplayName("다른 룸이 active일 때 ACTIVE_ANOTHER_ROOM 예외 대신 exit이 호출되어야 한다")
    void tryEnter_anotherRoomActive_shouldAutoExitInsteadOfException() {
        // given
        PartyroomId newRoomId = new PartyroomId(2L);
        PartyroomId oldRoomId = new PartyroomId(1L);

        Partyroom newPartyroom = Partyroom.builder()
                .partyroomId(newRoomId)
                .isTerminated(false)
                .build();
        newPartyroom.assignCrewSet(new HashSet<>());
        newPartyroom.assignDjSet(new HashSet<>());

        PartyroomData newPartyroomData = mock(PartyroomData.class);
        when(partyroomRepository.findById(newRoomId.getId())).thenReturn(Optional.of(newPartyroomData));
        when(partyroomConverter.toDomain(newPartyroomData)).thenReturn(newPartyroom);

        // 다른 룸에 이미 active
        ActivePartyroomWithCrewDto activeRoomInfo = mock(ActivePartyroomWithCrewDto.class);
        when(activeRoomInfo.getId()).thenReturn(oldRoomId.getId());
        when(partyroomInfoService.getMyActivePartyroomWithCrewId(userId)).thenReturn(Optional.of(activeRoomInfo));
        when(partyroomDomainService.isActiveInAnotherRoom(eq(newRoomId), any())).thenReturn(true);

        // exit() 호출 시 필요한 mock — 기존 룸 조회
        Crew oldCrew = Crew.builder()
                .id(5L)
                .partyroomId(oldRoomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Set<Crew> oldCrewSet = new HashSet<>();
        oldCrewSet.add(oldCrew);

        Partyroom oldPartyroom = Partyroom.builder()
                .partyroomId(oldRoomId)
                .isTerminated(false)
                .build();
        oldPartyroom.assignCrewSet(oldCrewSet);
        oldPartyroom.assignDjSet(new HashSet<>());

        PartyroomDataDto oldPartyroomDataDto = mock(PartyroomDataDto.class);
        PartyroomData oldPartyroomData = mock(PartyroomData.class);

        when(partyroomRepository.findPartyroomDto(oldRoomId)).thenReturn(Optional.of(oldPartyroomDataDto));
        when(partyroomConverter.toEntity(oldPartyroomDataDto)).thenReturn(oldPartyroomData);
        when(partyroomConverter.toDomain(oldPartyroomData)).thenReturn(oldPartyroom);
        when(partyroomConverter.toData(any(Partyroom.class))).thenReturn(oldPartyroomData);

        // 새 룸 enter 시 필요한 mock
        Crew newCrew = Crew.builder()
                .id(20L)
                .partyroomId(newRoomId)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        Set<Crew> newCrewSet = new HashSet<>();
        newCrewSet.add(newCrew);

        Partyroom savedNewPartyroom = Partyroom.builder()
                .partyroomId(newRoomId)
                .build();
        savedNewPartyroom.assignCrewSet(newCrewSet);
        savedNewPartyroom.assignDjSet(new HashSet<>());

        PartyroomData savedNewPartyroomData = mock(PartyroomData.class);
        when(partyroomConverter.toDomain(savedNewPartyroomData)).thenReturn(savedNewPartyroom);
        // 첫 번째 save(exit) → oldPartyroomData, 두 번째 save(enter) → savedNewPartyroomData
        when(partyroomRepository.save(any())).thenReturn(oldPartyroomData, savedNewPartyroomData);

        ProfileSettingDto profileSettingDto = mock(ProfileSettingDto.class);
        when(userProfileService.getUserProfileSetting(userId)).thenReturn(profileSettingDto);

        // when — 예외 없이 정상 실행되어야 함
        partyroomAccessService.tryEnter(newRoomId);

        // then — exit 이벤트 + enter 이벤트 = 최소 2번 publish 호출
        verify(messagePublisher, atLeast(2)).publish(any(), any());
    }
}
