package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.application.dto.command.AdminCreatePartyroomCommand;
import com.pfplaybackend.api.admin.application.port.out.AdminPartyroomPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.application.service.PartyroomAccessCommandService;
import com.pfplaybackend.api.party.application.service.PlaybackQueryService;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.admin.application.dto.result.AdminPartyroomResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPartyroomServiceTest {

    @Mock
    private AdminPartyroomPort adminPartyroomPort;

    @Mock
    private PartyroomAccessCommandService partyroomAccessCommandService;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private PlaybackQueryService playbackQueryService;

    @Mock
    private ReactionSimulationService reactionSimulationService;

    @InjectMocks
    private AdminPartyroomService adminPartyroomService;

    private PartyroomData createTestPartyroom(String title, String linkDomain) {
        UserId hostId = new UserId(1L);
        return PartyroomData.builder()
                .id(1L)
                .partyroomId(new PartyroomId(1L))
                .hostId(hostId)
                .stageType(StageType.GENERAL)
                .title(title)
                .introduction("Test introduction")
                .linkDomain(LinkDomain.of(linkDomain))
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(5))
                .noticeContent("")
                .isTerminated(false)
                .build();
    }

    @Test
    @DisplayName("createPartyroomWithHost — 성공 시 파티룸 생성 후 호스트가 입장한다")
    void createPartyroomWithHost_success() {
        // given
        AdminCreatePartyroomCommand command = new AdminCreatePartyroomCommand(
                "100", "Test Room", "Welcome", "testdomain01", 5);

        PartyroomData savedPartyroom = createTestPartyroom("Test Room", "testdomain01");

        when(adminPartyroomPort.savePartyroom(any(PartyroomData.class))).thenReturn(savedPartyroom);

        // when
        AdminPartyroomResult result = adminPartyroomService.createPartyroomWithHost(command);

        // then
        assertThat(result.title()).isEqualTo("Test Room");
        assertThat(result.linkDomain()).isEqualTo("testdomain01");
        verify(partyroomAccessCommandService).enterByHost(any(UserId.class), eq(savedPartyroom));
    }

    @Test
    @DisplayName("createPartyroomWithHost — linkDomain이 null이면 12자 자동 생성된다")
    void createPartyroomWithHost_autoLinkDomain() {
        // given
        AdminCreatePartyroomCommand command = new AdminCreatePartyroomCommand(
                "100", "Auto Link Room", null, null, 5);

        when(adminPartyroomPort.findPartyroomByLinkDomain(any(LinkDomain.class))).thenReturn(Optional.empty());
        when(adminPartyroomPort.savePartyroom(any(PartyroomData.class))).thenAnswer(invocation -> {
            PartyroomData input = invocation.getArgument(0);
            // Simulate JPA @PostPersist by returning with id/partyroomId set
            return PartyroomData.builder()
                    .id(2L)
                    .partyroomId(new PartyroomId(2L))
                    .hostId(input.getHostId())
                    .stageType(input.getStageType())
                    .title(input.getTitle())
                    .introduction(input.getIntroduction())
                    .linkDomain(input.getLinkDomain())
                    .playbackTimeLimit(input.getPlaybackTimeLimit())
                    .noticeContent(input.getNoticeContent())
                    .isTerminated(input.isTerminated())
                    .build();
        });

        // when
        AdminPartyroomResult result = adminPartyroomService.createPartyroomWithHost(command);

        // then
        assertThat(result.linkDomain()).hasSize(12);
        verify(partyroomAccessCommandService).enterByHost(any(UserId.class), any(PartyroomData.class));
    }

    @Test
    @DisplayName("createPartyroomWithHost — 숫자가 아닌 userId 전달 시 BadRequestException이 발생한다")
    void createPartyroomWithHost_invalidUserId_throws() {
        // given
        AdminCreatePartyroomCommand command = new AdminCreatePartyroomCommand(
                "not-a-number", "Room", null, "domain123456", 5);

        // when & then
        assertThatThrownBy(() -> adminPartyroomService.createPartyroomWithHost(command))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("simulateReactions — 활성 재생이 없으면 NotFoundException이 발생한다")
    void simulateReactions_noActivePlayback_throws() {
        // given
        Long partyroomId = 999L;
        PartyroomData partyroom = createTestPartyroom("Room", "linkdomain00");

        PartyroomPlaybackData playbackState = mock(PartyroomPlaybackData.class);
        when(playbackState.getCurrentPlaybackId()).thenReturn(null);

        when(adminPartyroomPort.findPartyroomById(partyroomId)).thenReturn(Optional.of(partyroom));
        when(adminPartyroomPort.findPlaybackState(new PartyroomId(partyroomId)))
                .thenReturn(Optional.of(playbackState));

        // when & then
        assertThatThrownBy(() -> adminPartyroomService.simulateReactions(partyroomId))
                .isInstanceOf(NotFoundException.class);
    }
}
