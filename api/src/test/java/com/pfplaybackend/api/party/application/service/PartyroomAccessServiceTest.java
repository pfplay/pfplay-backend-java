package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
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
import java.util.UUID;

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
        userId = new UserId(UUID.randomUUID());
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
                .crewSet(crewSet)
                .djSet(new HashSet<>())
                .isTerminated(false)
                .build();

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
}
