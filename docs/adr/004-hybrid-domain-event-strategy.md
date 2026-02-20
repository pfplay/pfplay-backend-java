# ADR-004: Hybrid Domain Event Generation Strategy

## Status
Accepted

## Context

도메인 이벤트를 어디서 생성할 것인가에 대한 두 가지 접근이 있다:

1. **엔티티 레벨**: 상태 변경 시 엔티티가 직접 이벤트 등록
2. **Application Service 레벨**: 서비스가 상태 변경 후 이벤트 생성/발행

순수 DDD에서는 엔티티 레벨이 이상적이지만, 여러 엔티티 데이터가 필요한 복잡한 이벤트는
Application Service에서 조합하는 것이 더 실용적이다.

## Decision

**하이브리드 전략을 채택한다.**

### 엔티티 레벨 이벤트 (단순 상태 전이)
```java
// PartyroomData.terminate()
public void terminate() {
    this.isTerminated = true;
    registerEvent(new PartyroomClosedEvent(this.partyroomId));
}

// PartyroomPlaybackData.deactivate()
public void deactivate() {
    this.currentPlaybackId = null;
    this.isActivated = false;
    registerEvent(new PlaybackDeactivatedEvent(new PartyroomId(this.partyroomId)));
}
```

Application Service에서 `pollDomainEvents()`로 수집 후 발행:
```java
partyroom.terminate();
aggregatePort.savePartyroom(partyroom);
partyroom.pollDomainEvents().forEach(eventPublisher::publishEvent);
```

### Application Service 레벨 이벤트 (복잡한 조합)
```java
// PlaybackStartedEvent — 여러 엔티티 데이터 필요
eventPublisher.publishEvent(new PlaybackStartedEvent(
    partyroomId, crewId, PlaybackDto.from(playback, trackDto)));
```

### DomainEvent 기반 클래스
```java
public abstract class DomainEvent {
    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;
    public abstract String getAggregateId();
}
```

## Consequences

### Positive
- 단순 상태 전이는 엔티티가 책임 → 도메인 응집도 향상
- 복잡한 이벤트는 서비스에서 자유롭게 조합 가능
- `eventId`, `occurredAt` 메타데이터로 이벤트 추적 가능

### Negative
- 이벤트 생성 위치가 혼재 → 일관성 규칙 필요
- `pollDomainEvents()` 호출을 잊으면 이벤트 유실 위험

## Applied Events

| Event | Generation Level | Trigger |
|-------|-----------------|---------|
| `PartyroomClosedEvent` | Entity | `PartyroomData.terminate()` |
| `PlaybackDeactivatedEvent` | Entity | `PartyroomPlaybackData.deactivate()` |
| `PlaybackStartedEvent` | Application | `PlaybackManagementService` |
| `CrewAccessedEvent` | Application | `PartyroomAccessService` |
| `DjQueueChangedEvent` | Application | `DjManagementService` |
| `CrewGradeChangedEvent` | Application | `CrewGradeService` |
| `CrewPenalizedEvent` | Application | `CrewPenaltyService` |
| `ReactionMotionChangedEvent` | Application | `PlaybackReactionPostProcessService` |
| `ReactionAggregationChangedEvent` | Application | `PlaybackReactionPostProcessService` |

## References
- `common/.../domain/event/DomainEvent.java` (Phase 5)
- `common/.../entity/BaseEntity.java` — `registerEvent()` / `pollDomainEvents()`
