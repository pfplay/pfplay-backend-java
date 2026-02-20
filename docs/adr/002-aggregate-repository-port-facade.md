# ADR-002: Aggregate Repository Port Facade Pattern

## Status
Accepted

## Context

Application Service들이 3~5개의 개별 JPA Repository를 직접 주입받는 패턴이 반복되었다.
이는 Aggregate 경계를 약화시키고, Repository 변경 시 여러 서비스를 수정해야 하는 문제를 야기했다.

```java
// Before: 5개 Repository 직접 주입
public class DjManagementService {
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final DjQueueRepository djQueueRepository;
    private final PartyroomPlaybackRepository playbackRepository;
}
```

## Decision

**Aggregate Root별 단일 Port 인터페이스를 정의하고, 어댑터가 개별 Repository에 위임한다.**

### Port 위치 규칙
- **Domain Port** (`domain/port/`): 엔티티 CRUD → Domain Service가 의존
- **Application Port** (`application/port/out/`): DTO 반환 쿼리 → Application Service만 의존

### 적용된 Aggregate Root
| Aggregate | Port | Adapter |
|-----------|------|---------|
| Partyroom | `PartyroomAggregatePort` | `PartyroomAggregateAdapter` |
| Playlist  | `PlaylistAggregatePort`  | `PlaylistAggregateAdapter` |

## Consequences

### Positive
- Application Service는 `*AggregatePort` 하나만 주입 → 의존성 단순화
- 개별 Repository 변경이 어댑터에만 영향 → 변경 격리
- Aggregate 경계가 인터페이스 레벨에서 강제됨
- ArchUnit 규칙으로 위반 자동 감지 가능

### Negative
- 포트 인터페이스 메서드 수가 많아질 수 있음
- 어댑터가 다수의 Repository를 주입받아야 함 (복잡도 이동)

## References
- `app/.../party/domain/port/PartyroomAggregatePort.java` (Phase 3)
- `playlist/.../domain/port/PlaylistAggregatePort.java` (Phase 8)
- `app/.../party/adapter/out/persistence/PartyroomAggregateAdapter.java`
- `playlist/.../adapter/out/persistence/PlaylistAggregateAdapter.java`
