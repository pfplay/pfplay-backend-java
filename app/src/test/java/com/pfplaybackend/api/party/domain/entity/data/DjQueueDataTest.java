package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DjQueueDataTest {

    @Test
    @DisplayName("createFor — 팩토리 메서드로 생성 시 열린 상태로 초기화된다")
    void createForDefaultOpen() {
        // when
        DjQueueData queue = DjQueueData.createFor(new PartyroomId(1L));

        // then
        assertThat(queue.getPartyroomId()).isEqualTo(new PartyroomId(1L));
        assertThat(queue.isClosed()).isFalse();
    }

    @Test
    @DisplayName("close — 대기열을 닫으면 isClosed가 true가 된다")
    void close() {
        // given
        DjQueueData queue = DjQueueData.createFor(new PartyroomId(1L));

        // when
        queue.close();

        // then
        assertThat(queue.isClosed()).isTrue();
    }

    @Test
    @DisplayName("open — 닫힌 대기열을 열면 isClosed가 false가 된다")
    void open() {
        // given
        DjQueueData queue = DjQueueData.createFor(new PartyroomId(1L));
        queue.close();

        // when
        queue.open();

        // then
        assertThat(queue.isClosed()).isFalse();
    }

    @Test
    @DisplayName("validateOpen — 열린 상태에서 검증 시 예외가 발생하지 않는다")
    void validateOpenOpenNoException() {
        // given
        DjQueueData queue = DjQueueData.createFor(new PartyroomId(1L));

        // when & then
        assertThatNoException().isThrownBy(queue::validateOpen);
    }

    @Test
    @DisplayName("validateOpen — 닫힌 상태에서 검증 시 예외가 발생한다")
    void validateOpenClosedThrows() {
        // given
        DjQueueData queue = DjQueueData.createFor(new PartyroomId(1L));
        queue.close();

        // when & then
        assertThatThrownBy(queue::validateOpen)
                .isInstanceOf(ForbiddenException.class);
    }
}
