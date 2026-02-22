package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.application.dto.crew.CrewDto;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.DjQueueInfoResult;
import com.pfplaybackend.api.party.application.dto.result.PartyroomSummaryResult;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomQueryServiceTest {

    @Mock PartyroomAggregatePort aggregatePort;
    @Mock PartyroomQueryPort queryPort;
    @Mock UserProfileQueryPort userProfileQueryPort;
    @Mock PlaybackQueryService playbackQueryService;

    @InjectMocks PartyroomQueryService partyroomQueryService;

    private final UserId userId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);

    @BeforeEach
    void setUp() {
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        lenient().when(authContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("getAllPartyrooms — 파티룸 목록에서 MODERATOR 이상 크루만 3명까지 필터링된다")
    void getAllPartyrooms_filtersCrewsByGradeAndLimit() {
        // given
        CrewDto host = new CrewDto(1L, new UserId(100L), GradeType.HOST);
        CrewDto communityManager = new CrewDto(2L, new UserId(101L), GradeType.COMMUNITY_MANAGER);
        CrewDto moderator = new CrewDto(3L, new UserId(102L), GradeType.MODERATOR);
        CrewDto moderator2 = new CrewDto(4L, new UserId(103L), GradeType.MODERATOR);
        CrewDto clubber = new CrewDto(5L, new UserId(104L), GradeType.CLUBBER);
        CrewDto listener = new CrewDto(6L, new UserId(105L), GradeType.LISTENER);

        PartyroomWithCrewDto dto = new PartyroomWithCrewDto(
                1L, StageType.GENERAL, new UserId(100L), "Test Room", "Hello",
                false, false, 6L, null,
                List.of(host, communityManager, moderator, moderator2, clubber, listener)
        );
        when(queryPort.getCrewDataByPartyroomId()).thenReturn(List.of(dto));

        // when
        List<PartyroomWithCrewDto> result = partyroomQueryService.getAllPartyrooms();

        // then
        assertThat(result).hasSize(1);
        List<CrewDto> filteredCrews = result.get(0).crews();
        assertThat(filteredCrews).hasSize(3);
        assertThat(filteredCrews).allMatch(c -> c.gradeType().isEqualOrHigherThan(GradeType.MODERATOR));
    }

    @Test
    @DisplayName("getPartyroomById — 파티룸이 존재하면 반환한다")
    void getPartyroomById_found_returns() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId())
                .partyroomId(partyroomId)
                .title("Test Room")
                .introduction("Hello")
                .linkDomain(LinkDomain.of("test-room"))
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(5))
                .stageType(StageType.GENERAL)
                .isTerminated(false)
                .build();
        when(aggregatePort.findPartyroomById(partyroomId.getId())).thenReturn(Optional.of(partyroom));

        // when
        PartyroomData result = partyroomQueryService.getPartyroomById(partyroomId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Room");
    }

    @Test
    @DisplayName("getPartyroomById — 파티룸이 없으면 예외가 발생한다")
    void getPartyroomById_notFound_throws() {
        // given
        when(aggregatePort.findPartyroomById(partyroomId.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> partyroomQueryService.getPartyroomById(partyroomId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("isAlreadyRegistered — DJ로 등록되어 있으면 true를 반환한다")
    void isAlreadyRegistered_djRegistered_returnsTrue() {
        // given
        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.of(crew));
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(1L))).thenReturn(true);

        // when
        boolean result = partyroomQueryService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAlreadyRegistered — 크루가 없으면 false를 반환한다")
    void isAlreadyRegistered_noCrew_returnsFalse() {
        // given
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.empty());

        // when
        boolean result = partyroomQueryService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getCrewOrThrow — 크루가 없으면 예외가 발생한다")
    void getCrewOrThrow_noCrew_throws() {
        // given
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> partyroomQueryService.getCrewOrThrow(partyroomId, userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getSummaryInfo — 재생 비활성 시 DJ 정보가 null이다")
    void getSummaryInfo_playbackInactive_djIsNull() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId())
                .partyroomId(partyroomId)
                .title("Test Room")
                .introduction("Hello")
                .linkDomain(LinkDomain.of("test-room"))
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(5))
                .stageType(StageType.GENERAL)
                .isTerminated(false)
                .build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);

        when(aggregatePort.findPartyroomById(partyroomId.getId())).thenReturn(Optional.of(partyroom));
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);

        // when
        PartyroomSummaryResult result = partyroomQueryService.getSummaryInfo(partyroomId);

        // then
        assertThat(result.currentDj()).isNull();
        assertThat(result.title()).isEqualTo("Test Room");
    }

    @Test
    @DisplayName("getDjQueueInfo — DJ 큐 정보를 정상 반환한다")
    void getDjQueueInfo_returnsCorrectResult() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId())
                .partyroomId(partyroomId)
                .title("Test Room")
                .introduction("Hello")
                .linkDomain(LinkDomain.of("test-room"))
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(5))
                .stageType(StageType.GENERAL)
                .isTerminated(false)
                .build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        DjQueueData djQueue = DjQueueData.createFor(partyroomId);

        when(aggregatePort.findPartyroomById(partyroomId.getId())).thenReturn(Optional.of(partyroom));
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(aggregatePort.findDjQueueState(partyroomId)).thenReturn(djQueue);
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.empty());
        when(aggregatePort.findDjsOrdered(partyroomId)).thenReturn(Collections.emptyList());

        // when
        DjQueueInfoResult result = partyroomQueryService.getDjQueueInfo(partyroomId);

        // then
        assertThat(result.playbackActivated()).isFalse();
        assertThat(result.queueStatus()).isEqualTo(QueueStatus.OPEN);
        assertThat(result.registered()).isFalse();
        assertThat(result.currentPlayback()).isNull();
        assertThat(result.djs()).isEmpty();
    }
}
