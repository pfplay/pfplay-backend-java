package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.event.CrewPenalizedEvent;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.party.application.dto.command.PunishPenaltyCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrewPenaltyCommandServiceTest {

    @Mock ApplicationEventPublisher eventPublisher;
    @Mock PartyroomAggregatePort aggregatePort;
    @Mock PartyroomAccessCommandService partyroomAccessCommandService;
    @Mock CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    @Mock RedisTemplate<String, Object> redisTemplate;
    @Mock PartyroomQueryService partyroomQueryService;
    @InjectMocks CrewPenaltyCommandService crewPenaltyCommandService;

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

    private PartyroomData createPartyroom() {
        return PartyroomData.builder()
                .id(1L).hostId(userId).stageType(StageType.GENERAL)
                .title("Room").introduction("Intro")
                .linkDomain(LinkDomain.of("link")).playbackTimeLimit(PlaybackTimeLimit.ofMinutes(5))
                .noticeContent("").isTerminated(false).build();
    }

    // ========== addPenalty ==========

    @Test
    @DisplayName("addPenalty — CHAT_BAN_30_SECONDS 페널티 시 Redis에 기록하고 이벤트를 발행한다")
    void addPenalty_chatBan() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        PartyroomData partyroom = createPartyroom();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);

        CrewData punisherCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.HOST).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(punisherCrew);

        CrewData punishedCrew = CrewData.builder()
                .id(20L).partyroomId(1L).userId(new UserId(2L)).gradeType(GradeType.CLUBBER).isActive(true).build();
        when(aggregatePort.findCrewById(20L)).thenReturn(Optional.of(punishedCrew));

        PunishPenaltyCommand command = new PunishPenaltyCommand(20L, PenaltyType.CHAT_BAN_30_SECONDS, "Spam");

        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // when
        crewPenaltyCommandService.addPenalty(partyroomId, command);

        // then
        verify(valueOps).set(eq("PENALTY:CHAT_BAN:20"), eq(30), any(java.time.Duration.class));
        verify(eventPublisher).publishEvent(any(CrewPenalizedEvent.class));
        verify(crewPenaltyHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("addPenalty — PERMANENT_EXPULSION 페널티 시 퇴장 처리하고 이력을 저장한다")
    void addPenalty_permanentExpulsion() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        PartyroomData partyroom = createPartyroom();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);

        CrewData punisherCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.HOST).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(punisherCrew);

        CrewData punishedCrew = CrewData.builder()
                .id(20L).partyroomId(1L).userId(new UserId(2L)).gradeType(GradeType.CLUBBER).isActive(true).build();
        when(aggregatePort.findCrewById(20L)).thenReturn(Optional.of(punishedCrew));

        PunishPenaltyCommand command = new PunishPenaltyCommand(20L, PenaltyType.PERMANENT_EXPULSION, "Harassment");

        // when
        crewPenaltyCommandService.addPenalty(partyroomId, command);

        // then
        verify(partyroomAccessCommandService).expel(partyroom, punishedCrew, true);
        verify(eventPublisher).publishEvent(any(CrewPenalizedEvent.class));
        verify(crewPenaltyHistoryRepository).save(any(CrewPenaltyHistoryData.class));
    }

    @Test
    @DisplayName("addPenalty — MODERATOR 미만 등급의 처벌자는 페널티를 부과할 수 없다")
    void addPenalty_gradeInsufficient() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(createPartyroom());

        CrewData punisherCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.CLUBBER).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(punisherCrew);

        CrewData punishedCrew = CrewData.builder()
                .id(20L).partyroomId(1L).userId(new UserId(2L)).gradeType(GradeType.CLUBBER).isActive(true).build();
        when(aggregatePort.findCrewById(20L)).thenReturn(Optional.of(punishedCrew));

        PunishPenaltyCommand command = new PunishPenaltyCommand(20L, PenaltyType.ONE_TIME_EXPULSION, "Reason");

        // when & then
        assertThatThrownBy(() -> crewPenaltyCommandService.addPenalty(partyroomId, command))
                .isInstanceOf(ForbiddenException.class);
    }

    // ========== releaseCrewPenalty ==========

    @Test
    @DisplayName("releaseCrewPenalty — 정상적으로 페널티를 해제하고 크루 밴을 풀고 이력을 업데이트한다")
    void releaseCrewPenalty_success() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(createPartyroom());

        CrewData releaserCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.HOST).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(releaserCrew);

        CrewPenaltyHistoryData historyData = CrewPenaltyHistoryData.builder()
                .id(5L).partyroomId(partyroomId).punishedCrewId(new CrewId(20L))
                .punisherCrewId(new CrewId(10L)).penaltyType(PenaltyType.PERMANENT_EXPULSION)
                .penaltyReason("Harassment").released(false).build();
        when(crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(5L, partyroomId))
                .thenReturn(Optional.of(historyData));

        CrewData punishedCrew = CrewData.builder()
                .id(20L).partyroomId(1L).userId(new UserId(2L)).gradeType(GradeType.CLUBBER)
                .isActive(false).isBanned(true).build();
        when(aggregatePort.findCrewById(20L)).thenReturn(Optional.of(punishedCrew));

        // when
        crewPenaltyCommandService.releaseCrewPenalty(partyroomId, 5L);

        // then
        assertThat(punishedCrew.isBanned()).isFalse();
        verify(aggregatePort).saveCrew(punishedCrew);
        verify(crewPenaltyHistoryRepository).save(historyData);
        assertThat(historyData.isReleased()).isTrue();
    }

    @Test
    @DisplayName("releaseCrewPenalty — MODERATOR 미만 등급은 페널티를 해제할 수 없다")
    void releaseCrewPenalty_gradeInsufficient() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(createPartyroom());

        CrewData releaserCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.CLUBBER).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(releaserCrew);

        // when & then
        assertThatThrownBy(() -> crewPenaltyCommandService.releaseCrewPenalty(partyroomId, 5L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("releaseCrewPenalty — 페널티 이력이 존재하지 않으면 NotFoundException이 발생한다")
    void releaseCrewPenalty_historyNotFound() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(createPartyroom());

        CrewData releaserCrew = CrewData.builder()
                .id(10L).partyroomId(1L).userId(userId).gradeType(GradeType.HOST).isActive(true).build();
        when(partyroomQueryService.getCrewOrThrow(1L, userId)).thenReturn(releaserCrew);

        when(crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(99L, partyroomId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> crewPenaltyCommandService.releaseCrewPenalty(partyroomId, 99L))
                .isInstanceOf(NotFoundException.class);
    }
}
