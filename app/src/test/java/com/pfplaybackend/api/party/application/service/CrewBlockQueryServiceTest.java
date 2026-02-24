package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewBlockHistoryRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileQueryService;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrewBlockQueryServiceTest {

    @Mock CrewBlockHistoryRepository blockHistoryRepository;
    @Mock UserProfileQueryService userProfileQueryService;
    @Mock PartyroomQueryService partyroomQueryService;
    @InjectMocks CrewBlockQueryService crewBlockQueryService;

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("getBlocks — 차단 목록과 프로필 정보를 함께 반환한다")
    void getBlocksReturnsBlockedCrewsWithProfile() {
        // given
        UserId userId = new UserId(1L);
        ThreadLocalContext.setContext(new AuthContext(userId, AuthorityTier.FM));

        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomQueryService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);

        UserId blockedUserId = new UserId(2L);
        CrewBlockHistoryData blockHistory = CrewBlockHistoryData.builder()
                .id(100L)
                .blockerCrewId(new CrewId(10L))
                .blockedCrewId(new CrewId(20L))
                .blockedUserId(blockedUserId)
                .blockDate(LocalDateTime.now())
                .unblocked(false)
                .build();
        when(blockHistoryRepository.findAllByBlockerCrewIdAndUnblockedIsFalse(new CrewId(10L)))
                .thenReturn(List.of(blockHistory));

        ProfileSettingDto profileDto = new ProfileSettingDto("blockedUser", AvatarCompositionType.SINGLE_BODY, "body", "face", "icon", 0, 0, 0, 0, 1.0);
        when(userProfileQueryService.getUsersProfileSetting(anyList()))
                .thenReturn(Map.of(blockedUserId, profileDto));

        // when
        List<BlockedCrewResult> result = crewBlockQueryService.getBlocks();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nickname()).isEqualTo("blockedUser");
        assertThat(result.get(0).blockedCrewId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("getBlocks — 차단 목록이 비어있으면 빈 리스트를 반환한다")
    void getBlocksReturnsEmptyListWhenNoBlocks() {
        // given
        UserId userId = new UserId(1L);
        ThreadLocalContext.setContext(new AuthContext(userId, AuthorityTier.FM));

        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomQueryService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);
        when(blockHistoryRepository.findAllByBlockerCrewIdAndUnblockedIsFalse(new CrewId(10L)))
                .thenReturn(List.of());

        // when
        List<BlockedCrewResult> result = crewBlockQueryService.getBlocks();

        // then
        assertThat(result).isEmpty();
    }
}
