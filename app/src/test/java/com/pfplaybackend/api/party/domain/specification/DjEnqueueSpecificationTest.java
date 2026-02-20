package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DjEnqueueSpecificationTest {

    private DjEnqueueSpecification spec;

    @BeforeEach
    void setUp() {
        spec = new DjEnqueueSpecification();
    }

    private PartyroomData openQueueRoom() {
        return PartyroomData.builder().isQueueClosed(false).build();
    }

    private PartyroomData closedQueueRoom() {
        return PartyroomData.builder().isQueueClosed(true).build();
    }

    @Test
    @DisplayName("정상 DJ 등록 — 예외 없음")
    void validEnqueue() {
        assertThatNoException().isThrownBy(() ->
                spec.validate(openQueueRoom(), false, false));
    }

    @Test
    @DisplayName("큐 닫힘 — QUEUE_CLOSED")
    void queueClosed() {
        assertThatThrownBy(() -> spec.validate(closedQueueRoom(), false, false))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("빈 플레이리스트 — EMPTY_PLAYLIST")
    void emptyPlaylist() {
        assertThatThrownBy(() -> spec.validate(openQueueRoom(), false, true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("이미 등록된 DJ — ALREADY_REGISTERED")
    void alreadyRegistered() {
        assertThatThrownBy(() -> spec.validate(openQueueRoom(), true, false))
                .isInstanceOf(ConflictException.class);
    }
}
