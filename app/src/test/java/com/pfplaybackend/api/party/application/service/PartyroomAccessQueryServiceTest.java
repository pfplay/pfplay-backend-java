package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessQueryServiceTest {

    @Mock PartyroomAggregatePort aggregatePort;
    @InjectMocks PartyroomAccessQueryService partyroomAccessQueryService;

    @Test
    @DisplayName("getRedirectUri — 링크 도메인으로 파티룸 ID를 조회한다")
    void getRedirectUriReturnsPartyroomId() {
        // given
        String linkDomain = "test-link";
        PartyroomData partyroomData = PartyroomData.builder()
                .id(42L)
                .hostId(new UserId(1L))
                .stageType(StageType.GENERAL)
                .title("Room")
                .introduction("Intro")
                .linkDomain(LinkDomain.of(linkDomain))
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(10))
                .isTerminated(false)
                .build();
        when(aggregatePort.findByLinkDomain(LinkDomain.of(linkDomain)))
                .thenReturn(Optional.of(partyroomData));

        // when
        Map<String, Long> result = partyroomAccessQueryService.getRedirectUri(linkDomain);

        // then
        assertThat(result).containsEntry("partyroomId", 42L);
    }
}
