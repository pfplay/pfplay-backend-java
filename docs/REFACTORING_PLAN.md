# PFPlay Backend - DDD Cleanup Refactoring Plan

> **작성일**: 2026-02-18 (갱신: 2026-02-19)
> **브랜치**: `refactor/ddd-cleanup` (`chore/playlist-tests` 에서 분기)
> **목표**: 현재 구조를 DDD + 헥사고널 아키텍처 기반의 모듈러 모놀리스로 개편

---

## 현재 상태 요약

### 코드베이스 기반
- `chore/playlist-tests` 기반 (main 대비 184커밋 ahead)
- 컴파일 성공, 테스트 18개 파일 전부 통과
- 신규 패키지: `profile/` (23개 파일), `avatarresource/` (5개 파일)

### 핵심 문제점

1. **Domain Model ↔ Data Entity 이중 구조의 과도한 변환 비용**
   - 도메인 모델: party(4), user(8), playlist(1) = 13개
   - Data 엔티티: party(4), user(5+), playlist(1) = 10+개
   - 전용 컨버터 4개 (party 도메인)
   - `.toDomain()` / `.toData()` 호출 다수, 3단계 변환 패턴 반복
   - JPA 더티 체킹, 1차 캐시, 연관관계 관리 등 ORM 이점을 구조적으로 포기

2. **반복 보일러플레이트**
   ```java
   PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
   Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
   if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
   PartyroomDataDto partyroomDataDto = optional.get();
   PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
   Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
   ```
   이 패턴이 파티 서비스 다수 파일에 반복 등장

3. **Aspect 프로덕션 미달 품질**
   - `@Before` + `@After`로 ThreadLocal 관리 → 예외 시 메모리 누수 위험 (`@Around` + try-finally 필요)
   - `PartyContext`, `PlaylistContext`, `UserContext` 3개 클래스가 100% 동일
   - `instanceof` 체크 없이 unsafe cast

4. **Partyroom 어그리게잇 비대화**
   - Partyroom이 Crew(max 50) + DJ 컬렉션을 모두 보유 (`@OneToMany CascadeType.ALL`)
   - 크루 1명 등급 변경에도 전체 어그리게잇 로드
   - CrewRepository, DjRepository 없음 — 독립적 접근 불가

5. **크로스 도메인 순환 의존**
   - peer/proxy 서비스 로케이터 패턴 (8개 파일)
   - admin → party/playlist/user/avatar (다수 import)

6. **QueryDSL 품질 이슈**
   - `QueryDslConfig` Bean은 등록되어 있으나, **5개 RepositoryImpl에서 미사용**
   - 매 메서드마다 `new JPAQueryFactory(em)` 생성
   - N+1 위험 쿼리 다수

---

## Phase 0: 기반 정비

### 목표
어떤 리팩토링을 하든 방해가 되는 버그/안티패턴을 먼저 제거

### 0-1. Aspect 수정 (3개 파일)

**수정 대상:**
- `api/src/main/java/com/pfplaybackend/api/playlist/application/aspect/PlaylistContextAspect.java`
- `api/src/main/java/com/pfplaybackend/api/party/application/aspect/PartyContextAspect.java`
- `api/src/main/java/com/pfplaybackend/api/user/application/aspect/UserContextAspect.java`

**변경 내용:**
1. `@Before` + `@After` → `@Around` + try-finally
2. `!authentication.getPrincipal().equals("anonymousUser")` → `authentication instanceof CustomJwtAuthenticationToken`
3. 3개 Context 클래스를 1개 공통 클래스로 통합

**신규 파일:**
- `api/src/main/java/com/pfplaybackend/api/common/aspect/context/AuthContext.java`
  ```java
  @Getter @AllArgsConstructor
  public class AuthContext {
      UserId userId;
      AuthorityTier authorityTier;
      public static AuthContext create(CustomJwtAuthenticationToken token) {
          return new AuthContext(token.getUserId(), token.getAuthorityTier());
      }
  }
  ```

**삭제 파일:**
- `PlaylistContext.java`, `PartyContext.java`, `UserContext.java`

**영향 받는 서비스:**
캐스팅 변경: `(PlaylistContext) ThreadLocalContext.getContext()` → `(AuthContext) ThreadLocalContext.getContext()`

**테스트 영향:**
- `PlaylistCommandServiceTest.java` — `mock(PlaylistContext.class)` → `mock(AuthContext.class)`
- `TrackCommandServiceTest.java` — 동일
- `PartyroomAccessServiceTest.java` — `mock(PartyContext.class)` → `mock(AuthContext.class)`
- `PartyroomAccessServiceDjQueueChangeTest.java` — 동일
- `DjManagementServiceDjQueueChangeTest.java` — 동일
- `PartyroomInfoServiceIsRegisteredTest.java` — 동일
- `PartyroomInfoServiceGetDjsTest.java` — 동일

### ~~0-2. JPAQueryFactory 주입 수정~~ ✅ 완료

`QueryDslConfig` Bean은 이미 존재 (`common/config/jpa/QueryDslConfig.java`).
각 RepositoryImpl에서 `new JPAQueryFactory(em)` → 생성자 주입으로 변경만 필요.

**수정 대상 (5개 파일):**
- `PartyroomRepositoryImpl.java`
- `PlaylistRepositoryImpl.java`
- `TrackRepositoryImpl.java`
- `MemberRepositoryImpl.java`
- `GuestRepositoryImpl.java`

### 검증
```bash
export JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11"
cd api && ./gradlew compileJava && ./gradlew test
```

### 완료 기준
- [x] 3개 Aspect가 `@Around` + try-finally 사용
- [x] `AuthContext` 단일 클래스로 통합
- [x] JPAQueryFactory가 Bean 주입으로 사용 (5개 RepositoryImpl)
- [x] 전체 테스트 통과

---

## Phase 1: Data 엔티티 통합

### 목표
Domain Model 비즈니스 로직을 Data 엔티티로 병합하고, Converter를 제거하여 JPA 이점을 구조적으로 활용

### 원칙
- Data 엔티티(`*Data.java`)가 비즈니스 메서드를 직접 보유
- `domainmodel/` 디렉토리 제거
- `converter/` 디렉토리 제거
- DTO 변환 보일러플레이트 제거
- 서비스에서 `repository.findById()` → 바로 비즈니스 메서드 호출

### 1-1. Party 도메인 (가장 큰 작업)

**병합 대상:**

| Domain Model | → Data Entity | 비즈니스 메서드 수 |
|---|---|---|
| `Partyroom.java` (259줄) | → `PartyroomData.java` | 30+ 메서드 |
| `Crew.java` | → `CrewData.java` | 6 메서드 |
| `Dj.java` | → `DjData.java` | 3 메서드 |
| `Playback.java` | → `PlaybackData.java` | 5 메서드 |

**삭제할 파일:**
- `party/domain/entity/domainmodel/Partyroom.java`
- `party/domain/entity/domainmodel/Crew.java`
- `party/domain/entity/domainmodel/Dj.java`
- `party/domain/entity/domainmodel/Playback.java`
- `party/domain/entity/converter/PartyroomConverter.java`
- `party/domain/entity/converter/CrewConverter.java`
- `party/domain/entity/converter/DjConverter.java`
- `party/domain/entity/converter/PlaybackConverter.java`
- `party/application/dto/base/PartyroomDataDto.java` (존재 시)
- `party/application/dto/base/CrewDataDto.java` (존재 시)
- `party/application/dto/base/DjDataDto.java` (존재 시)

**서비스 변경 패턴:**
```java
// BEFORE (현재)
PartyroomDataDto dto = partyroomRepository.findPartyroomDto(partyroomId).orElseThrow(...);
PartyroomData data = partyroomConverter.toEntity(dto);
Partyroom partyroom = partyroomConverter.toDomain(data);
partyroom.addNewCrew(userId, tier, grade);
partyroomRepository.save(partyroomConverter.toData(partyroom));

// AFTER (목표)
PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
    .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
partyroom.addNewCrew(userId, tier, grade);
partyroomRepository.save(partyroom);
```

**테스트 영향:**
- `PartyroomDjQueueTest.java` — `domainmodel.Partyroom` → `data.PartyroomData`
- `PartyroomRotateDjsTest.java` — 동일
- `PlaybackParseDurationTest.java` — `domainmodel.Playback` → `data.PlaybackData`
- Party 서비스 테스트 5개 — converter mock 제거

### 1-2. Playlist 도메인

**병합 대상:**

| Domain Model | → Data Entity |
|---|---|
| `Playlist.java` | → `PlaylistData.java` |

**삭제:** `playlist/domain/entity/domainmodel/Playlist.java`

### 1-3. User 도메인

**병합 대상:**

| Domain Model | → Data Entity |
|---|---|
| `Member.java` | → `MemberData.java` |
| `Guest.java` | → `GuestData.java` |
| `Activity.java` | → `ActivityData.java` |
| `User.java` (추상 베이스) | 삭제 또는 병합 |
| `Profile.java` | 검토 필요 (profile 패키지에 `ProfileData` 존재) |
| `AvatarBodyResource.java` | → `AvatarBodyResourceData.java` |
| `AvatarFaceResource.java` | → `AvatarFaceResourceData.java` |
| `AvatarIconResource.java` | → `AvatarIconResourceData.java` |

> **NOTE**: `profile/` 패키지가 별도 존재하며 `ProfileData.java`를 보유. User 도메인의 `Profile.java` domainmodel과의 관계를 정리해야 함.

### 검증
```bash
cd api && ./gradlew compileJava && ./gradlew test
```

### 완료 기준
- [x] `domainmodel/` 디렉토리가 모든 도메인에서 제거됨
- [x] `converter/` 디렉토리가 제거됨
- [x] 서비스에서 3단계 변환 보일러플레이트가 없음
- [x] Data 엔티티가 비즈니스 메서드를 직접 보유
- [x] 중간 DTO(`PartyroomDataDto` 등) 제거
- [x] 전체 테스트 통과

---

## Phase 2: Aggregate 분리

### 목표
비대한 Partyroom 어그리게잇에서 Crew, DJ를 독립 어그리게잇으로 분리

### 2-1. CrewRepository 추출

**신규 파일:**
- `party/infrastructure/repository/PartymemberRepository.java`
  ```java
  public interface PartymemberRepository extends JpaRepository<CrewData, Long> {
      List<CrewData> findByPartyroomDataIdAndIsActiveAndIsBanned(...);
      Optional<CrewData> findByPartyroomDataIdAndUserId(...);
  }
  ```

### 2-2. DjRepository 추출

**신규 파일:**
- `party/infrastructure/repository/DjRepository.java`
  ```java
  public interface DjRepository extends JpaRepository<DjData, Long> {
      List<DjData> findByPartyroomDataIdAndIsQueuedOrderByOrderNumber(...);
      Optional<DjData> findByPartyroomDataIdAndCrewId(...);
  }
  ```

### 2-3. Partyroom에서 컬렉션 제거

`PartyroomData`에서 `@OneToMany crewDataSet`, `@OneToMany djDataSet` 제거.
Crew/DJ는 독립 Repository를 통해서만 접근.

### 완료 기준
- [x] `CrewRepository`, `DjRepository` 존재
- [x] `PartyroomData`에서 Crew/DJ 컬렉션 제거
- [x] 크루 1명 등급 변경 시 Partyroom 전체 로드 없음
- [x] 전체 테스트 통과

---

## Phase 3: 헥사고널 패키지 구조 개편

### 목표
각 도메인을 port/adapter 구조로 정리하고, 도메인 간 인터페이스를 명확히 정의

### 목표 패키지 구조 (각 도메인 공통)
```
com.pfplaybackend.api.{domain}/
├── application/
│   ├── port/
│   │   ├── in/              # 유스케이스 인터페이스
│   │   └── out/             # 아웃바운드 포트
│   └── service/             # 유스케이스 구현
├── domain/
│   ├── entity/              # JPA 엔티티 (비즈니스 메서드 포함)
│   ├── service/             # 도메인 서비스
│   ├── enums/
│   ├── exception/
│   └── value/               # 값 객체
├── adapter/
│   ├── in/
│   │   └── web/             # REST Controllers
│   └── out/
│       ├── persistence/     # Repository 구현
│       ├── redis/           # Redis 관련
│       └── external/        # 외부 API
└── dto/                     # 도메인 간 공유 DTO
```

### 3-1. 크로스 도메인 인터페이스 정의

**peer/proxy 패턴 (8개 파일) → port 인터페이스로 대체:**
- `MusicQueryPeerService` → `PlaylistQueryPort`
- `GrabMusicPeerService` → `PlaylistCommandPort`
- `UserProfilePeerService` → `UserProfileQueryPort`
- `UserActivityPeerService` → `UserActivityPort`
- 대응하는 4개 Proxy 파일 제거

### 3-2. 파일 이동 매핑

| 현재 위치 | 이동 위치 |
|-----------|----------|
| `{domain}/presentation/` 또는 `{domain}/interfaces/api/rest/` | `{domain}/adapter/in/web/` |
| `{domain}/infrastructure/repository/` 또는 `{domain}/repository/` | `{domain}/adapter/out/persistence/` |
| `party/application/peer/` | 삭제 → port 인터페이스 |
| `party/application/proxy/` | 삭제 → port 인터페이스 |

### 3-3. Admin 도메인 의존성 정리

각 도메인이 제공하는 port를 통해서만 접근하도록 변경.

### 완료 기준
- [x] 각 도메인이 `port/in`, `port/out` 인터페이스를 보유
- [x] `peer/`, `proxy/` 패키지 제거
- [x] presentation 계층이 `adapter/in/web/`으로 이동
- [x] infrastructure 계층이 `adapter/out/`으로 이동
- [x] 전체 테스트 통과

### Phase 3 후속: 헥사고널 네이밍 정리 (2026-02-19 완료)

Phase 3 이후 남아있던 레거시 구조 잔재를 일괄 정리:

**playlist:**
- `serach/` 오타 → `search/` 수정 (MusicSearchController)
- 미사용 `config/external/YoutubeService{,Impl}` 삭제
- 테스트 파일 위치 `presentation/` → `adapter/in/web/` 이동

**auth:**
- `auth/enums/` → `auth/domain/enums/`
- `auth/validation/` → `auth/adapter/in/web/validation/`
- `auth/dto/` → `adapter/in/web/dto/` + `application/dto/` 재배치
- `auth/application/store/StateStore` → `application/port/out/StateStorePort` (port/adapter 패턴)
- `auth/application/store/RedisStateStore` → `adapter/out/persistence/RedisStateStoreAdapter`
- `auth/config/WebClientConfig` → `adapter/out/external/config/`

**admin:**
- `admin/util/NicknameGenerator` → `admin/application/util/`

**테스트 파일 위치 수정:**
- `profile/presentation/` → `profile/adapter/in/web/` (UpdateMyBioRequestTest)
- `party/interfaces/api/rest/` → `party/adapter/in/web/` (PartyroomAccessControllerLinkTest)

**빈 레거시 디렉토리 전체 삭제** (40+ 디렉토리: `presentation/`, `interfaces/`, `infrastructure/`, `repository/`, `client/` 등)

### party 도메인 분리 검토 (2026-02-19 결론: 불필요)

user/profile처럼 party를 partyroom/crew/dj/playback으로 최상위 분리할지 검토함.

**분리하지 않는 이유:**
1. **JPA hard FK**: CrewData, DjData가 `@ManyToOne`으로 PartyroomData에 직접 연결
2. **서비스 교차 의존**: PlaybackManagementService가 4개 Repository 전부 사용, DjManagementService가 3개 사용
3. **트랜잭션 원자성**: 퇴장/강퇴/DJ등록 등 핵심 유스케이스가 Crew+DJ+Playback을 단일 트랜잭션으로 조작
4. **라이프사이클 동일**: 모두 "파티룸 세션"이라는 하나의 라이프사이클에 귀속

→ party는 단일 Aggregate (PartyroomData = Aggregate Root)로 유지. 내부 `application/dto/crew/`, `dto/dj/`, `dto/playback/` 하위 패키지로 개념 구분하는 현재 수준이 적정.

---

## Phase 4: 채팅 모듈 WebFlux 분리

### 목표
liveconnect 도메인을 별도 Gradle 모듈로 분리하고 WebFlux 기반으로 전환

### 현재 상태
`api/src/main/java/com/pfplaybackend/api/liveconnect/` 안에 chat + websocket 패키지가 존재.
STOMP 기반 WebSocket + Redis pub/sub 구조.

### 4-1. Gradle 멀티 모듈 구성

```
pfplay-backend-java/
├── api/                    # 기존 Servlet 기반 모듈
├── chat/                   # 새로운 WebFlux 모듈
│   └── src/main/java/com/pfplaybackend/chat/
├── common/                 # 공유 모듈 (DTO, Value Objects)
└── settings.gradle         # include 'api', 'chat', 'common'
```

### 4-2. WebFlux 전환 대상

| 현재 (Blocking) | 목표 (Reactive) |
|---|---|
| `SimpMessagingTemplate` | `WebSocketHandler` + `WebSocketSession` |
| `RedisTemplate` | `ReactiveRedisTemplate` |
| `@MessageMapping` (STOMP) | Reactor Netty WebSocket |
| `ChatTopicListener` (blocking) | `ReactiveRedisOperations.listenTo()` |

### 완료 기준
- [ ] `chat` 모듈이 독립적으로 빌드됨
- [ ] WebSocket 연결이 WebFlux 기반으로 동작
- [ ] `api` 모듈에서 liveconnect 패키지 제거
- [ ] 전체 테스트 통과

---

## Phase 5: QueryDSL 정비

### 목표
구조 변경 완료 후 QueryDSL 코드를 정비

### 5-1. N+1 쿼리 수정
- `PartyroomRepositoryImpl.getCrewDataByPartyroomId()`: 전체 메모리 로드 → 2-query 전략
- Phase 2에서 Partyroom 컬렉션 제거되므로 일부 메서드 불필요해짐

### 5-2. fetchJoin 적용
남아있는 컬렉션 조인에 `.fetchJoin()` 명시

### 5-3. 비효율 쿼리 개선
- `MemberRepositoryImpl`: 중복 WHERE 조건 제거
- `TrackRepositoryImpl`: count 쿼리 최적화

### 완료 기준
- [ ] `new JPAQueryFactory(em)` 패턴이 코드베이스에 없음 (Phase 0에서 처리)
- [ ] N+1 위험 쿼리 제거
- [ ] 불필요한 메모리 로드 패턴 제거
- [ ] 전체 테스트 통과

---

## Phase 간 의존 관계

```
Phase 0 (기반 정비)
    ↓
Phase 1 (Data 엔티티 통합)     ← 가장 큰 작업, Phase 0 완료 필수
    ↓
Phase 2 (Aggregate 분리)       ← Phase 1 완료 후에만 가능
    ↓
Phase 3 (헥사고널 패키지 구조)  ← Phase 2와 병행 가능하나 순차 권장
    ↓
Phase 4 (채팅 WebFlux 분리)    ← Phase 3 완료 후 권장
    ↓
Phase 5 (QueryDSL 정비)        ← 마지막, 구조 안정화 후
```

---

## 작업 시 주의사항

1. **각 Phase 완료 후 반드시 커밋** — 롤백 가능한 단위 유지
2. **Phase 1이 가장 위험** — 모든 서비스에 영향. 도메인별로 나눠서 진행 (party → playlist → user 순)
3. **테스트 우선** — 기존 테스트(18개 파일)가 통과하는 상태를 항상 유지
4. **컴파일 먼저** — 대규모 변경 시 `compileJava` 먼저 확인 후 테스트
5. **한 번에 한 도메인** — Phase 1에서 party, playlist, user를 동시에 바꾸지 말 것
6. **신규 패키지 주의** — `profile/`, `avatarresource/` 패키지도 리팩토링 대상에 포함

---

## 빌드/테스트 명령어

```bash
# 환경 설정
export JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11"

# 컴파일 확인
cd api && ./gradlew compileJava

# 전체 테스트
cd api && ./gradlew test

# 특정 도메인 테스트
cd api && ./gradlew test --tests "com.pfplaybackend.api.party.*"
cd api && ./gradlew test --tests "com.pfplaybackend.api.playlist.*"

# 빠른 빌드 확인 (테스트 제외)
cd api && ./gradlew build -x test
```

---

**문서 최종 업데이트**: 2026-02-19
