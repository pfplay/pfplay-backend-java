package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewBlockHistoryRepository;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.AddBlockRequest;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrewBlockServiceTest {

    @Mock PartyroomAggregatePort aggregatePort;
    @Mock CrewBlockHistoryRepository blockHistoryRepository;
    @Mock UserProfileService userProfileService;
    @Mock PartyroomInfoService partyroomInfoService;
    @InjectMocks CrewBlockService crewBlockService;

    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = new UserId(1L);
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    // ========== addBlock ==========

    @Test
    @DisplayName("addBlock — 정상적으로 크루를 차단하고 이력을 저장한다")
    void addBlock_success() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomInfoService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);

        CrewId blockerCrewId = new CrewId(10L);
        CrewId blockedCrewId = new CrewId(20L);
        when(blockHistoryRepository.findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(blockerCrewId, blockedCrewId))
                .thenReturn(Optional.empty());

        UserId blockedUserId = new UserId(2L);
        CrewData blockedCrew = CrewData.builder()
                .id(20L).partyroomId(1L).userId(blockedUserId).gradeType(GradeType.CLUBBER)
                .isActive(true).isBanned(false).build();
        when(aggregatePort.findCrewById(20L)).thenReturn(Optional.of(blockedCrew));

        AddBlockRequest request = new AddBlockRequest(20L);

        // when
        crewBlockService.addBlock(request);

        // then
        ArgumentCaptor<CrewBlockHistoryData> captor = ArgumentCaptor.forClass(CrewBlockHistoryData.class);
        verify(blockHistoryRepository).save(captor.capture());
        CrewBlockHistoryData saved = captor.getValue();
        assertThat(saved.getBlockerCrewId()).isEqualTo(blockerCrewId);
        assertThat(saved.getBlockedCrewId()).isEqualTo(blockedCrewId);
        assertThat(saved.getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(saved.isUnblocked()).isFalse();
    }

    @Test
    @DisplayName("addBlock — 이미 차단된 크루를 다시 차단하면 BadRequestException이 발생한다")
    void addBlock_alreadyBlocked() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomInfoService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);

        CrewId blockerCrewId = new CrewId(10L);
        CrewId blockedCrewId = new CrewId(20L);
        CrewBlockHistoryData existing = CrewBlockHistoryData.builder()
                .id(1L).blockerCrewId(blockerCrewId).blockedCrewId(blockedCrewId).unblocked(false).build();
        when(blockHistoryRepository.findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(blockerCrewId, blockedCrewId))
                .thenReturn(Optional.of(existing));

        AddBlockRequest request = new AddBlockRequest(20L);

        // when & then
        assertThatThrownBy(() -> crewBlockService.addBlock(request))
                .isInstanceOf(BadRequestException.class);
    }

    // ========== removeBlock ==========

    @Test
    @DisplayName("removeBlock — 정상적으로 차단을 해제하고 이력을 업데이트한다")
    void removeBlock_success() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomInfoService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);

        CrewId blockerCrewId = new CrewId(10L);
        CrewBlockHistoryData historyData = CrewBlockHistoryData.builder()
                .id(5L).blockerCrewId(blockerCrewId).blockedCrewId(new CrewId(20L))
                .blockedUserId(new UserId(2L)).unblocked(false).build();
        when(blockHistoryRepository.findByIdAndBlockerCrewIdAndUnblockedIsFalse(5L, blockerCrewId))
                .thenReturn(Optional.of(historyData));

        // when
        crewBlockService.removeBlock(5L);

        // then
        assertThat(historyData.isUnblocked()).isTrue();
        verify(blockHistoryRepository).save(historyData);
    }

    @Test
    @DisplayName("removeBlock — 차단 이력이 없으면 NotFoundException이 발생한다")
    void removeBlock_notFound() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(1L, false, 10L, false, null, null);
        when(partyroomInfoService.getMyActivePartyroomOrThrow(userId)).thenReturn(activeDto);

        CrewId blockerCrewId = new CrewId(10L);
        when(blockHistoryRepository.findByIdAndBlockerCrewIdAndUnblockedIsFalse(99L, blockerCrewId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> crewBlockService.removeBlock(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
