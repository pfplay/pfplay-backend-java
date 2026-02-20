# ADR-001: Unified Entity Model (*Data.java)

## Status
Accepted

## Context

전통적인 DDD에서는 도메인 모델과 JPA 엔티티를 분리하여 도메인 순수성을 보장한다.
그러나 이 방식은 Converter/Mapper 레이어가 필요하고, 코드 중복과 유지보수 비용이 증가한다.

PFPlay는 중규모 프로젝트로, 도메인 모델과 영속성 모델의 구조적 차이가 크지 않다.

## Decision

**JPA 엔티티(`*Data.java`)에 비즈니스 로직을 직접 포함하는 통합 모델을 사용한다.**

- `PartyroomData.java`: `create()`, `terminate()`, `validateHost()`, `updateBaseInfo()`
- `CrewData.java`: `deactivatePresence()`, `enforceBan()`, `isBelowGrade()`
- `PlaylistData.java`: `create()`, `rename()`

## Consequences

### Positive
- Converter/Mapper 레이어 제거 → 코드 간결화
- 단일 모델로 도메인 로직과 영속성이 한 곳에 → 이해 용이
- `@Builder` + 팩토리 메서드로 객체 생성 캡슐화

### Negative
- JPA 애노테이션(`@Entity`, `@Column`)이 도메인 로직과 혼재
- 도메인 모델이 JPA에 물리적으로 의존
- 테스트 시 JPA 초기화 없이 엔티티를 생성해야 함 (Builder로 해결)

### Mitigations
- `protected` no-arg 생성자로 JPA만 접근
- `@Setter` 제거 → 도메인 메서드를 통한 상태 변경만 허용 (Phase 1)
- Value Object 적극 활용 (`UserId`, `PartyroomId`, `LinkDomain` 등)

## References
- `app/.../domain/entity/data/PartyroomData.java`
- `playlist/.../domain/entity/data/PlaylistData.java`
