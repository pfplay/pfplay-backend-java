package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.application.port.out.AdminPartyroomPort;
import com.pfplaybackend.api.admin.domain.enums.ChatScriptType;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSimulationServiceTest {

    @Mock
    private AdminPartyroomPort adminPartyroomPort;

    @Mock
    private RedisMessagePublisher messagePublisher;

    @InjectMocks
    private ChatSimulationService chatSimulationService;

    @AfterEach
    void cleanup() {
        // Stop all active simulations to avoid leaked threads
        Set<Long> activeIds = chatSimulationService.getActiveSimulations();
        for (Long id : activeIds) {
            chatSimulationService.stopChatSimulation(id);
        }
    }

    private List<CrewData> createCrewList(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> CrewData.builder()
                        .id((long) i)
                        .partyroomId(new PartyroomId(1L))
                        .userId(new UserId((long) i))
                        .gradeType(GradeType.LISTENER)
                        .isActive(true)
                        .enteredAt(java.time.LocalDateTime.now())
                        .build())
                .toList();
    }

    @Test
    @DisplayName("startChatSimulation \u2014 \uc2dc\ubbac\ub808\uc774\uc158\uc774 \uc2dc\uc791\ub418\uba74 \ud65c\uc131 \uc0c1\ud0dc\uac00 \ub41c\ub2e4")
    void startChatSimulation_becomesActive() {
        // given
        Long partyroomId = 1L;
        List<CrewData> crewList = createCrewList(3);
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId))).thenReturn(crewList);

        // when
        chatSimulationService.startChatSimulation(partyroomId, ChatScriptType.CHILL);

        // then
        assertThat(chatSimulationService.isSimulationActive(partyroomId)).isTrue();
    }

    @Test
    @DisplayName("stopChatSimulation \u2014 \uc2dc\ubbac\ub808\uc774\uc158 \uc911\uc9c0 \ud6c4 \ube44\ud65c\uc131 \uc0c1\ud0dc\uac00 \ub41c\ub2e4")
    void stopChatSimulation_becomesInactive() {
        // given
        Long partyroomId = 2L;
        List<CrewData> crewList = createCrewList(3);
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId))).thenReturn(crewList);
        chatSimulationService.startChatSimulation(partyroomId, ChatScriptType.CHILL);

        // when
        chatSimulationService.stopChatSimulation(partyroomId);

        // then
        assertThat(chatSimulationService.isSimulationActive(partyroomId)).isFalse();
    }

    @Test
    @DisplayName("startChatSimulation \u2014 \uc774\ubbf8 \uc2e4\ud589 \uc911\uc774\uba74 \uc911\ubcf5 \uc2dc\uc791\ub418\uc9c0 \uc54a\ub294\ub2e4")
    void startChatSimulation_alreadyRunning_doesNotDuplicate() {
        // given
        Long partyroomId = 3L;
        List<CrewData> crewList = createCrewList(3);
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId))).thenReturn(crewList);

        // when
        chatSimulationService.startChatSimulation(partyroomId, ChatScriptType.CHILL);
        chatSimulationService.startChatSimulation(partyroomId, ChatScriptType.CHILL);

        // then
        verify(adminPartyroomPort, times(1)).findActiveCrewByPartyroom(new PartyroomId(partyroomId));
    }

    @Test
    @DisplayName("startChatSimulation \u2014 \ud06c\ub8e8\uac00 \uc5c6\uc73c\uba74 \uc608\uc678\uac00 \ubc1c\uc0dd\ud55c\ub2e4")
    void startChatSimulation_noCrew_throws() {
        // given
        Long partyroomId = 4L;
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId)))
                .thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> chatSimulationService.startChatSimulation(partyroomId, ChatScriptType.CHILL))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getActiveSimulations \u2014 \ud65c\uc131 \uc2dc\ubbac\ub808\uc774\uc158 \ubaa9\ub85d\uc744 \ubc18\ud658\ud55c\ub2e4")
    void getActiveSimulations_returnsList() {
        // given
        Long partyroomId1 = 5L;
        Long partyroomId2 = 6L;
        List<CrewData> crewList = createCrewList(3);
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId1))).thenReturn(crewList);
        when(adminPartyroomPort.findActiveCrewByPartyroom(new PartyroomId(partyroomId2))).thenReturn(crewList);

        chatSimulationService.startChatSimulation(partyroomId1, ChatScriptType.CHILL);
        chatSimulationService.startChatSimulation(partyroomId2, ChatScriptType.HYPE);

        // when
        Set<Long> activeSimulations = chatSimulationService.getActiveSimulations();

        // then
        assertThat(activeSimulations).hasSize(2);
        assertThat(activeSimulations).containsExactlyInAnyOrder(partyroomId1, partyroomId2);
    }
}
