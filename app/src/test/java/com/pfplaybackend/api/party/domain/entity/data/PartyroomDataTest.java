package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartyroomDataTest {

    private PartyroomData createPartyroom() {
        return PartyroomData.create("Test Room", "intro", LinkDomain.of("youtube.com"),
                PlaybackTimeLimit.ofMinutes(5), StageType.GENERAL, new UserId(1L));
    }

    @Test
    @DisplayName("create — 팩토리 메서드로 생성 시 기본 상태가 올바르게 초기화된다")
    void create_defaultState() {
        // when
        PartyroomData partyroom = createPartyroom();

        // then
        assertThat(partyroom.isTerminated()).isFalse();
        assertThat(partyroom.getNoticeContent()).isEmpty();
        assertThat(partyroom.getTitle()).isEqualTo("Test Room");
        assertThat(partyroom.getStageType()).isEqualTo(StageType.GENERAL);
    }

    @Test
    @DisplayName("validateHost — 호스트가 아닌 사용자로 검증 시 예외가 발생한다")
    void validateHost_notHost_throws() {
        // given
        PartyroomData partyroom = createPartyroom();
        UserId otherUser = new UserId(999L);

        // when & then
        assertThatThrownBy(() -> partyroom.validateHost(otherUser))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("validateHost — 호스트 본인으로 검증 시 예외가 발생하지 않는다")
    void validateHost_host_noException() {
        // given
        PartyroomData partyroom = createPartyroom();
        UserId hostId = partyroom.getHostId();

        // when & then
        assertThatNoException().isThrownBy(() -> partyroom.validateHost(hostId));
    }

    @Test
    @DisplayName("validateNotTerminated — 이미 종료된 파티룸 검증 시 예외가 발생한다")
    void validateNotTerminated_terminated_throws() {
        // given
        PartyroomData partyroom = createPartyroom();
        partyroom.terminate();

        // when & then
        assertThatThrownBy(() -> partyroom.validateNotTerminated())
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("terminate — 파티룸 종료 시 isTerminated가 true가 된다")
    void terminate() {
        // given
        PartyroomData partyroom = createPartyroom();

        // when
        partyroom.terminate();

        // then
        assertThat(partyroom.isTerminated()).isTrue();
    }
}
