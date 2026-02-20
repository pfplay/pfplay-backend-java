package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.CrewGradeChangedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrewGradeServiceTest {

    @Mock CrewRepository crewRepository;
    @Mock PartyroomInfoService partyroomInfoService;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks CrewGradeService crewGradeService;

    private final UserId adjusterUserId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);
    private final CrewId adjustedCrewId = new CrewId(20L);

    @BeforeEach
    void setUp() {
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(adjusterUserId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    private AdjustGradeRequest createRequest(GradeType gradeType) {
        AdjustGradeRequest request = mock(AdjustGradeRequest.class);
        lenient().when(request.getGradeType()).thenReturn(gradeType);
        return request;
    }

    @Test
    @DisplayName("updateGrade — 정상적인 등급 변경 시 크루의 등급이 업데이트되고 이벤트가 발행된다")
    void updateGrade_success() {
        // given
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).build();
        CrewData adjusterCrew = CrewData.builder()
                .id(1L).userId(adjusterUserId).authorityTier(AuthorityTier.FM).gradeType(GradeType.HOST).build();
        CrewData adjustedCrew = CrewData.builder()
                .id(adjustedCrewId.getId()).userId(new UserId(2L)).authorityTier(AuthorityTier.FM).gradeType(GradeType.CLUBBER).build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(crewRepository.findById(adjustedCrewId.getId())).thenReturn(Optional.of(adjustedCrew));
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), adjusterUserId)).thenReturn(adjusterCrew);
        when(crewRepository.save(any())).thenReturn(adjustedCrew);

        AdjustGradeRequest request = createRequest(GradeType.MODERATOR);

        // when
        crewGradeService.updateGrade(partyroomId, adjustedCrewId, request);

        // then
        assertThat(adjustedCrew.getGradeType()).isEqualTo(GradeType.MODERATOR);
        verify(eventPublisher).publishEvent(any(CrewGradeChangedEvent.class));
    }

    @Test
    @DisplayName("updateGrade — 파티룸이 존재하지 않으면 예외가 발생한다")
    void updateGrade_roomNotFound() {
        // given
        when(partyroomInfoService.getPartyroomById(partyroomId))
                .thenThrow(new NotFoundException("PTR-001", "Can not find Partyroom"));
        AdjustGradeRequest request = createRequest(GradeType.MODERATOR);

        // when & then
        assertThatThrownBy(() -> crewGradeService.updateGrade(partyroomId, adjustedCrewId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("updateGrade — GradeAdjustmentSpecification 검증 실패 시 예외가 발생한다")
    void updateGrade_specValidationFails() {
        // given — adjuster가 CLUBBER인데 MODERATOR로 변경 시도 (권한 부족)
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).build();
        CrewData adjusterCrew = CrewData.builder()
                .id(1L).userId(adjusterUserId).authorityTier(AuthorityTier.FM).gradeType(GradeType.CLUBBER).build();
        CrewData adjustedCrew = CrewData.builder()
                .id(adjustedCrewId.getId()).userId(new UserId(2L)).authorityTier(AuthorityTier.FM).gradeType(GradeType.LISTENER).build();

        when(partyroomInfoService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(crewRepository.findById(adjustedCrewId.getId())).thenReturn(Optional.of(adjustedCrew));
        when(partyroomInfoService.getCrewOrThrow(partyroomId.getId(), adjusterUserId)).thenReturn(adjusterCrew);

        AdjustGradeRequest request = createRequest(GradeType.MODERATOR);

        // when & then
        assertThatThrownBy(() -> crewGradeService.updateGrade(partyroomId, adjustedCrewId, request))
                .isInstanceOf(ForbiddenException.class);
    }
}
