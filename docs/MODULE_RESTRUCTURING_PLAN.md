# Gradle 모듈 재구조화 계획

> **Status: COMPLETED** — 4개 Phase 모두 완료 (2026-02-20)
>
> 2개 모듈(api, realtime)을 4개 모듈(common, realtime, playlist, app)로 재구성하고,
> profile/avatarresource 패키지를 user로 병합.
>
> | Phase | 내용 | Commit |
> |-------|------|--------|
> | A | common 모듈 승격 | `3816ff2` |
> | B | playlist 모듈 분리 | `d0d543c` |
> | C | api → app 개명 | `ad643af` |
> | D | profile+avatarresource → user 병합 | `222b495` |

---

## 현재 상태

### 모듈 구조

```
pfplay-backend-java/
├── api/        (403파일 — 전체 도메인 + 인프라 + REST + 리스너 + STOMP)
└── realtime/   (10파일 — WebSocket 인프라, 도메인 import 0건)
```

### 도메인 패키지 (api 모듈 내)

| 패키지 | 파일 수 | 역할 |
|--------|---------|------|
| party | 162 | Core — 파티룸, 크루, DJ, 재생, 채팅 |
| user | 65 | Identity — 회원, 게스트, 활동 |
| common | 51 | Shared Kernel — config, 예외, 공유 VO |
| playlist | 46 | Media — 플레이리스트, 트랙 |
| admin | 27 | Management — 관리 콘솔 |
| auth | 25 | Authentication — OAuth, 로그인 플로우 |
| profile | 22 | User Support — 프로필, 아바타 |
| avatarresource | 4 | User Support — 아바타 리소스 참조 데이터 |
| bootstrap | 1 | Composition Root |

### 도메인 간 의존 관계

```
                    ┌─────────┐
                    │ common  │ ← 모든 도메인 (326 imports)
                    │ (51파일) │    역방향 import: 0건 ✓
                    └────┬────┘
                         │
    ┌────────┬───────────┼───────────┬──────────┐
    ▼        ▼           ▼           ▼          ▼
  auth     party        user      playlist   admin
 (25파일)  (162파일)    (65파일)    (46파일)   (27파일)
    │        │           │                      │
    │        ├─Port────→ user (22 imports)       │
    │        ├─Port────→ playlist (3 imports)    │
    │        │           │                      │
    │        │           ├─35 직접──→ profile    │
    │        │           ├─13 직접──→ avatarres  │
    │        │           │                      │
    └────────┴───────────┴──────────────────────┘
              admin이 전부 직접 참조 (Port 없음)
              party 49, user 23, playlist 6
```

### 기존 Port 인터페이스 (6개)

| Port | 정의 위치 | 구현체 | 방향 |
|------|-----------|--------|------|
| `PlaylistCommandPort` | party/application/port/out | PlaylistCommandAdapter | party → playlist |
| `PlaylistQueryPort` | party/application/port/out | PlaylistQueryAdapter | party → playlist |
| `UserProfileQueryPort` | party/application/port/out | UserProfileQueryAdapter | party → user/profile |
| `UserActivityPort` | party/application/port/out | UserActivityAdapter | party → user |
| `PlaylistSetupPort` | user/application/port/out | PlaylistSetupAdapter | user → playlist |
| `StateStorePort` | auth/application/port/out | RedisStateStoreAdapter | auth → Redis |

---

## 문제 진단

### 1. common의 격리가 컨벤션에만 의존

common → 도메인 역방향 import가 0건이지만 **컴파일러가 강제하지 않는다.**
누군가 `common/config/`에서 `party.domain.entity`를 import해도 빌드가 통과한다.

### 2. playlist가 이미 Port 격리됐으나 모듈이 아님

party→playlist, user→playlist 의존이 모두 Port 경유인데,
playlist 내부에서 party를 직접 import해도 컴파일러가 막지 않는다.

### 3. profile + avatarresource가 user와 분리되어 있으나 사실상 하나

- user→profile: 35건 직접 import (`ProfileData`, `UserProfileService`, `UserAvatarDomainService`)
- user→avatarresource: 13건 직접 import (리포지토리 3개 + DTO)
- `UserAvatarDomainService`가 user 패키지에 있으면서 profile 엔티티를 조작
- 하나의 Bounded Context를 셋으로 쪼개놓은 상태

### 4. api 모듈 이름이 부정확

REST API뿐 아니라 도메인 전체, Redis 리스너, STOMP 컨트롤러, 부트스트랩을 포함.
"API 레이어만 있다"는 오해 유발.

---

## 목표 구조

```
pfplay-backend-java/
│
├── common/           ◀ Gradle 모듈 (기존 common 패키지를 모듈로 승격)
│   └── com.pfplaybackend.api.common/
│       ├── domain/value/         UserId, Duration, DurationConverter
│       ├── domain/enums/         MessageTopic, AvatarCompositionType
│       ├── domain/event/         DomainEvent (base class)
│       ├── enums/                AuthorityTier
│       ├── config/               Redis, Security, JPA, Swagger, Cache, REST, Mapper
│       ├── exception/            GlobalExceptionHandler, DomainException, ErrorType, HTTP exceptions
│       ├── adapter/realtime/     JwtWebSocketAuthAdapter
│       ├── dto/                  PaginationDto
│       ├── entity/               BaseEntity
│       └── ApiCommonResponse, ThreadLocalContext
│
├── realtime/         ◀ Gradle 모듈 (변경 없음, 10파일)
│   └── com.pfplaybackend.realtime/
│       ├── port/                 WebSocketAuthPort, SessionCachePort
│       ├── config/               WebSocketConfig
│       ├── sender/               SimpMessageSender
│       ├── interceptor/          WebSocketHandshakeInterceptor
│       ├── event/                4개 이벤트 핸들러
│       └── controller/           HeartbeatController
│
├── playlist/         ◀ Gradle 모듈 (신규 분리, 46파일)
│   └── com.pfplaybackend.api.playlist/
│       ├── adapter/in/web/       REST 컨트롤러 + payload/
│       ├── adapter/out/persistence/  JPA 리포지토리 + QueryDSL impl
│       ├── application/service/  PlaylistCommandService, TrackCommandService, ...
│       ├── application/dto/      PlaylistDto, PlaylistTrackDto, SearchResult*
│       └── domain/               PlaylistData, TrackData, enums, value, exception
│
└── app/              ◀ Gradle 모듈 (api에서 개명, ~300파일)
    └── com.pfplaybackend.api/
        ├── party/                Core 도메인 (162파일, 변경 없음)
        ├── user/                 Identity 도메인 (91파일, profile+avatarresource 병합)
        │   ├── adapter/in/web/   기존 user + profile 컨트롤러 통합
        │   ├── application/
        │   │   ├── service/      UserProfileService, UserAvatarService 등 통합
        │   │   ├── port/out/     PlaylistSetupPort (기존 유지)
        │   │   └── dto/          command/, shared/ (기존 유지)
        │   ├── domain/
        │   │   ├── entity/data/  MemberData, GuestData, ProfileData, AvatarResource*
        │   │   ├── service/      UserAvatarDomainService
        │   │   └── value/, enums/
        │   └── adapter/out/
        │       ├── persistence/  기존 user + profile + avatarresource 리포지토리
        │       └── external/     PlaylistSetupAdapter
        ├── auth/                 인증 (25파일, 변경 없음)
        ├── admin/                관리 콘솔 (27파일, 변경 없음)
        └── bootstrap/            Application 진입점 + 초기화
```

### Gradle 의존성 그래프

```
common    → (없음)
realtime  → (없음)
playlist  → common
app       → common, realtime, playlist
```

순환 의존 없음. 모든 화살표가 단방향.

---

## 구현 단계

### Phase A: common 모듈 승격

**범위:** common 패키지를 독립 Gradle 모듈로 분리
**코드 변경:** 없음 (역방향 의존 0건이므로)

#### A-1. 디렉터리 구조 생성

```
common/
└── src/main/java/com/pfplaybackend/api/common/
    └── (api/src/main/java/.../common/ 하위 전체 이동)
```

#### A-2. common/build.gradle 생성

```groovy
dependencies {
    // Spring Boot starters (security, data-redis, data-jpa, web, validation)
    // JWT (jjwt)
    // Swagger (springdoc)
    // QueryDSL
    // realtime 모듈
    implementation project(':realtime')
}
```

common은 `JwtWebSocketAuthAdapter`가 realtime의 `WebSocketAuthPort`를 구현하므로 realtime 의존 필요.

#### A-3. app/build.gradle 수정

```groovy
dependencies {
    implementation project(':common')
    implementation project(':realtime')
    // ... 기존 도메인 의존성
}
```

#### A-4. settings.gradle 수정

```groovy
include 'common', 'realtime', 'api'
```

#### A-5. 검증

```bash
./gradlew :common:compileJava   # 0 errors
./gradlew :api:compileJava      # 0 errors
./gradlew :api:test             # 0 failures
```

#### A-6. 역방향 의존 방지 확인

common/build.gradle에 api(app) 의존성이 없으므로,
common에서 `import com.pfplaybackend.api.party.*` 작성 시 컴파일 에러 발생 → 목표 달성.

---

### Phase B: playlist 모듈 분리

**범위:** playlist 패키지를 독립 Gradle 모듈로 분리
**코드 변경:** 최소 — Port 구현체(Adapter) 위치 조정만 필요

#### B-1. 디렉터리 구조 생성

```
playlist/
└── src/main/java/com/pfplaybackend/api/playlist/
    └── (api/src/main/java/.../playlist/ 하위 전체 이동)
```

#### B-2. Port 구현체 배치 확인

playlist 모듈 분리 시 의존 방향:

| Port | 정의 위치 | 구현체 | 위치 결정 |
|------|-----------|--------|-----------|
| `PlaylistCommandPort` | app(party) | `PlaylistCommandAdapter` | **app에 유지** (party가 정의, playlist를 호출) |
| `PlaylistQueryPort` | app(party) | `PlaylistQueryAdapter` | **app에 유지** |
| `PlaylistSetupPort` | app(user) | `PlaylistSetupAdapter` | **app에 유지** (user가 정의, playlist를 호출) |

Adapter 구현체는 app 모듈에 남는다. app이 playlist를 의존하므로 playlist의 Service를 호출할 수 있다.
Port 인터페이스도 app(party/user)에 남는다.

**playlist 모듈 자체는 common만 의존하면 된다.**

#### B-3. playlist/build.gradle 생성

```groovy
dependencies {
    implementation project(':common')
    // Spring Boot starters (data-jpa, web, validation)
    // QueryDSL
    // Swagger
}
```

#### B-4. app/build.gradle 수정

```groovy
dependencies {
    implementation project(':common')
    implementation project(':realtime')
    implementation project(':playlist')
}
```

#### B-5. settings.gradle 수정

```groovy
include 'common', 'realtime', 'playlist', 'api'
```

#### B-6. 검증

```bash
./gradlew :playlist:compileJava  # 0 errors (playlist → party import 없어야 함)
./gradlew :api:compileJava       # 0 errors
./gradlew :api:test              # 0 failures
```

---

### Phase C: api → app 개명

**범위:** Gradle 모듈 디렉터리명만 변경. Java 패키지 경로(`com.pfplaybackend.api`)는 불변.
**코드 변경:** 없음

#### C-1. 디렉터리 rename

```bash
git mv api app
```

#### C-2. settings.gradle 수정

```groovy
include 'common', 'realtime', 'playlist', 'app'
```

#### C-3. 다른 build.gradle에서 참조 수정

```groovy
// 없음 — api를 참조하는 다른 모듈이 없으므로
// (realtime, common, playlist는 app을 의존하지 않음)
```

#### C-4. 검증

```bash
./gradlew :app:compileJava  # 0 errors
./gradlew :app:test          # 0 failures
```

---

### Phase D: profile + avatarresource → user 병합

**범위:** profile(22파일) + avatarresource(4파일)를 user 패키지 하위로 이동
**코드 변경:** import 경로 변경 (48건 cross-domain import → 내부 참조), 로직 변경 없음

#### D-1. 파일 이동 계획

**profile → user:**

| 원본 경로 | 이동 경로 |
|-----------|-----------|
| `profile/adapter/in/web/*Controller.java` | `user/adapter/in/web/` |
| `profile/adapter/in/web/dto/request/*.java` | `user/adapter/in/web/payload/request/` |
| `profile/adapter/out/persistence/*.java` | `user/adapter/out/persistence/` |
| `profile/application/service/*.java` | `user/application/service/` |
| `profile/application/dto/*.java` | `user/application/dto/` |
| `profile/domain/entity/data/ProfileData.java` | `user/domain/entity/data/` |
| `profile/domain/enums/*.java` | `user/domain/enums/` |
| `profile/domain/value/*.java` | `user/domain/value/` |
| `profile/domain/exception/*.java` | `user/domain/exception/` |

**avatarresource → user:**

| 원본 경로 | 이동 경로 |
|-----------|-----------|
| `avatarresource/adapter/out/persistence/*.java` | `user/adapter/out/persistence/` |
| `avatarresource/application/service/*.java` | `user/application/service/` |
| `avatarresource/domain/entity/data/*.java` | `user/domain/entity/data/` |

#### D-2. import 경로 일괄 수정

```
com.pfplaybackend.api.profile.* → com.pfplaybackend.api.user.*
com.pfplaybackend.api.avatarresource.* → com.pfplaybackend.api.user.*
```

영향 범위:
- user 패키지 내부: ~35건 (profile import → 내부 참조로 전환)
- party 패키지: UserProfileQueryAdapter에서 profile import → user import
- admin 패키지: ~4건
- bootstrap: ~1건

#### D-3. 파일명 충돌 확인

이동 전에 user 패키지에 동명 파일이 있는지 확인 필요.
예: `UserProfileService`가 이미 user에 있을 경우 → profile 것을 그대로 이동 (현재 profile에만 존재).

#### D-4. @ComponentScan 영향 없음

`@SpringBootApplication(scanBasePackages = "com.pfplaybackend")` — base package가 루트이므로
패키지 이동이 Spring Bean 스캔에 영향 없음.

#### D-5. 검증

```bash
./gradlew :app:compileJava  # 0 errors
./gradlew :app:test          # 0 failures
```

#### D-6. 빈 패키지 삭제

profile/, avatarresource/ 디렉터리가 비었으면 삭제.

---

## 하지 않는 것과 근거

### auth 모듈 분리

| 항목 | 내용 |
|------|------|
| **현재 결합** | auth→user 직접 import 존재 (`MemberSignService.getMemberOrCreate`) |
| **분리 비용** | `MemberAuthPort` 신규 Port 정의 + Adapter 구현 필요 |
| **이점** | 25파일 분리 — 독립 빌드 이점 미미 |
| **판단** | JwtService, SecurityConfig가 이미 common에 있어서 auth 모듈의 실체가 OAuth 클라이언트 + 로그인 플로우뿐. 비용 대비 가치 부족. |

### admin 모듈 분리

| 항목 | 내용 |
|------|------|
| **현재 결합** | party 49, user 23, playlist 6건 직접 import (Port 없음) |
| **분리 비용** | 최소 5개 Port 신규 정의 필요 (`PartyManagementPort`, `UserManagementPort` 등) |
| **이점** | admin 변경 시 다른 모듈 영향 없음 (이미 역방향 의존 0건) |
| **판단** | admin은 관리 콘솔 — 의도적으로 내부를 알아야 하는 도메인. Port 추상화는 인위적 간접층일 뿐. |

### party 내부 분리 (crew/playback/chat)

| 항목 | 내용 |
|------|------|
| **현재 상태** | 162파일, 단일 Aggregate (JPA FK + 트랜잭션 원자성) |
| **분리 비용** | 트랜잭션 경계 재설계, eventual consistency 도입 필요 |
| **이점** | 독립 배포 가능성 (현재 불필요) |
| **판단** | PartyroomData-CrewData-DjData-PlaybackData가 FK로 연결된 단일 Aggregate. 분리 시 데이터 정합성 보장 비용이 현재 규모에서 과잉. |

### common 내부 분리 (shared-kernel vs infra-config)

| 항목 | 내용 |
|------|------|
| **현재 상태** | 51파일. UserId/Duration (shared kernel) + RedisConfig/SecurityConfig (infra) 혼재 |
| **분리 비용** | 2개 모듈로 분리 + 각각의 build.gradle 관리 |
| **이점** | shared kernel의 순수성 보장 |
| **판단** | 51파일을 두 모듈로 쪼개면 모듈 수만 늘고 실질적 의존성 차단 효과 없음. common 전체가 이미 역방향 import 0건. |

### realtime 모듈 변경

| 항목 | 내용 |
|------|------|
| **현재 상태** | 10파일, 도메인 import 0건, Port 인터페이스만 정의 |
| **판단** | 변경할 이유 없음. 현재 상태가 이상적. |

---

## 변경 규모 요약

| Phase | 작업 | 코드 변경 | 리스크 |
|-------|------|-----------|--------|
| **A: common 모듈 승격** | build.gradle 분리, 파일 이동 | 없음 | 최저 |
| **B: playlist 모듈 분리** | build.gradle 분리, 파일 이동 | 최소 (Port 이미 존재) | 낮음 |
| **C: api → app 개명** | 디렉터리 rename, settings.gradle | 없음 | 최저 |
| **D: profile+avatar → user 병합** | 파일 이동, import 수정 (~50건) | 중간 (로직 불변) | 중간 |

**권장 실행 순서:** A → B → C → D

Phase A~C는 코드 변경이 거의 없어 안전하다.
Phase D는 import 경로 변경이 수반되므로 별도 커밋으로 분리한다.

---

## 최종 상태 (예상)

| 모듈 | 파일 수 | 의존 |
|------|---------|------|
| **common** | ~51 | 없음 |
| **realtime** | 10 | 없음 |
| **playlist** | ~46 | common |
| **app** | ~300 | common, realtime, playlist |

| 도메인 (app 내) | 파일 수 | 비고 |
|-----------------|---------|------|
| party | 162 | 변경 없음 |
| user | ~91 | profile(22) + avatarresource(4) 병합 |
| auth | 25 | 변경 없음 |
| admin | 27 | 변경 없음 |
| bootstrap | 1 | 변경 없음 |

---

*작성: 2026-02-20*
