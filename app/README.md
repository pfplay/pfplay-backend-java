# App Module — Party / Auth / Admin / Bootstrap

## Bounded Context

4개의 서브도메인을 포함하는 최상위 모듈:
- **Party Context**: 파티룸 핵심 도메인 (크루, DJ, 재생, 채팅)
- **Auth Context**: OAuth 인증, JWT 토큰 관리
- **Admin Context**: 관리자 도구
- **Bootstrap**: Composition Root — 모듈 경계를 넘는 어댑터 배치

## Party Context

### 책임
- 파티룸 생성/관리/종료
- 크루(참여자) 입장/퇴장, 등급 관리, 제재
- DJ 큐 관리 및 자동 로테이션
- 재생 제어 및 반응 집계
- 실시간 채팅 (Redis Pub/Sub + WebSocket)

### Partyroom Aggregate
- **Root**: `PartyroomData` — 파티룸 생성, 종료, 설정 변경
- **내부 엔티티**: `CrewData`, `DjData`, `DjQueueData`, `PartyroomPlaybackData`
- **Aggregate Port**: `PartyroomAggregatePort` (`domain/port/`)

### 핵심 엔티티

| 엔티티 | 비즈니스 로직 |
|--------|-------------|
| `PartyroomData` | `create()`, `terminate()`, `validateHost()`, `updateBaseInfo()` |
| `CrewData` | `deactivatePresence()`, `enforceBan()`, `isBelowGrade()` |
| `DjData` | `create()`, DJ 참여 관리 |
| `DjQueueData` | DJ 큐 상태 (열림/닫힘) |
| `PartyroomPlaybackData` | 재생 상태, `deactivate()` |
| `PlaybackData` | 재생 이력 |
| `PlaybackAggregationData` | 반응 집계 |

### Domain Service
- `PartyroomAggregateService` — Aggregate 내부 불변식 검증, DJ 로테이션

### Application Service (Party)

| Service | 역할 |
|---------|------|
| `PartyroomManagementService` | 파티룸 CRUD |
| `PartyroomAccessService` | 입장/퇴장 |
| `PartyroomInfoService` | 파티룸 정보 조회 |
| `PartyroomNoticeService` | 공지 관리 |
| `DjManagementService` | DJ 큐 관리 |
| `PlaybackManagementService` | 재생 제어 |
| `PlaybackInfoService` | 재생 정보/이력 |
| `PlaybackReactionService` | 반응 처리 |
| `PlaybackReactionPostProcessService` | 반응 후처리 + 이벤트 발행 |
| `CrewGradeService` | 크루 등급 변경 |
| `CrewPenaltyService` | 제재 (채팅 금지, 추방) |
| `CrewBlockService` | 차단 |
| `PartyroomChatService` | 채팅 메시지 |

### 도메인 이벤트

| Event | 생성 레벨 | 트리거 |
|-------|----------|--------|
| `PartyroomClosedEvent` | Entity | `PartyroomData.terminate()` |
| `PlaybackDeactivatedEvent` | Entity | `PartyroomPlaybackData.deactivate()` |
| `PlaybackStartedEvent` | Application | `PlaybackManagementService` |
| `CrewAccessedEvent` | Application | `PartyroomAccessService` |
| `DjQueueChangedEvent` | Application | `DjManagementService` |
| `CrewGradeChangedEvent` | Application | `CrewGradeService` |
| `CrewPenalizedEvent` | Application | `CrewPenaltyService` |
| `ReactionMotionChangedEvent` | Application | `PlaybackReactionPostProcessService` |
| `ReactionAggregationChangedEvent` | Application | `PlaybackReactionPostProcessService` |

### Cross-Domain Port (소비)

| Port | Supplier | 용도 |
|------|----------|------|
| `PlaylistCommandPort` | Playlist | 트랙 소비/로테이션 |
| `PlaylistQueryPort` | Playlist | DJ의 트랙 조회 |
| `UserProfileQueryPort` | User | 크루 프로필 조회 |
| `UserActivityPort` | User | 활동 업데이트 |
| `PartyroomQueryPort` | (self) | 파티룸 DTO 조회 |

---

## Auth Context

### 책임
- OAuth2 인증 흐름 (Google, Twitter)
- JWT 토큰 발급/갱신
- OAuth 상태 관리 (Redis)

### Port
- `StateStorePort` (`application/port/out/`) — OAuth 상태 저장/검증

### Service
- `AuthService`, `OAuthUrlService`, `OAuthClientService`, `LogoutService`

---

## Admin Context

### 책임
- 관리자 파티룸/사용자/프로필 관리
- 데모 데이터, 시뮬레이션 (채팅, 반응)

### Service
- `AdminPartyroomService`, `AdminUserService`, `AdminProfileService`
- `AdminDemoService`, `ChatSimulationService`, `ReactionSimulationService`

---

## Bootstrap (Composition Root)

### 책임
모듈 경계를 넘는 어댑터 중, 어느 도메인에도 속하지 않는 것을 배치한다.

### 어댑터
- `PlaylistSetupAdapter` — User의 `PlaylistSetupPort` 구현 (Playlist 서비스 호출)
- `OAuth2RedirectAdapter` — User의 `OAuth2RedirectPort` 구현 (Auth 서비스 호출)

---

## 의존 방향

```
app → common, user, playlist, realtime
```
