# PFPlay Backend - DDD Cleanup Refactoring Plan

> **작성일**: 2026-02-18 (갱신: 2026-02-20)
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
Phase 4 (Gradle 멀티모듈 분리)     ✅ 완료 (2026-02-20)
    ↓
Phase 5 (QueryDSL 정비)           ✅ 완료 (2026-02-20)
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

# 컴파일 확인 (루트에서)
./gradlew :api:compileJava
./gradlew :realtime:compileJava

# 전체 테스트
./gradlew :api:test

# 특정 도메인 테스트
./gradlew :api:test --tests "com.pfplaybackend.api.party.*"
./gradlew :api:test --tests "com.pfplaybackend.api.playlist.*"

# 빠른 빌드 확인 (테스트 제외)
./gradlew :api:build -x test

# 클린 빌드
./gradlew clean :api:test
```

---

**문서 최종 업데이트**: 2026-02-20
