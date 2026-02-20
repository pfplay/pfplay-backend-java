# ADR-005: Cross-Domain Communication via Port/Adapter

## Status
Accepted

## Context

멀티모듈 구조에서 도메인 간 통신이 필요할 때, 직접 import하면 모듈 간 결합도가 높아진다.
DDD의 Bounded Context 원칙에 따라, 각 Context는 자체 모델과 언어를 가져야 하며,
외부 Context와의 통신은 명시적 경계를 통해 이루어져야 한다.

## Decision

**소비하는 Context가 자신의 언어로 Port 인터페이스를 정의하고, `app` 모듈에서 어댑터를 구현한다.**

### Port 정의 원칙
1. Port는 **소비자**의 패키지에 위치 (`application/port/out/`)
2. Port 메서드 시그니처는 **소비자의 언어**를 사용
3. 어댑터는 **공급자**의 서비스를 호출하여 변환

### 구현 패턴
```
[Party Context]                           [User Context]
UserProfileQueryPort  <-- impl --  UserProfileQueryAdapter --> UserProfileService
(party/application/port/out/)      (party/adapter/out/external/)   (user module)
```

### Cross-Domain Port Map

| Consumer | Port | Adapter | Supplier |
|----------|------|---------|----------|
| Party | `PlaylistCommandPort` | `PlaylistCommandAdapter` | Playlist |
| Party | `PlaylistQueryPort` | `PlaylistQueryAdapter` | Playlist |
| Party | `UserProfileQueryPort` | `UserProfileQueryAdapter` | User |
| Party | `UserActivityPort` | `UserActivityAdapter` | User |
| Party | `SessionCachePort` | `PartyroomSessionCacheManager` | Realtime |
| User | `OAuth2RedirectPort` | `OAuth2RedirectAdapter` | Auth |
| User | `PlaylistSetupPort` | `PlaylistSetupAdapter` | Playlist |
| Realtime | `WebSocketAuthPort` | `JwtWebSocketAuthAdapter` | Common |

### Bootstrap Module (Composition Root)

`app/bootstrap/`는 모듈 경계를 넘는 어댑터 중 특별한 위치가 필요한 것을 배치한다:
- `PlaylistSetupAdapter`: User 모듈의 `PlaylistSetupPort` 구현 (Playlist 서비스 호출)

## Consequences

### Positive
- 모듈 간 결합이 인터페이스 레벨로 제한 → 변경 격리
- 각 Context가 자신의 언어로 외부 의존을 표현 → 모델 순수성
- ArchUnit으로 직접 의존 자동 감지 가능
- 모듈 교체/목킹이 Port 구현만 변경하면 됨

### Negative
- 간단한 위임도 Port + Adapter 쌍 생성 필요 → 보일러플레이트
- 데이터 변환 비용 (DTO 매핑)

## References
- `app/.../party/application/port/out/` (6개 Port)
- `app/.../party/adapter/out/external/` (4개 Adapter)
- `app/.../bootstrap/adapter/` (1개 Adapter)
- `user/.../application/port/out/` (2개 Port)
