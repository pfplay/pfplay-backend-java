package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.command.CrewProfileChangedCommand;
import com.pfplaybackend.api.party.application.dto.command.CrewProfilePreCheckCommand;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrewProfileChangeEventHandlerTest {

    @Mock PartyroomQueryService partyroomQueryService;

    @InjectMocks CrewProfileChangeEventHandler crewProfileChangeEventHandler;

    private final UserId userId = new UserId(1L);

    @Test
    @DisplayName("preCheck — 활성 파티룸이 있으면 CrewProfileChangedCommand를 반환한다")
    void preCheckActivePartyroomReturnsCommand() {
        // given
        CrewProfilePreCheckCommand command = new CrewProfilePreCheckCommand(
                userId, "nickname", "faceUri", "bodyUri", "iconUri",
                AvatarCompositionType.SINGLE_BODY, 0, 0, 0.0, 0.0, 1.0
        );
        ActivePartyroomDto activeDto = new ActivePartyroomDto(
                1L, false, 10L, true, new PlaybackId(1L), new CrewId(5L)
        );
        when(partyroomQueryService.getMyActivePartyroom(userId)).thenReturn(Optional.of(activeDto));

        // when
        Optional<CrewProfileChangedCommand> result = crewProfileChangeEventHandler.preCheck(command);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().crewId()).isEqualTo(10L);
        assertThat(result.get().nickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("preCheck — 활성 파티룸이 없으면 empty를 반환한다")
    void preCheckNoActivePartyroomReturnsEmpty() {
        // given
        CrewProfilePreCheckCommand command = new CrewProfilePreCheckCommand(
                userId, "nickname", "faceUri", "bodyUri", "iconUri",
                AvatarCompositionType.SINGLE_BODY, 0, 0, 0.0, 0.0, 1.0
        );
        when(partyroomQueryService.getMyActivePartyroom(userId)).thenReturn(Optional.empty());

        // when
        Optional<CrewProfileChangedCommand> result = crewProfileChangeEventHandler.preCheck(command);

        // then
        assertThat(result).isEmpty();
    }
}
