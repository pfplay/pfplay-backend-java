package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrewPenaltyQueryServiceTest {

    @Mock CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    @Mock PartyroomAggregatePort aggregatePort;
    @Mock UserProfileQueryPort userProfileQueryPort;
    @InjectMocks CrewPenaltyQueryService crewPenaltyQueryService;

    @Test
    @DisplayName("getPenalties — 활성 패널티를 크루/프로필 정보와 함께 반환한다")
    void getPenalties_returnsPenaltiesWithProfile() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        UserId punishedUserId = new UserId(20L);

        CrewPenaltyHistoryData penalty = CrewPenaltyHistoryData.builder()
                .id(100L)
                .partyroomId(partyroomId)
                .punisherCrewId(new CrewId(10L))
                .punishedCrewId(new CrewId(30L))
                .penaltyType(PenaltyType.PERMANENT_EXPULSION)
                .penaltyDate(LocalDateTime.now())
                .released(false)
                .build();
        when(crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId))
                .thenReturn(List.of(penalty));

        CrewData crewData = CrewData.builder()
                .id(30L)
                .partyroomId(partyroomId)
                .userId(punishedUserId)
                .gradeType(GradeType.CLUBBER)
                .build();
        when(aggregatePort.findCrewsByIds(List.of(30L))).thenReturn(List.of(crewData));

        ProfileSettingDto profileDto = new ProfileSettingDto("punishedUser", AvatarCompositionType.SINGLE_BODY, "body", "face", "icon", 0, 0, 0, 0, 1.0);
        when(userProfileQueryPort.getUsersProfileSetting(anyList()))
                .thenReturn(Map.of(punishedUserId, profileDto));

        // when
        List<PenaltyResult> result = crewPenaltyQueryService.getPenalties(partyroomId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nickname()).isEqualTo("punishedUser");
        assertThat(result.get(0).penaltyType()).isEqualTo(PenaltyType.PERMANENT_EXPULSION);
    }

    @Test
    @DisplayName("getPenalties — 패널티가 없으면 빈 리스트를 반환한다")
    void getPenalties_returnsEmptyListWhenNoPenalties() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId))
                .thenReturn(List.of());

        // when
        List<PenaltyResult> result = crewPenaltyQueryService.getPenalties(partyroomId);

        // then
        assertThat(result).isEmpty();
    }
}
