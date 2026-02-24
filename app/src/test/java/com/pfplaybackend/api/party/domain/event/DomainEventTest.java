package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.enums.DjChangeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

    private static final String TEST_ROOM = "Test Room";

    @Test
    @DisplayName("DomainEvent — eventId가 자동 생성된다")
    void domainEventHasEventId() {
        // given
        DomainEvent event = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);

        // then
        assertThat(event.getEventId()).isNotNull();
    }

    @Test
    @DisplayName("DomainEvent — occurredAt이 자동 설정된다")
    void domainEventHasOccurredAt() {
        // given
        DomainEvent event = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);

        // then
        assertThat(event.getOccurredAt()).isNotNull();
    }

    @Test
    @DisplayName("DomainEvent — eventType이 클래스 이름으로 설정된다")
    void domainEventHasEventType() {
        // given
        DomainEvent event = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);

        // then
        assertThat(event.getEventType()).isEqualTo("PartyroomClosedEvent");
    }

    @Test
    @DisplayName("DomainEvent — 서로 다른 이벤트 인스턴스는 서로 다른 eventId를 갖는다")
    void domainEventUniqueEventId() {
        // given
        DomainEvent event1 = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);
        DomainEvent event2 = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);

        // then
        assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
    }

    @Test
    @DisplayName("getAggregateId — partyroomId를 문자열로 반환한다")
    void getAggregateIdReturnsPartyroomIdAsString() {
        // given
        DomainEvent event = new PartyroomClosedEvent(new PartyroomId(42L), new UserId(1L), TEST_ROOM);

        // then
        assertThat(event.getAggregateId()).isEqualTo("42");
    }

    @Test
    @DisplayName("eventType — 이벤트 클래스마다 다른 이름이 설정된다")
    void eventTypeVariesByClass() {
        // given
        DomainEvent closed = new PartyroomClosedEvent(new PartyroomId(1L), new UserId(1L), TEST_ROOM);
        DomainEvent deactivated = new PlaybackDeactivatedEvent(new PartyroomId(1L), new PlaybackId(10L), new CrewId(5L));
        DomainEvent queueChanged = new DjQueueChangedEvent(new PartyroomId(1L), DjChangeType.ENQUEUE, new CrewId(1L));

        // then
        assertThat(closed.getEventType()).isEqualTo("PartyroomClosedEvent");
        assertThat(deactivated.getEventType()).isEqualTo("PlaybackDeactivatedEvent");
        assertThat(queueChanged.getEventType()).isEqualTo("DjQueueChangedEvent");
    }
}
