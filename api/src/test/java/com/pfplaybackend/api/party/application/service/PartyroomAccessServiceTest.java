package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.infrastructure.repository.CrewRepository;
import com.pfplaybackend.api.party.infrastructure.repository.DjRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessServiceTest {

    @Mock private RedisMessagePublisher messagePublisher;
    @Mock private PartyroomRepository partyroomRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private DjRepository djRepository;
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
        when(authContext.getUserId()).thenReturn(userId);
        when(authContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("같은 룸 재진입 시 publishAccessChangedEvent가 호출되어야 한다")
    void tryEnter_sameRoomReEntry_shouldPublishAccessEvent() {
        // given
        CrewData crew = CrewData.builder()
                .id(10L)
                .userId(userId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .isTerminated(false)
                .build();

        when(partyroomRepository.findById(partyroomId.getId())).thenReturn(Optional.of(partyroomData));
        when(crewRepository.countByPartyroomDataIdAndIsActiveTrue(partyroomId.getId())).thenReturn(10L);
        when(crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId)).thenReturn(Optional.of(crew));

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

        // 새 룸 PartyroomData — 사용자가 inactive crew로 존재; addOrActivateCrew가 reactivate
        CrewData newRoomCrew = CrewData.builder()
                .id(20L)
                .userId(userId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(false)
                .build();

        PartyroomData newPartyroomData = PartyroomData.builder()
                .id(2L)
                .partyroomId(newRoomId)
                .isTerminated(false)
                .build();

        when(partyroomRepository.findById(newRoomId.getId())).thenReturn(Optional.of(newPartyroomData));
        when(crewRepository.countByPartyroomDataIdAndIsActiveTrue(newRoomId.getId())).thenReturn(5L);

        // 다른 룸에 이미 active
        ActivePartyroomWithCrewDto activeRoomInfo = mock(ActivePartyroomWithCrewDto.class);
        when(activeRoomInfo.getId()).thenReturn(oldRoomId.getId());
        when(partyroomInfoService.getMyActivePartyroomWithCrewId(userId)).thenReturn(Optional.of(activeRoomInfo));
        when(partyroomDomainService.isActiveInAnotherRoom(eq(newRoomId), any())).thenReturn(true);

        // exit() 호출 시 필요한 mock — 기존 룸 조회
        CrewData oldCrew = CrewData.builder()
                .id(5L)
                .userId(userId)
                .authorityTier(AuthorityTier.FM)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData oldPartyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(oldRoomId)
                .isTerminated(false)
                .build();

        when(partyroomRepository.findById(oldRoomId.getId())).thenReturn(Optional.of(oldPartyroomData));
        // exit() mock: crew lookup
        when(crewRepository.findByPartyroomDataIdAndUserId(oldRoomId.getId(), userId)).thenReturn(Optional.of(oldCrew));
        when(djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(oldRoomId.getId())).thenReturn(Collections.emptyList());

        // addOrActivateCrew mock for new room: inactive crew found → reactivate
        when(crewRepository.findByPartyroomDataIdAndUserId(newRoomId.getId(), userId)).thenReturn(Optional.of(newRoomCrew));
        when(crewRepository.save(any(CrewData.class))).thenReturn(newRoomCrew);

        ProfileSettingDto profileSettingDto = mock(ProfileSettingDto.class);
        when(userProfileService.getUserProfileSetting(userId)).thenReturn(profileSettingDto);

        // when — 예외 없이 정상 실행되어야 함
        partyroomAccessService.tryEnter(newRoomId);

        // then — exit 이벤트 + enter 이벤트 = 최소 2번 publish 호출
        verify(messagePublisher, atLeast(2)).publish(any(), any());
    }
}
