# PFPlay Context Map

## Bounded Contexts

```
+---------------------+     Customer/Supplier     +---------------------+
|                     | <------------------------ |                     |
|   Party Context     |  PlaylistCommandPort      |  Playlist Context   |
|   (app/party)       |  PlaylistQueryPort        |  (playlist module)  |
|                     |                           |                     |
|   AR: PartyroomData |                           |   AR: PlaylistData  |
|       CrewData      |                           |       TrackData     |
|       DjData        |                           |                     |
|       PlaybackData  |                           +---------------------+
|                     |
|   9 Domain Events   |     Customer/Supplier     +---------------------+
|                     | <------------------------ |                     |
+--------+------------+  UserProfileQueryPort     |   User Context      |
         |               UserActivityPort         |   (user module)     |
         |                                        |                     |
         |                                        |   AR: MemberData    |
         |  Published Language                    |       GuestData     |
         |  (WebSocket Events)                    |       ProfileData   |
         v                                        |                     |
+---------------------+                           +--------+------------+
|                     |                                     |
| Realtime Context    |                                     |
| (realtime module)   |              +----------------------+
|                     |              | OAuth2RedirectPort
| WebSocketAuthPort   |              | PlaylistSetupPort
| SessionCachePort    |              v
+---------------------+     +---------------------+
                             |                     |
                             |   Auth Context      |
                             |   (app/auth)        |
                             |                     |
                             |   StateStorePort    |
                             |   ACL -> OAuth2     |
                             +---------------------+

                      +---------------------+
                      |                     |
                      | Common: Shared      |
                      |  Kernel             |
                      |                     |
                      | UserId, Duration,   |
                      | DomainEvent,        |
                      | BaseEntity,         |
                      | Config, Exceptions  |
                      +---------------------+
```

## Context Relationships

### 1. Party <-- Playlist (Customer/Supplier)

Party Context는 Playlist Context에서 트랙 데이터를 가져와 DJ 재생에 사용한다.

| Port | Direction | Purpose |
|------|-----------|---------|
| `PlaylistCommandPort` | Party -> Playlist | 트랙 그랩, 첫 트랙 조회 |
| `PlaylistQueryPort` | Party -> Playlist | 플레이리스트 비어있는지 확인 |

**Adapter**: `PlaylistCommandAdapter`, `PlaylistQueryAdapter` (app/party/adapter/out/external/)

### 2. Party <-- User (Customer/Supplier)

Party Context는 User Context에서 프로필 정보와 활동 점수를 조회/업데이트한다.

| Port | Direction | Purpose |
|------|-----------|---------|
| `UserProfileQueryPort` | Party -> User | 닉네임, 아바타 등 프로필 조회 |
| `UserActivityPort` | Party -> User | DJ 활동 점수 업데이트 |

**Adapter**: `UserProfileQueryAdapter`, `UserActivityAdapter` (app/party/adapter/out/external/)

### 3. Auth <-- User (Customer/Supplier)

Auth Context는 User Context의 OAuth2 리다이렉트 URL과 회원가입/로그인 기능을 사용한다.

| Port | Direction | Purpose |
|------|-----------|---------|
| `OAuth2RedirectPort` | User -> Auth (impl) | OAuth2 프로바이더 URL 생성 |
| `PlaylistSetupPort` | User -> Playlist (impl) | 신규 회원 기본 플레이리스트 생성 |

**Adapter**: `OAuth2RedirectAdapter` (app/auth/), `PlaylistSetupAdapter` (app/bootstrap/)

### 4. Party --> Realtime (Published Language)

Party Context는 Realtime Context의 WebSocket 인프라를 통해 실시간 이벤트를 클라이언트에 브로드캐스트한다.

| Port | Direction | Purpose |
|------|-----------|---------|
| `WebSocketAuthPort` | Realtime <- Common | JWT 기반 WebSocket 인증 |
| `SessionCachePort` | Realtime <- Party | 세션 라이프사이클 관리 |

**Adapter**: `JwtWebSocketAuthAdapter` (common/), `PartyroomSessionCacheManager` (app/party/)

### 5. Auth --> External OAuth (ACL)

Auth Context는 Anti-Corruption Layer를 통해 외부 OAuth2 프로바이더(Google, Twitter)와 통신한다.

**Adapter**: `RedisStateStoreAdapter` (Redis 기반 state 관리)

### 6. Common: Shared Kernel

모든 Context가 공유하는 Value Objects와 인프라 설정.

| 요소 | 설명 |
|------|------|
| `UserId` | 전역 사용자 식별자 |
| `Duration` | 재생 시간 값 객체 |
| `DomainEvent` | 도메인 이벤트 기반 클래스 |
| `BaseEntity` | JPA 엔티티 베이스 + 이벤트 수집 |
| `AuthContext` / `ThreadLocalContext` | 인증 컨텍스트 전파 |
| Config 클래스 | JPA, Redis, JWT, Security, QueryDSL |

## Data Flow

```
[Client] --HTTP/WS--> [Controller]
                          |
                    [Application Service]
                          |
                    [Domain Service]  <-- [Domain Port (Aggregate)]
                          |
                    [Adapter (Repository)]
                          |
                    [Database / Redis]

Cross-domain:
[Party Service] --> [PlaylistCommandPort] --> [PlaylistCommandAdapter] --> [Playlist Service]
```

## Module Dependency Direction

```
app  -->  user     -->  common  -->  realtime
app  -->  playlist -->  common
```

`app`은 모든 모듈에 의존하며, 모듈 간 의존은 Port/Adapter를 통해서만 이루어진다.
