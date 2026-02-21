# DTO Naming Convention & Package Structure

> 헥사고널 아키텍처 + CQRS-lite 관행에 기반한 네이밍 표준

## 1. Application Layer (`application/dto/`)

| 접미어 | 의미 | 패키지 | 판별 기준 | 합성 공식 | 형태 |
|--------|------|--------|-----------|-----------|------|
| `*Command` | 상태 변경 명령 입력 | `command/` | Service 메서드에 전달되어 상태를 변경하는 입력 | `{Verb}{DomainConcept}Command` | record |
| `*Result` | 유스케이스 출력 | `result/` | Service → Controller에 반환되는 최종 출력 | `{DomainConcept}{Qualifier}Result` | record |
| `*Dto` | 내부 데이터 운반체 | `{subdomain}/` 또는 `shared/` | 레이어 간 조립·전달·프로젝션용 중간 객체 | `{DomainConcept}{Qualifier}Dto` | record (기본), class (예외) |
| `*Event` | 도메인 이벤트 | `event/` | 발생한 사실을 비동기적으로 알림 | `{DomainConcept}{PastTense}Event` | record |

### 판별 흐름

```
Service 메서드에서 Controller로 직접 반환? → *Result
  └ NO → 상태 변경 명령 입력? → *Command
    └ NO → 비동기 이벤트 알림? → *Event
      └ NO → *Dto (내부 운반체)
```

### 루트 정책

`application/dto/` 루트에 파일을 직접 배치하지 않고, 반드시 하위 패키지에 배치합니다.

## 2. Web Adapter Layer (`adapter/in/web/payload/`)

| 접미어 | 의미 | 패키지 | 합성 공식 | 형태 |
|--------|------|--------|-----------|------|
| `*Request` | HTTP 요청 바디 | `payload/request/` | CUD: `{Verb}{DomainConcept}Request`, R: `{DomainConcept}{Qualifier}Request` | class |
| `*Response` | HTTP 응답 바디 | `payload/response/` | CUD: `{Verb}{DomainConcept}Response`, R: `Query{DomainConcept}Response` | class |

## 3. Listener/STOMP Adapter Layer

| 접미어 | 의미 | 패키지 | 합성 공식 | 형태 |
|--------|------|--------|-----------|------|
| `*Message` | Redis Pub/Sub 메시지 | `adapter/in/listener/message/` | `{DomainConcept}{Event}Message` | record |
| STOMP 입력 | WebSocket 수신 페이로드 | `adapter/in/stomp/message/` | `Incoming{DomainConcept}Message` | class |

## 4. Naming Composition Rules

**합성 공식: `{Prefix}{DomainConcept}{Qualifier}{Suffix}`**

| 요소 | 역할 | 예시 |
|------|------|------|
| Prefix (선택) | 동사/액션 또는 도메인 한정자 | `Create`, `Update`, `Admin`, `Query` |
| DomainConcept | 대상 애그리거트/엔티티 | `Partyroom`, `Crew`, `Playback`, `OAuth` |
| Qualifier (선택) | 범위 한정 | `Summary`, `WithProfile`, `Active`, `List` |
| Suffix | 유형 표시자 | `Command`, `Result`, `Dto`, `Request`, `Response` |

### 접두어 규칙

| 상황 | 규칙 | 예시 |
|------|------|------|
| 타 도메인 데이터 접근 | 항상 도메인 접두어 | `AdminPartyroomResult` |
| 동일 도메인, 이름 모호 | 접두어 사용 | `CrewProfileSummaryResult` |
| 동일 도메인 + 하위패키지 컨텍스트 명확 | 접두어 생략 | `CrewDto` (in `crew/`) |

## 5. Verb Reference (CUD Request/Response)

CUD 유형의 Request/Response에는 반드시 **동사 접두어**가 붙습니다. 아래는 허용된 동사 목록과 사용 기준입니다.

### 5.1 허용 동사 목록

| 동사 | HTTP | 의미 | 사용 조건 | 예시 |
|------|------|------|-----------|------|
| `Create` | POST | 새로운 독립 리소스 생성 | 최상위 애그리거트/엔티티를 새로 만들 때 | `CreatePlaylistRequest`, `CreatePartyroomRequest` |
| `Add` | POST | 기존 리소스에 하위 항목 추가 | 이미 존재하는 부모 리소스에 자식을 추가할 때 | `AddTrackRequest`, `AddBlockRequest` |
| `Update` | PUT/PATCH | 기존 리소스 수정 | 리소스의 속성을 변경할 때 | `UpdatePartyroomRequest`, `UpdateAvatarRequest` |
| `Delete` | DELETE | 리소스 삭제 | 리소스를 영구 제거할 때 | `DeletePlaylistsRequest` |
| `Move` | PUT | 리소스의 위치/순서 변경 | 정렬 순서나 소속을 이동할 때 | `MoveTrackRequest` |
| `Register` | POST | 대기열·큐에 등록 | 참여 대기열이나 큐에 자신을 등록할 때 | `RegisterDjRequest` |
| `Cancel` | DELETE | 등록·예약 취소 | Register의 반대 동작, 대기열에서 철회할 때 | `CancelDjRequest` |
| `Login` | POST | 인증 수행 | 사용자 인증 요청 | `LoginOAuthRequest` |
| `Generate` | POST | 일회성 값 생성 | URL, 토큰 등 파생 값을 생성할 때 | `GenerateOAuthUrlRequest` |
| `Initialize` | POST | 환경·상태 초기화 | 시스템 환경을 초기 상태로 구성할 때 | `InitializeDemoEnvironmentRequest` |
| `React` | POST | 사용자 반응 기록 | 좋아요/싫어요 등 반응을 기록할 때 | `ReactPlaybackRequest` |
| `Apply` | POST | 조치 적용 | 제재·정책 등을 대상에 적용할 때 | `ApplyPenaltyRequest` |
| `Adjust` | PUT | 등급·수치 조정 | 등급이나 설정값을 조정할 때 | `AdjustGradeRequest` |
| `Start` | POST | 프로세스 시작 | 시뮬레이션·배치 등 실행 프로세스를 시작할 때 | `StartChatSimulationRequest` |
| `Simulate` | POST | 시뮬레이션 실행 | 테스트용 시뮬레이션 동작을 트리거할 때 | `SimulateReactionsRequest` |
| `Enter` | POST | 공간 입장 | 파티룸 등 가상 공간에 입장할 때 | `EnterPartyroomResponse` |

### 5.2 유사 동사 구분 가이드

#### Create vs Add

| 기준 | Create | Add |
|------|--------|-----|
| 대상 | 최상위 독립 리소스 | 부모 리소스의 하위 항목 |
| URL 패턴 | `POST /playlists` | `POST /playlists/{id}/tracks` |
| 판별 질문 | "이 리소스가 단독으로 존재할 수 있는가?" → Yes | "부모 없이는 의미가 없는가?" → Yes |
| 예시 | `CreatePlaylistRequest` (플레이리스트 생성) | `AddTrackRequest` (플레이리스트에 트랙 추가) |

#### Update vs ~~Set~~

`Set`은 사용하지 않습니다. 모든 기존 리소스 수정은 `Update`로 통일합니다.

| 기준 | Update (사용) | ~~Set~~ (사용 금지) |
|------|--------------|---------------------|
| 이유 | REST 관행에서 PUT/PATCH에 대응하는 표준 동사 | "설정"이라는 의미가 모호 (초기 설정? 변경?) |
| 예시 | `UpdateAvatarRequest` | ~~`SetAvatarRequest`~~ |

#### Register vs Add

| 기준 | Register | Add |
|------|----------|-----|
| 대상 | 대기열·큐 참여 (자발적 등록) | 부모 리소스에 항목 추가 (소유자의 조작) |
| 의미 | 본인이 큐에 자신을 등록 | 소유자가 컬렉션에 항목을 넣음 |
| 예시 | `RegisterDjRequest` (DJ 큐 등록) | `AddTrackRequest` (트랙 목록에 추가) |

#### Cancel vs Delete

| 기준 | Cancel | Delete |
|------|--------|--------|
| 대상 | 등록·예약의 철회 | 리소스의 영구 제거 |
| 복원 | 재등록 가능 (상태 변경) | 복구 불가 (물리 삭제) |
| 예시 | `CancelDjRequest` (DJ 큐 탈퇴) | `DeletePlaylistsRequest` (플레이리스트 삭제) |

#### Start vs Initialize

| 기준 | Start | Initialize |
|------|-------|------------|
| 대상 | 실행 가능한 프로세스 시작 | 환경·데이터의 초기 구성 |
| 반복성 | 반복 실행 가능 | 보통 1회성 또는 리셋 |
| 예시 | `StartChatSimulationRequest` (채팅 시뮬 시작) | `InitializeDemoEnvironmentRequest` (데모 환경 초기화) |

### 5.3 R-type (조회) 접두어 규칙

조회(Read)용 Request/Response는 동사 접두어 대신 다음 규칙을 따릅니다.

| 유형 | 규칙 | 합성 공식 | 예시 |
|------|------|-----------|------|
| Request | 명사 우선 (동사 없음) | `{DomainConcept}{Qualifier}Request` | `MusicSearchRequest`, `PaginationRequest` |
| Response | `Query` 접두어 | `Query{DomainConcept}{Qualifier}Response` | `QueryPartyroomListResponse`, `QueryMusicSearchResponse` |

> `Query`는 동사가 아니라 **유형 표시자**로 취급합니다. CUD Response에는 사용하지 않습니다.

## 6. Examples

### Application Layer

```
application/dto/
├── command/
│   ├── OAuthLoginCommand.java         (record)
│   └── AdminCreatePartyroomCommand.java (record)
├── result/
│   ├── AuthResult.java                (record)
│   └── AdminPartyroomResult.java      (record)
├── event/
│   └── ProfileChangedEvent.java       (record)
├── oauth/
│   ├── OAuthTokenDto.java             (record)
│   └── OAuthUserProfileDto.java       (record)
├── partyroom/
│   └── ActivePartyroomDto.java        (record)
└── playback/
    └── PlaybackDurationWaitDto.java   (record)
```

### Web Adapter Layer

```
adapter/in/web/
├── payload/
│   ├── request/
│   │   ├── LoginOAuthRequest.java              (class)
│   │   └── AdminCreatePartyroomRequest.java   (class)
│   └── response/
│       ├── LoginOAuthResponse.java            (class)
│       └── CreateAdminPartyroomResponse.java  (class)
└── validation/
    ├── ValidProvider.java             (annotation)
    └── ProviderValidator.java         (class)
```

### Listener/STOMP Adapter Layer

```
adapter/in/
├── listener/message/
│   └── PlaybackEndedMessage.java      (record)
└── stomp/message/
    ├── IncomingGroupChatMessage.java   (class)
    └── IncomingPrivateChatMessage.java (class)
```

## 7. Record vs Class 정책

| 형태 | 사용 범위 | 근거 |
|------|----------|------|
| record | `*Command`, `*Result`, `*Dto`, `*Event`, `*Message` | 불변, equals/hashCode 자동 생성 |
| class | `*Request`, `*Response`, STOMP 입력 | Jackson deserialization, `@QueryProjection`, mutable 필요 |

> 상세 정책은 [DTO Record Policy](archive/DTO_RECORD_POLICY.md)를 참조하세요.

---

**Last Updated**: 2026-02-21
