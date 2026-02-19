package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.infrastructure.repository.CrewRepository;
import com.pfplaybackend.api.party.infrastructure.repository.DjRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyroomInfoServiceIsRegisteredTest {

    @Mock private CrewRepository crewRepository;
    @Mock private DjRepository djRepository;
    @InjectMocks
    private PartyroomInfoService partyroomInfoService;

    private UserId myUserId;

    @BeforeEach
    void setUp() {
        myUserId = new UserId();

        AuthContext authContext = mock(AuthContext.class);
        when(authContext.getUserId()).thenReturn(myUserId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("DJ 대기열에 등록되어 있으면 true 반환")
    void isAlreadyRegistered_shouldReturnTrue_whenUserIsRegistered() {
        // given
        Long partyroomId = 1L;
        when(djRepository.existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(partyroomId, myUserId))
                .thenReturn(true);

        // when
        boolean result = partyroomInfoService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DJ 대기열에 등록되어 있지 않으면 false 반환")
    void isAlreadyRegistered_shouldReturnFalse_whenUserIsNotRegistered() {
        // given
        Long partyroomId = 1L;
        when(djRepository.existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(partyroomId, myUserId))
                .thenReturn(false);

        // when
        boolean result = partyroomInfoService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isFalse();
    }
}
