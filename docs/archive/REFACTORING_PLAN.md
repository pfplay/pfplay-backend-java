# PFPlay Backend - DDD Cleanup Refactoring Plan

> **작성일**: 2026-02-18 (갱신: 2026-02-21)
> **브랜치**: `refactor/ddd-cleanup` (`chore/playlist-tests` 에서 분기)
> **목표**: 현재 구조를 DDD + 헥사고널 아키텍처 기반의 모듈러 모놀리스로 개편
> **상태**: Phase 0~8 + 모듈 재구조화 + ERD 정규화(Phase ERD-1~6) **전체 완료** — 5모듈, 420 소스파일, 47 테스트파일(231 메서드)

---

## 리팩토링 이전 상태 (참고용)

### 코드베이스 기반 (리팩토링 시작 시점)
- `chore/playlist-tests` 기반 (main 대비 184커밋 ahead)
- 컴파일 성공, 테스트 18개 파일 전부 통과
- 신규 패키지: `profile/` (23개 파일), `avatarresource/` (5개 파일)

### 당시 핵심 문제점 (전부 해결됨)

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

## Phase 0: 기반 정비 ✅ 완료

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

## Phase 1: Data 엔티티 통합 ✅ 완료

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

## Phase 2: Aggregate 분리 ✅ 완료

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

## Phase 3: 헥사고널 패키지 구조 개편 ✅ 완료

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

## Phase 4: Gradle 멀티모듈 분리 (STOMP 유지) ✅ 완료 (2026-02-20)

### 목표
liveconnect 패키지의 WebSocket 인프라를 독립 Gradle 모듈(`realtime`)로 분리하여 도메인 코드와 컴파일 타임 경계를 만든다. STOMP 프로토콜은 유지.

### 실제 수행 내역

#### 4-1. Gradle 멀티모듈 구조 설정

```
pfplay-backend-java/           (새 Gradle root)
├── settings.gradle             # rootProject.name = 'pfplay', include 'api', 'realtime'
├── build.gradle                # 공유 설정 (Java 17, Lombok, Spring DM BOM)
├── gradlew, gradlew.bat, gradle/   (api/에서 루트로 이동)
├── api/
│   ├── build.gradle            # implementation project(':realtime') + 도메인 의존성
│   └── src/
└── realtime/
    ├── build.gradle            # websocket + spring-security-web만
    └── src/main/java/com/pfplaybackend/realtime/
```

- `api/settings.gradle` 삭제 → 루트 `settings.gradle`로 교체
- `api/gradlew`, `api/gradlew.bat`, `api/gradle/` → 루트로 이동
- 루트 `build.gradle` 신규 (subprojects 공유 설정)
- `api/build.gradle` 수정 (공유 설정 제거, `implementation project(':realtime')` 추가)

#### 4-2. realtime 모듈 생성 (10파일, 도메인 import 제로)

**Port 인터페이스 (신규):**
- `realtime/.../port/WebSocketAuthPort.java` — 인증 추상화 (`Optional<String> extractUserId`)
- `realtime/.../port/SessionCachePort.java` — 세션 캐시 추상화 (`String userId` 기반, UserId 제거)

**WebSocket 인프라 (liveconnect에서 이동+수정):**

| 원본 (api/liveconnect/) | 이동 (realtime/) | 변경 사항 |
|---|---|---|
| `websocket/WebSocketConfig.java` | `config/WebSocketConfig.java` | `JwtHandshakeInterceptor` → `WebSocketHandshakeInterceptor` (WebSocketAuthPort 기반) |
| `websocket/SimpMessageSender.java` | `sender/SimpMessageSender.java` | 패키지명만 변경 |
| `websocket/HeartbeatController.java` | `controller/HeartbeatController.java` | SimpMessageSender import 경로 변경 |
| `websocket/interceptor/JwtHandshakeInterceptor.java` | `interceptor/WebSocketHandshakeInterceptor.java` | `JwtCookieValidator` 제거 → `WebSocketAuthPort` 사용 |
| `websocket/cache/SessionCacheManager.java` | 삭제 (SessionCachePort로 대체) | — |
| `websocket/exception/SessionException.java` | 삭제 (AuthenticationServiceException 직접 사용) | — |
| `websocket/event/listener/ConnectionEventListener.java` | `event/ConnectionEventListener.java` | 패키지만 변경 |
| `websocket/event/listener/DisconnectionEventListener.java` | `event/DisconnectionEventListener.java` | `SessionCacheManager` → `SessionCachePort` |
| `websocket/event/listener/SubscriptionEventListener.java` | `event/SubscriptionEventListener.java` | `SessionCacheManager`+`UserId`+`ExceptionCreator` → `SessionCachePort`+`String`+`AuthenticationServiceException` |
| `websocket/event/listener/UnsubscriptionEventListener.java` | `event/UnsubscriptionEventListener.java` | 동일 패턴 |

#### 4-3. api 모듈 — Port 구현체 등록

**WebSocketAuthPort 구현 (신규):**
- `api/.../common/adapter/realtime/JwtWebSocketAuthAdapter.java` — `JwtCookieValidator` 위임

**SessionCachePort 구현 (기존 수정):**
- `api/.../party/application/service/cache/PartyroomSessionCacheManager.java`
  - `implements SessionCacheManager` → `implements SessionCachePort`
  - `saveSessionCache(String, UserId, String)` → `saveSessionCache(String, String, String)` (내부에서 `UserId.fromString()` 변환)

**SimpMessageSender import 경로 변경 (15파일):**
- `common/config/redis/RedisConfig.java`
- `party/adapter/in/listener/` 하위 12개 토픽 리스너
- `party/adapter/in/listener/ChatTopicListener.java` (이동된 파일)
- `party/application/service/chat/PartyroomChatService.java` (`SessionCachePort`로도 변경)

#### 4-4. liveconnect 패키지 정리 → party로 통합

| 원본 | 이동 |
|------|------|
| `liveconnect/chat/adapter/in/stomp/PartyroomChatController.java` | `party/adapter/in/stomp/PartyroomChatController.java` |
| `liveconnect/chat/adapter/in/listener/ChatTopicListener.java` | `party/adapter/in/listener/ChatTopicListener.java` |
| `liveconnect/chat/adapter/in/listener/message/OutgoingGroupChatMessage.java` | `party/adapter/in/listener/message/OutgoingGroupChatMessage.java` |

- `admin/application/service/ChatSimulationService.java` — OutgoingGroupChatMessage import 경로 변경
- `liveconnect/` 패키지 완전 삭제

### 수치 요약

| 항목 | 수량 |
|------|------|
| 신규 생성 (realtime 모듈) | 10파일 |
| 신규 생성 (api 모듈) | 1파일 (JwtWebSocketAuthAdapter) |
| liveconnect → party 이동 | 3파일 |
| import 경로 수정 | 15파일 |
| 삭제 | liveconnect 전체 (13파일) + api/settings.gradle |
| Gradle 구조 변경 | 5파일 (settings, build x2, gradlew 이동) |

### 빌드 명령어 변경

```bash
# Before (api 디렉토리에서)
cd api && ./gradlew compileJava
cd api && ./gradlew test

# After (루트에서)
JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11" ./gradlew :api:compileJava
JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11" ./gradlew :api:test
JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11" ./gradlew :realtime:compileJava
```

### 완료 기준
- [x] `realtime` 모듈이 독립적으로 컴파일됨 (도메인 import 제로)
- [x] `api` → `realtime` 단방향 의존 (역방향 없음)
- [x] `api` 모듈에서 `liveconnect/` 패키지 완전 삭제
- [x] chat 파일이 `party/adapter/in/` 하위로 통합
- [x] 전체 테스트 통과 (0 failures)

---

## Phase 5: QueryDSL 정비 ✅ 완료 (2026-02-20)

### 목표
구조 변경 완료 후 QueryDSL 코드를 정비

### 실제 수행 내역

#### 5-1. MemberRepositoryImpl — fetchJoin 추가 + 중복 WHERE 제거
- `join` → `leftJoin().fetchJoin()` (profileData, activityDataMap)
- 중복 WHERE 조건 2개 제거 (join이 보장하는 `qActivityData.userId`, `qProfileData.userId`)

#### 5-2. GuestRepositoryImpl — fetchJoin 추가 + 중복 WHERE 제거
- `join` → `leftJoin().fetchJoin()` (profileData)
- 중복 WHERE 조건 1개 제거 (`qProfileData.userId.eq(qGuestData.userId)`)

#### 5-3. PartyroomRepositoryImpl — cross-join 제거
- `getRecentPlaybackHistory()`: `from(qPartyroomData, qPlaybackData)` cross-join → `from(qPlaybackData)` 단일 테이블 쿼리
- `PlaybackData.partyroomId`가 `@Embedded` VO이므로 PartyroomData 테이블 불필요

#### 5-4. PlaylistRepositoryImpl — 필드 alias 통일
- `findAllByUserId`의 `.as("memberCount")` → `.as("musicCount")` (PlaylistSummary 필드명과 일치)

### 수정하지 않은 항목
- `getCrewDataByPartyroomId()` 클라이언트 사이드 그룹핑 — QueryDSL 표준 패턴
- `TrackRepositoryImpl` 2-query 페이지네이션 — `PageImpl` 표준 패턴
- `PlaylistRepositoryImpl` leftJoin 구문 — explicit join이 정확

### 완료 기준
- [x] `new JPAQueryFactory(em)` 패턴 제거 (Phase 0에서 처리 완료)
- [x] N+1 위험 쿼리 제거 (fetchJoin 적용)
- [x] 불필요한 cross-join 제거
- [x] 필드 alias 일관성 수정
- [x] 전체 테스트 통과

---

## Phase 6: DDD Tactical Patterns ✅ 완료 (2026-02-20)

### 목표
Value Object, Rich Model, Specification, Aggregate 경계, Domain Event 등 DDD 전술 패턴을 전면 적용

### 실제 수행 내역

#### 6-1. Value Object 도입 (`6763f84`)
- 원시값을 VO로 전환: `Duration`, `Score`, `Nickname`, `PlaybackTimeLimit`, `LinkDomain`
- JPA `@Converter` / `@Embedded` 적용 (DB 스키마 변경 없음)

#### 6-2. Specification / Policy 패턴 (`6763f84`)
- `PartyroomEntrySpecification`, `DjEnqueueSpecification`, `GradeAdjustmentSpecification`
- `PartyroomCreationPolicy`, `PlaylistCreationPolicy`
- 서비스에서 검증 로직을 Specification으로 추출

#### 6-3. Aggregate 경계 명확화 (`6763f84`)
- `PartyroomAggregateService` 신규: DJ 큐 조작(`removeDjFromQueue`, `rotateDjQueue`, `deactivatePlayback`) 중앙화
- `PlaybackDomainService` 및 5개 빈약 도메인 서비스 삭제

#### 6-4. Domain Event 도입 (`6763f84`)
- 9개 도메인 이벤트 생성, `@TransactionalEventListener(AFTER_COMMIT)` 기반
- 7개 응용 서비스에서 `RedisMessagePublisher` 직접 호출 → `ApplicationEventPublisher` 전환
- 도메인 ↔ Redis 인프라 디커플링 달성

#### 6-5. User JOINED 상속 + ProfileData 분해 (`42383da`)
- `UserAccountData` 추상 부모 + JOINED 상속 전략 (`MemberData`, `GuestData` 확장)
- `UserAccountRepository`로 다형 쿼리 지원
- `ProfileData`의 13+ 필드 → `Bio`, `AvatarSetting` `@Embeddable` VO로 분해 (DB 스키마 불변)

### 완료 기준
- [x] 주요 원시값이 VO로 전환됨
- [x] 검증 로직이 Specification/Policy로 분리됨
- [x] 도메인 이벤트로 인프라 디커플링됨
- [x] User 엔티티에 JOINED 상속 적용됨
- [x] 전체 테스트 통과

---

## Phase 7: 코드 품질 정비 ✅ 완료 (2026-02-20)

### 목표
데드 코드 제거, 네이밍 통일, 예외 아키텍처 정비, DTO Record 전환 등 전반적 코드 품질 향상

### 실제 수행 내역

#### 7-1. 데드 코드 제거 + 네이밍 통일 (`b83657c`, `b7d1622`)
- `GuestDomainService`, `MusicSearchDomainService`, 미사용 Avatar VO 5개 삭제
- **Music → Track 네이밍 통일**: 20+ 클래스/메서드 일괄 변경
- CQRS 위반 수정: `getFirstTrack()+rotateTrackOrder()`를 Query→Command 서비스로 이동
- 서비스 통합: `CrewInfoService` → `PartyroomInfoService`, `PartyroomInitializeService` → `PartyroomManagementService`

#### 7-2. @Transactional 통일 + 쿼리 최적화 (`f55d2ac`)
- `jakarta.transaction.Transactional` → Spring `@Transactional` (12파일)
- 조회 메서드에 `@Transactional(readOnly = true)` 추가
- `isEmptyPlaylist`: 페이지네이션 쿼리 → `existsByPlaylistDataId`로 교체

#### 7-3. 예외 아키텍처 정비 (`58416b3`, `8f30765`, `cfc07b1`)
- 리플렉션 기반 예외 생성(`InstanceCreator`) → Java 17 switch expression으로 교체
- 도메인 예외 enum에서 HTTP 예외 클래스 import 제거 (도메인/인프라 분리)
- `AdminException`(8개 상수), `AuthException`(2개 상수) 신규 생성
- Redis 리스너 12개: `throw RuntimeException` → `log.error + return`
- `System.out.println` → `log.warn` 교체

#### 7-4. DTO/Message Record 전환 (`6062151`)
- **51개 DTO/Message를 record로 전환** (Lombok 제거, 생성자 호출로 변경)
- Redis 리스너 13개 → `GroupBroadcastTopicListener`로 통합
- `docs/DTO_RECORD_POLICY.md` 문서 작성 (record vs class 판단 기준)

### 완료 기준
- [x] 데드 코드 제거 완료
- [x] Music → Track 네이밍 통일 완료
- [x] 예외 생성이 리플렉션 없이 switch expression 기반
- [x] 51개 DTO가 record로 전환됨
- [x] 전체 테스트 통과

---

## Gradle 모듈 재구조화 ✅ 완료 (2026-02-20)

> 별도 문서: `docs/MODULE_RESTRUCTURING_PLAN.md`

2모듈(api, realtime) → 5모듈(common, realtime, playlist, user, app) 전환.
Phase A(common 승격) → B(playlist 분리) → C(api→app 개명) → D(profile+avatarresource→user 병합) → E(user 모듈 분리).

---

## Phase 8: 구조 개선 + 테스트 안전망 ✅ 완료 (2026-02-20)

### 목표
조회 집약 로직 분리, 응답 구조 통일, 관용적 Java 패턴 적용, 테스트 커버리지 확충

### 실제 수행 내역

#### 8-1. partyview 패키지 추출 (`6448d9c`)
- Setup API의 크로스 도메인 조회 집약 로직을 `partyview` 패키지로 분리
- `PartyroomSetupQueryService`(107줄) 신규 → `DisplayInfoService` 대체
- 설정 관련 DTO(`CrewSetupDto`, `DisplayDto`, `ReactionDto`, `CurrentDjDto`) 재배치

#### 8-2. 테스트 안전망 구축 (`f10f7ae`)
- **13개 테스트 파일, 75개 테스트 메서드** 추가
- VO/Model 테스트, Domain Entity 테스트, Domain Service 테스트, Application Service 테스트
- 컨벤션: Korean `@DisplayName`, given/when/then, AssertJ

#### 8-3. 응답 구조 통일 (`3f40e72`)
- `ResponseEntity<?>` 와일드카드 → 타입 안전 제네릭 (24개 컨트롤러, ~44개 메서드)
- `ApiErrorResponse` record 신규 (ApiCommonResponse와 분리)
- 미래핑 엔드포인트 래핑, "OK" 문자열 → `ApiCommonResponse.ok()`
- `ExceptionResult` 삭제

#### 8-4. 관용적 Java 패턴 적용 (`9e1c9ba`)
- `Optional` 베스트 프랙티스: `isPresent+get` → `orElseThrow`/`orElseGet`
- PenaltyType if-chain → switch expression
- `ThreadLocalContext.getAuthContext()` 타입 안전 메서드 추가 (36개 unsafe cast 제거)
- `ReactionState` → record 전환
- CrewPenaltyService N+1 쿼리 수정 (배치 조회)

#### 8-5. 크루 조회 중복 제거 (`0b38a00`)
- `PartyroomInfoService`에 `getCrewOrThrow()`, `getMyActivePartyroomWithCrewOrThrow()` 추가
- 6개 서비스에서 ~25곳의 중복 repository+exception 패턴을 중앙 메서드로 교체

### 완료 기준
- [x] 조회 집약 로직이 partyview 패키지로 분리됨
- [x] 테스트 44파일 223메서드 (안전망 확보)
- [x] 모든 컨트롤러가 타입 안전 응답 사용
- [x] 중복 조회 패턴 ~25곳 제거
- [x] 전체 테스트 통과

---

## ERD 정규화 (Phase ERD-1~6) ✅ 완료 (2026-02-21)

### 목표
PARTYROOM God Table 해체, 3NF 정규화, 도메인 개념 명시화

### 실제 수행 내역

#### ERD-1: PARTYROOM_PLAYBACK 엔티티 + 재생 상태 분리 (`54c7225`)
- PARTYROOM에서 재생 관련 상태(`currentPlaybackId`, `isPlaybackActivated`, 현재 DJ)를 분리
- `PartyroomPlaybackData` 신규 엔티티 (1:1 관계, partyroom_id = PK)
- `PartyroomData`에서 4개 필드/메서드 제거 (`applyActivation`, `applyDeactivation`, `updatePlaybackId`, `isPlaybackActivated`)
- 현재 DJ 식별: DJ 큐 스캔 O(n) → `playbackState.isCurrentDj(crewId)` 필드 비교 O(1)
- `ActivePartyroomDto` 재설계: PARTYROOM + PARTYROOM_PLAYBACK JOIN 프로젝션
- 8개 서비스 파일, QueryDSL 2개 쿼리, 8개 테스트 파일 수정

#### ERD-2: DJ_QUEUE 엔티티 + 대기열 상태 분리 (`2a37d90`)
- PARTYROOM에서 `isQueueClosed`를 분리하여 대기열 개념 명시화
- `DjQueueData` 신규 엔티티 (1:1 관계, partyroom_id = PK)
- `PartyroomData`에서 3개 필드/메서드 제거 (`isQueueClosed`, `openQueue`, `closeQueue`, `validateQueueOpen`)
- `DjEnqueueSpecification`이 `DjQueueData` 파라미터를 직접 받도록 변경
- QueryDSL에 DJ_QUEUE JOIN 추가

#### ERD-3: PLAYBACK_AGGREGATION 엔티티 + 반응 집계 분리 (`d57254d`)
- PLAYBACK을 불변 이력으로 만들고, 가변 반응 카운터(`likeCount`, `dislikeCount`, `grabCount`)를 분리
- `PlaybackAggregationData` 신규 엔티티 (1:1 관계, playback_id = PK)
- `PlaybackData`에서 3개 필드/메서드 제거, PLAYBACK이 append-only 불변 테이블로 전환
- `PlaybackData`에 `@Index(partyroom_id)` 추가

#### ERD-4: DJ.userId 제거 — 3NF 정규화 (`0d1466a`)
- `DJ.userId`는 `crewId → CREW.userId`로 유도 가능한 이행 종속
- `DjData`에서 `userId` 필드 제거, `DjRepository`에서 userId 기반 메서드 → crewId 기반으로 대체
- `PlaybackManagementService`, `PlaybackInfoService`, `PartyroomInfoService`, `DjManagementService` 등 6개 서비스 변경
- 8개 테스트 파일 수정

#### ERD-5: CREW.authorityTier 제거 — 3NF 정규화 (`b666d14`)
- `CREW.authorityTier`는 `userId → USER_ACCOUNT.authorityTier`로 유도 가능한 이행 종속
- `CrewData`에서 `authorityTier` 필드 제거, `CrewDto`에서도 제거
- `UserProfileQueryPort.getAuthorityTier(UserId)` 메서드 추가로 런타임 조회
- `CrewGradeService`, `PartyroomInfoService`, `PartyroomAccessService` 등 서비스 변경
- QueryDSL 프로젝션에서 `authorityTier` 제거
- 6개 테스트 파일 수정

#### ERD-6: UNIQUE 제약조건 (`bb45d7d`)
- `CREW`: `UNIQUE(partyroom_id, user_id)` — 동일 파티룸에 동일 사용자 중복 방지
- `PLAYBACK_REACTION_HISTORY`: `UNIQUE(user_id, playback_id)` — 동일 재생에 중복 반응 방지
- `USER_ACTIVITY`: `UNIQUE(user_id, activity_type)` — 동일 활동 유형 중복 방지

### 변경 전후 비교

| 지표 | Before | After |
|------|--------|-------|
| 테이블 수 | 18 | 21 (+3) |
| 3NF 위반 | 2건 | 0건 |
| UNIQUE 제약 | 1개 | 4개 |
| PARTYROOM 컬럼 | 14 | 10 |
| PLAYBACK 불변성 | 불변+가변 혼합 | append-only |
| 현재 DJ 식별 | 큐 스캔 O(n) | 필드 비교 O(1) |

### 완료 기준
- [x] 3개 신규 엔티티 생성 (PARTYROOM_PLAYBACK, DJ_QUEUE, PLAYBACK_AGGREGATION)
- [x] 2개 이행 종속 제거 (DJ.userId, CREW.authorityTier)
- [x] 3개 UNIQUE 제약조건 추가
- [x] PARTYROOM 14→10 컬럼으로 축소
- [x] 전체 138 테스트 통과

---

## Phase 간 의존 관계

```
Phase 0 (기반 정비)               ✅ 완료
    ↓
Phase 1 (Data 엔티티 통합)         ✅ 완료
    ↓
Phase 2 (Aggregate 분리)           ✅ 완료
    ↓
Phase 3 (헥사고널 패키지 구조)      ✅ 완료
    ↓
Phase 4 (Gradle 멀티모듈 분리)     ✅ 완료
    ↓
Phase 5 (QueryDSL 정비)           ✅ 완료
    ↓
Phase 6 (DDD Tactical Patterns)   ✅ 완료
    ↓
Phase 7 (코드 품질 정비)           ✅ 완료
    ↓
모듈 재구조화 (Phase A~E)          ✅ 완료 (→ docs/MODULE_RESTRUCTURING_PLAN.md)
    ↓
Phase 8 (구조 개선 + 테스트)       ✅ 완료
    ↓
ERD 정규화 (Phase ERD-1~6)        ✅ 완료
  ERD-1 (PARTYROOM_PLAYBACK)
    ↓
  ERD-2 (DJ_QUEUE)
    ↓ (ERD-3, 4, 5는 상호 독립)
  ERD-3 (PLAYBACK_AGGREGATION)
  ERD-4 (DJ.userId 제거)
  ERD-5 (CREW.authorityTier 제거)
    ↓
  ERD-6 (UNIQUE 제약조건)
```

---

## 현재 코드베이스 수치 요약

| 항목 | 수치 |
|------|------|
| Gradle 모듈 | 5개 (common, realtime, playlist, user, app) |
| 소스 파일 | 420개 (common 51, realtime 10, playlist 47, user 91, app 221) |
| 테스트 파일 | 47개 (common 2, playlist 6, user 9, app 30) |
| 테스트 메서드 | 231개 |
| DTO record | 51개 |
| DB 테이블 | 21개 (3NF 달성, UNIQUE 제약 4개) |

---

## 빌드/테스트 명령어

```bash
# 환경 설정
export JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11"

# 컴파일 확인 (루트에서)
./gradlew :app:compileJava
./gradlew :realtime:compileJava
./gradlew :playlist:compileJava
./gradlew :user:compileJava

# 전체 테스트
./gradlew :common:test :playlist:test :user:test :app:test

# 특정 모듈 테스트
./gradlew :app:test --tests "com.pfplaybackend.api.party.*"
./gradlew :playlist:test

# 클린 빌드
./gradlew clean :app:test
```

---

**문서 최종 업데이트**: 2026-02-21
