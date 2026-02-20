# ADR-003: @ManyToOne to ID Reference Migration

## Status
Accepted

## Context

Aggregate 내부 엔티티들이 `@ManyToOne` JPA 참조로 Root 엔티티를 직접 참조하고 있었다.
이는 JPA에서는 편리하지만, Aggregate 경계를 물리적으로 강제하지 못한다.

```java
// Before
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "partyroom_id")
private PartyroomData partyroomData;
```

## Decision

**Aggregate 간 `@ManyToOne` 참조를 ID 참조(`Long`)로 교체한다.**

```java
// After
@Column(name = "partyroom_id", nullable = false)
private Long partyroomId;
```

### 적용 대상
| Entity | Before | After |
|--------|--------|-------|
| `CrewData` | `PartyroomData partyroomData` | `Long partyroomId` |
| `DjData` | `PartyroomData partyroomData` | `Long partyroomId` |
| `TrackData` | `PlaylistData playlistData` | `Long playlistId` |

### DB 변경
**없음** — 컬럼명, 인덱스, FK 모두 동일. JPA 매핑만 변경.

## Consequences

### Positive
- Aggregate 경계가 물리적으로 강제됨 (객체 그래프 탐색 불가)
- N+1 쿼리 위험 제거 (Lazy Loading 근본 차단)
- 쿼리 의도가 명시적 (explicit join)

### Negative
- QueryDSL implicit join → explicit join 변환 필요
- 팩토리 메서드 시그니처 변경 (`PartyroomData` → `Long partyroomId`)
- Repository 메서드명 일부 변경

### QueryDSL Migration
```java
// Before (implicit join)
.join(qCrewData.partyroomData, qPartyroomData)

// After (explicit join)
.join(qPartyroomData).on(qPartyroomData.id.eq(qCrewData.partyroomId))
```

## References
- `app/.../domain/entity/data/CrewData.java` (Phase 4)
- `app/.../domain/entity/data/DjData.java` (Phase 4)
- `playlist/.../domain/entity/data/TrackData.java` (Phase 4)
