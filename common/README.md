# Common Module — Shared Kernel

## Bounded Context

인프라 설정과 공유 커널을 제공하는 지원 모듈. 독립적인 도메인 로직은 없으며,
다른 모듈들이 공통으로 사용하는 기반 요소를 정의한다.

## 책임

- **Shared Kernel**: Value Object (`UserId`, `Duration`), 기반 엔티티(`BaseEntity`), 도메인 이벤트(`DomainEvent`)
- **인프라 설정**: JPA, Redis, JWT, QueryDSL, Swagger, Security 공통 config
- **공통 서비스**: JWT 토큰 발급/검증(`JwtService`), 분산 락(`RedisLockService`)
- **예외 체계**: `DomainException`, `ExceptionCreator`, HTTP 예외 클래스

## 제공하는 요소

| 구분 | 항목 |
|------|------|
| Value Object | `UserId`, `Duration` |
| Base Class | `BaseEntity` (`registerEvent()` / `pollDomainEvents()`) |
| Event | `DomainEvent` (abstract: `eventId`, `occurredAt`, `eventType`, `getAggregateId()`) |
| Service | `JwtService`, `RedisLockService` |
| Config | `JpaConfig`, `RedisConfig`, `SecurityConfig`, `SwaggerConfig`, `QueryDslConfig` |
| Exception | `DomainException`, `ExceptionCreator`, `NotFoundException`, `ForbiddenException`, etc. |

## 의존 방향

```
common은 외부 모듈에 의존하지 않음
```

## 핵심 패턴

- `BaseEntity`에 도메인 이벤트 수집 메커니즘 내장 (`registerEvent` / `pollDomainEvents`)
- `ExceptionCreator`가 `ErrorType` → HTTP 예외 매핑을 중앙화
- 다른 모듈은 `implementation project(':common')`으로 의존하되, 필요한 Spring starter는 각자 선언
