# QA 미해결 이슈 분석 및 수정 계획서

## Context
QA에서 발견된 26건의 이슈 중 미해결 12건(시작 전 6, 진행 중 3, 재검수 3)에 대해 백엔드 코드를 탐색하고, 각 이슈의 근본 원인과 구체적인 수정 방법을 정리한 문서입니다. 종속성을 고려하여 작업 순서를 결정합니다.

---

## 종속성 그래프 및 작업 순서

```
[PHASE 1] 기반 버그 수정 (다른 이슈의 전제 조건)
  ├── TASK-01: rotateDjs() stream 미실행 ──→ TASK-04, TASK-05, TASK-13에 영향
  ├── TASK-02: CookieUtil.deleteCookie() 속성 누락 ──→ TASK-03 해결
  └── TASK-03: 캐시 삭제 후 로그인 (TASK-02에 종속)

[PHASE 2] 핵심 기능 버그 수정
  ├── TASK-04: dequeueDj() 무조건 skipBySystem (TASK-01 선행 필요) ──→ TASK-14에 영향
  ├── TASK-05: DJing 중 곡 추가 미반영 (TASK-01 선행 필요)
  └── TASK-06: DJ 새로고침 시 아바타 사라짐

[PHASE 3] 중간 우선순위 수정
  ├── TASK-07: OAuthUrlService → RedisStateStore 전환
  ├── TASK-08: 파티룸 간 직접 이동 시 자동 exit
  └── TASK-09: 프로필 introduction 컬럼 길이 조정

[PHASE 4] 낮은 우선순위 / 신규 기능
  ├── TASK-10: 곡 검색 에러 (platform @NotNull)
  ├── TASK-11: duration 파싱 다양한 포맷 지원
  └── TASK-12: 아바타 좌표 DB 마이그레이션

[PHASE 5] 재검수 필요 항목
  ├── TASK-13: 곡 전환 시 리액션 카운트 미갱신 (TASK-01 선행 필요)
  ├── TASK-14: Mod 이상 DJ 대기열 관리 미구현 (TASK-04 선행 필요)
  └── TASK-15: 파티룸 링크 비로그인 입장 시 메인 홈 노출 (FE 협업 필요)
```

> **참고**: "플레이리스트 간 곡 이동 기능"은 신규 기능 개발이므로 이 문서에서 제외합니다. 별도 기능 명세 후 진행합니다.

---

## PHASE 1: 기반 버그 수정

### TASK-01: `rotateDjs()` stream 미실행 — DJ 로테이션 전면 불가
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 플레이리스트 다음 곡으로 자동 재생 안됨 |
| **심각도** | Critical |
| **파일** | `api/.../party/domain/entity/domainmodel/Partyroom.java:144-154` |
| **종속** | TASK-04, TASK-05의 전제 조건 |

**현재 코드:**
```java
// Partyroom.java:144
public Partyroom rotateDjs() {
    int totalElements = this.djSet.size();
    this.djSet.stream().peek(dj -> {        // ← stream terminal operation 없음
        if(dj.getOrderNumber() == 1) {
            dj.updateOrderNumber(totalElements);
        }else {
            dj.updateOrderNumber(dj.getOrderNumber() - 1);
        }
    });                                      // ← 스트림 미실행, 아무 일도 안 됨
    return this;
}
```

**수정 방법:**
```java
public Partyroom rotateDjs() {
    int totalElements = this.djSet.size();
    this.djSet.forEach(dj -> {               // ← forEach로 즉시 실행
        if(dj.getOrderNumber() == 1) {
            dj.updateOrderNumber(totalElements);
        } else {
            dj.updateOrderNumber(dj.getOrderNumber() - 1);
        }
    });
    return this;
}
```

**변경 범위:** `Partyroom.java` 1개 파일, 1줄 변경 (`stream().peek(` → `forEach(`, 마지막 `);` 제거)

---

### TASK-02: `CookieUtil.deleteCookie()` 속성 누락 — 로그아웃 불가
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 로그아웃해도 로그인 상태 유지 + 캐시 삭제 후 로그인 |
| **심각도** | High |
| **파일** | `api/.../common/config/security/jwt/CookieUtil.java:78-88` |
| **종속** | TASK-03이 이 수정에 종속 |

**현재 코드:**
```java
// CookieUtil.java:78
private void deleteCookie(HttpServletResponse response, String name) {
    var cookieConfig = jwtProperties.getCookie();
    Cookie cookie = new Cookie(name, "");    // ← jakarta Cookie API (SameSite 미지원)
    cookie.setPath(cookieConfig.getPath());
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);              // ← domain, SameSite, Secure 모두 누락
}
```

**수정 방법:** `addCookie()`와 동일하게 `Set-Cookie` 헤더 직접 구성
```java
private void deleteCookie(HttpServletResponse response, String name) {
    var cookieConfig = jwtProperties.getCookie();

    StringBuilder cookieBuilder = new StringBuilder();
    cookieBuilder.append(name).append("=");
    cookieBuilder.append("; Path=").append(cookieConfig.getPath());
    cookieBuilder.append("; Max-Age=0");
    cookieBuilder.append("; HttpOnly");

    if (cookieConfig.getSecure()) {
        cookieBuilder.append("; Secure");
    }

    cookieBuilder.append("; SameSite=").append(cookieConfig.getSameSite());

    response.addHeader("Set-Cookie", cookieBuilder.toString());

    log.debug("Deleted cookie: {}", name);
}
```

**변경 범위:** `CookieUtil.java` 1개 파일, `deleteCookie()` 메서드 전체 교체

---

### TASK-03: 캐시 삭제해야만 로그인 가능 — TASK-02에 종속
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 로그인 오류(구글/트위터 동일) |
| **심각도** | High |
| **파일** | — |
| **종속** | TASK-02 완료 시 자동 해결 |

TASK-02의 `deleteCookie()` 수정으로 쿠키가 정상 삭제되면, 만료된 JWT 쿠키 잔존 문제가 해결됩니다. TASK-02 완료 후 검증만 진행합니다.

---

## PHASE 2: 핵심 기능 버그 수정

### TASK-04: `dequeueDj()` 무조건 `skipBySystem()` 호출 — DJ 대기열 불안정
| 항목 | 내용 |
|------|------|
| **관련 이슈** | DJ 대기열 올라갔다 내려갔다 반복 중 오류로 튕김 |
| **심각도** | High |
| **파일** | `api/.../party/application/service/DjManagementService.java:63-78` |
| | `api/.../party/domain/entity/domainmodel/Partyroom.java:167-178` |
| | `api/.../party/application/service/lock/DistributedLockExecutor.java:19` |
| **종속** | TASK-01 선행 필요 (rotateDjs 정상 동작 전제) |

**버그 1: 현재 DJ 여부 무관하게 skipBySystem 호출**

현재 코드:
```java
// DjManagementService.java:74-77
partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
partyroomRepository.save(partyroomConverter.toData(partyroom));
// TODO 2024.10.06 CurrentDj인 경우, skipBySystem 호출
playbackManagementService.skipBySystem(partyroomId);   // ← 항상 호출
```

수정:
```java
partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
partyroomRepository.save(partyroomConverter.toData(partyroom));
if (partyroom.isCurrentDj(new CrewId(crew.getId()))) {  // ← 조건 추가
    playbackManagementService.skipBySystem(partyroomId);
}
```

> **주의:** `isCurrentDj()`는 `isPlaybackActivated`가 true일 때만 true를 반환합니다(`Partyroom.java:223-228`). 따라서 재생이 비활성 상태일 때 DJ 대기열에서 나가면 skip이 호출되지 않아 정상입니다.

**버그 2: `tryRemoveInDjQueue()`에서 HashSet 순서 비보장**

현재 코드:
```java
// Partyroom.java:167-178
public void tryRemoveInDjQueue(CrewId crewId) {
    AtomicInteger orderNumber = new AtomicInteger(1);
    this.djSet = this.djSet.stream().peek(dj -> {
        if(dj.getCrewId().equals(crewId)) {
            dj.applyDequeued();
        }else {
            dj.updateOrderNumber(orderNumber.get());   // ← HashSet 순서 = 랜덤
            orderNumber.getAndIncrement();
        }
    }).collect(Collectors.toSet());
}
```

수정:
```java
public void tryRemoveInDjQueue(CrewId crewId) {
    AtomicInteger orderNumber = new AtomicInteger(1);
    this.djSet = this.djSet.stream()
        .sorted(Comparator.comparingInt(Dj::getOrderNumber))  // ← 정렬 후 재배정
        .peek(dj -> {
            if (dj.getCrewId().equals(crewId)) {
                dj.applyDequeued();
            } else {
                dj.updateOrderNumber(orderNumber.getAndIncrement());
            }
        }).collect(Collectors.toSet());
}
```

**버그 3: 분산 락 LOCK_VALUE 고정값**

현재 코드:
```java
// DistributedLockExecutor.java:19
private static final String LOCK_VALUE = "unique-identifier";
```

수정:
```java
public void performTaskWithLock(String LOCK_SUFFIX, Supplier<Void> action) {
    String LOCK_KEY = LOCK_PREFIX + LOCK_SUFFIX;
    String lockValue = UUID.randomUUID().toString();     // ← 호출마다 고유 값 생성
    boolean lockAcquired = redisLockService.acquireLock(LOCK_KEY, lockValue, 10, TimeUnit.SECONDS);
    if (lockAcquired) {
        try {
            action.get();
        } finally {
            redisLockService.releaseLock(LOCK_KEY, lockValue);
        }
    } else {
        log.warn("Could not acquire lock for key: {}", LOCK_KEY);
    }
}
```

**변경 범위:** `DjManagementService.java` (조건 추가), `Partyroom.java` (정렬 추가), `DistributedLockExecutor.java` (UUID 도입)

---

### TASK-05: DJing 도중 추가한 곡이 다음 재생에 반영 안됨
| 항목 | 내용 |
|------|------|
| **관련 이슈** | DJing 도중 플레이중인 플레이리스트에 새 곡 추가해도 반영 안됨 |
| **심각도** | High |
| **파일** | `api/.../playlist/application/service/TrackCommandService.java:30` |
| **종속** | TASK-01 선행 필요 (DJ 로테이션 정상 동작 전제) |

**현재 코드:**
```java
// TrackCommandService.java:30
public void addTrackInPlaylist(Long playlistId, AddTrackRequest request) {
    // ← @Transactional 없음!
    ...
    long nextMusicOrderNumber = playlistSummary.getMusicCount() == 0 ? 1 : playlistSummary.getMusicCount() + 1;
    ...
    trackRepository.save(trackData);
}
```

**수정 방법:**
```java
@Transactional  // ← 추가
public void addTrackInPlaylist(Long playlistId, AddTrackRequest request) {
    ...
}
```

> **참고:** `jakarta.transaction.Transactional`이 이미 import되어 있으므로 (`TrackCommandService.java:15`) 어노테이션만 추가하면 됩니다.

**변경 범위:** `TrackCommandService.java` 1개 파일, 1줄 추가

---

### TASK-06: DJ 새로고침 시 Crew 아바타 사라짐 (Partyroom Id Not Found)
| 항목 | 내용 |
|------|------|
| **관련 이슈** | DJ가 새로고침 시 Crew 아바타 사라짐 |
| **심각도** | High |
| **파일** | `api/.../party/application/service/PartyroomAccessService.java:62-69` |
| | `api/.../party/application/service/cache/PartyroomSessionCacheManager.java:32-35` |
| **종속** | 없음 (독립적) |

**현재 코드:**
```java
// PartyroomAccessService.java:62-69
Optional<ActivePartyroomWithCrewDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
if (optActiveRoomInfo.isPresent()) {
    ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
    if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId())))
        throw ExceptionCreator.create(PartyroomException.ACTIVE_ANOTHER_ROOM);
    return partyroom.getCrewByUserId(userId).orElseThrow();
    // ← 같은 룸 재진입 시 publishAccessChangedEvent() 미호출!
}
```

**수정 방법:**
```java
if (optActiveRoomInfo.isPresent()) {
    ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
    if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId())))
        throw ExceptionCreator.create(PartyroomException.ACTIVE_ANOTHER_ROOM);
    // 같은 룸 재진입 — 아바타 이벤트를 다시 발행하여 다른 Crew에게 전파
    Crew crew = partyroom.getCrewByUserId(userId).orElseThrow();
    publishAccessChangedEvent(crew, userId);
    return crew;
}
```

**변경 범위:** `PartyroomAccessService.java` 1개 파일, 2줄 수정

---

## PHASE 3: 중간 우선순위 수정

### TASK-07: `OAuthUrlService` → `RedisStateStore` 전환
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 트위터 로그인 실패 (비밀모드에서 계속 실패) |
| **심각도** | Mid |
| **파일** | `api/.../auth/application/service/OAuthUrlService.java` |
| **종속** | 없음 (독립적) |

**현재 상태:** `OAuthUrlService`가 인메모리 `ConcurrentHashMap<String, StateInfo>`를 사용. `RedisStateStore`(`api/.../auth/application/store/RedisStateStore.java`)가 이미 구현되어 있지만 사용되지 않음.

**수정 방법:** `OAuthUrlService`를 리팩터링하여 `RedisStateStore`를 주입받아 사용
```java
@Service
@RequiredArgsConstructor
public class OAuthUrlService {
    private final OAuth2Properties oAuth2Properties;
    private final StateStore stateStore;          // ← RedisStateStore 주입

    // ConcurrentHashMap<String, StateInfo> stateStore 필드 제거
    // StateInfo 내부 클래스 제거

    public OAuthUrlResponse generateAuthUrl(OAuthProvider provider, String codeVerifier) {
        OAuth2Properties.Provider config = getProviderConfig(provider);
        String state = stateStore.generateAndStoreState(provider.getValue());  // ← Redis 사용
        String codeChallenge = generateCodeChallenge(codeVerifier);
        String authUrl = buildAuthUrl(provider, config, state, codeChallenge);
        ...
    }

    public boolean validateAndConsumeState(String state, OAuthProvider provider, String codeVerifier) {
        return stateStore.validateAndConsumeState(state, provider.getValue()); // ← Redis 사용
    }

    // cleanupExpiredStates() 메서드 제거 (Redis TTL이 자동 처리)
}
```

> **참고:** `RedisStateStore`는 `codeVerifier`를 저장하지 않습니다. PKCE를 제대로 지원하려면 `RedisStateStore`에 `codeVerifier`도 함께 저장하도록 확장이 필요합니다. 단, 현재 콜백에서 `codeVerifier` 검증이 optional이므로(`OAuthUrlService.java:77`) 우선은 provider만 저장하는 현재 `RedisStateStore`로 전환해도 동작합니다.

**변경 범위:** `OAuthUrlService.java` 1개 파일 리팩터링

---

### TASK-08: 파티룸 간 직접 이동 시 자동 exit
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 현재 파티룸에서 홈 안 거치고 다른 파티룸 이동 시 오류 |
| **심각도** | Mid |
| **파일** | `api/.../party/application/service/PartyroomAccessService.java:62-69` |
| **종속** | TASK-06 이후 작업 권장 (같은 메서드 수정) |

**현재 코드:** 다른 룸이 active이면 `ACTIVE_ANOTHER_ROOM` 예외 발생

**수정 방법:** 다른 룸이 active이면 자동으로 기존 룸 exit 처리 후 새 룸 enter 진행
```java
if (optActiveRoomInfo.isPresent()) {
    ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
    if (partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId()))) {
        // 기존 룸 자동 exit 처리
        exit(new PartyroomId(activeRoomInfo.getId()));
        // 이후 아래로 fall-through하여 새 룸 enter 진행
    } else {
        // 같은 룸 재진입
        Crew crew = partyroom.getCrewByUserId(userId).orElseThrow();
        publishAccessChangedEvent(crew, userId);
        return crew;
    }
}
```

> **주의:** `exit()` 메서드가 `ThreadLocalContext`에서 `PartyContext`를 가져오므로, `tryEnter()` 내에서 호출 시 컨텍스트가 동일한지 확인 필요. 필요시 `exit()` 내부 로직을 직접 호출하는 private 메서드로 분리.

**변경 범위:** `PartyroomAccessService.java` 1개 파일

---

### TASK-09: 프로필 `introduction` 컬럼 길이 조정
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 프로필 설명 50자 제한임에도 50자 입력 시 오류 |
| **심각도** | Mid |
| **파일** | `api/.../profile/domain/ProfileData.java:41` |
| | `api/.../profile/presentation/dto/request/UpdateMyBioRequest.java` |
| **종속** | 없음 (독립적) |

**수정 방법 1 — DB 컬럼 길이 변경:**
```java
// ProfileData.java:41
@Column(length = 50)       // ← 30 → 50으로 변경
private String introduction;
```

**수정 방법 2 — 요청 DTO에 유효성 검증 추가:**
```java
// UpdateMyBioRequest.java
@Getter
public class UpdateMyBioRequest {
    @Size(max = 20, message = "닉네임은 20자를 초과할 수 없습니다")
    String nickname;

    @Size(max = 50, message = "소개글은 50자를 초과할 수 없습니다")
    String introduction;
}
```

**수정 방법 3 — JPA DDL 반영:** `spring.jpa.hibernate.ddl-auto` 설정에 따라 자동 반영 여부 확인. 수동 마이그레이션이 필요하면:
```sql
ALTER TABLE USER_PROFILE MODIFY introduction VARCHAR(50);
```

**변경 범위:** `ProfileData.java`, `UpdateMyBioRequest.java` 2개 파일 + DB 마이그레이션 스크립트

---

## PHASE 4: 낮은 우선순위

### TASK-10: 곡 검색 에러 (`platform` @NotNull)
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 곡 추가 > 검색어 입력 시 에러 발생 |
| **심각도** | High (이슈 심각도) / Low (작업 난이도) |
| **파일** | `api/.../playlist/presentation/payload/request/SearchMusicListRequest.java:14` |
| **종속** | 없음 (독립적) |

**수정 방법:** `platform` 필드의 `@NotNull` 제거 (현재 사용하지 않으므로)
```java
// SearchMusicListRequest.java
@Getter
@Setter
public class SearchMusicListRequest {
    @NotNull(message = "q cannot be null")
    private final String q;

    private final String platform;    // ← @NotNull 제거

    SearchMusicListRequest(@BindParam("q") String q, @BindParam("platform") String platform) {
        this.q = q;
        this.platform = platform;
    }
}
```

**변경 범위:** `SearchMusicListRequest.java` 1개 파일, 1줄 제거

---

### TASK-11: `Playback.parseDuration()` 다양한 포맷 지원
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 곡 길이 제한보다 짧은 곡 신청 시 에러 |
| **심각도** | Low |
| **파일** | `api/.../party/domain/entity/domainmodel/Playback.java:74-82` |
| **종속** | 없음 (독립적) |

**현재 코드:** `"MM:SS"` 포맷만 지원
```java
private static Duration parseDuration(String durationStr) {
    String[] parts = durationStr.split(":");
    if (parts.length != 2) {
        throw new DateTimeParseException("Invalid duration format", durationStr, 0);
    }
    int minutes = Integer.parseInt(parts[0]);
    int seconds = Integer.parseInt(parts[1]);
    return Duration.ofMinutes(minutes).plusSeconds(seconds);
}
```

**수정 방법:** `"H:MM:SS"` 포맷도 지원
```java
private static Duration parseDuration(String durationStr) {
    String[] parts = durationStr.split(":");
    return switch (parts.length) {
        case 2 -> {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            yield Duration.ofMinutes(minutes).plusSeconds(seconds);
        }
        case 3 -> {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            yield Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
        default -> throw new DateTimeParseException("Invalid duration format", durationStr, 0);
    };
}
```

**변경 범위:** `Playback.java` 1개 파일, `parseDuration()` 메서드 수정

---

### TASK-12: 아바타 좌표 DB 마이그레이션
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 아바타 body 중앙점 위치 오류 |
| **심각도** | High (이슈 심각도) / Mid (작업 난이도) |
| **파일** | `api/.../user/application/service/initialize/AvatarResourceInitializeService.java` |
| **종속** | 없음 (독립적) |

**현재 상태:** 커밋 `a7da045`에서 `AvatarResourceInitializeService.java`의 좌표값은 수정했지만, 기존 DB 레코드가 갱신되지 않음. `addAvatarBody()`는 `save()` = INSERT만 수행.

**수정 방법:** DB 마이그레이션 SQL 스크립트 작성
```sql
-- 1. AVATAR_BODY_RESOURCE 테이블의 좌표 갱신
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 41 WHERE name = 'ava_body_basic_001';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 39 WHERE name = 'ava_body_djing_003';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 45 WHERE name = 'ava_body_djing_004';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_005';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 39 WHERE name = 'ava_body_djing_006';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_007';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_008';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 42 WHERE name = 'ava_body_djing_009';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 43 WHERE name = 'ava_body_djing_010';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 44 WHERE name = 'ava_body_djing_011';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 38 WHERE name = 'ava_body_djing_012';

-- 2. USER_PROFILE 테이블의 기존 사용자 좌표도 갱신
-- 아바타 body URI를 기준으로 매칭하여 갱신
UPDATE USER_PROFILE up
JOIN AVATAR_BODY_RESOURCE abr ON up.avatar_body_uri = abr.resource_uri
SET up.combine_position_x = abr.combine_position_x,
    up.combine_position_y = abr.combine_position_y;
```

또한 `AvatarResourceInitializeService.addAvatarBody()`를 upsert 방식으로 변경하여 향후 좌표 변경 시 기존 레코드도 갱신되도록 개선 필요:
```java
private void addAvatarBody(String name, String resourceUri, ...) {
    Optional<AvatarBodyResourceData> existing = avatarBodyResourceRepository.findByName(name);
    if (existing.isPresent()) {
        // UPDATE 기존 레코드
        AvatarBodyResourceData data = existing.get();
        data.updatePositions(x, y);
        avatarBodyResourceRepository.save(data);
    } else {
        // INSERT 새 레코드
        AvatarBodyResource avatarBodyResource = AvatarBodyResource.create(name, resourceUri, ...);
        avatarBodyResourceRepository.save(avatarBodyResource.toData());
    }
}
```

**변경 범위:** DB 마이그레이션 SQL + `AvatarResourceInitializeService.java` + `AvatarBodyResourceRepository` (findByName 메서드 추가)

---

## PHASE 5: 재검수 필요 항목

### TASK-13: 곡 전환 시 좋아요/그랩/싫어요 숫자 미갱신
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 노래가 전환되었지만 전광판 하단에 좋아요/그랩/싫어요 숫자 유지 |
| **심각도** | Mid |
| **파일** | `api/.../party/application/dto/playback/PlaybackDto.java` |
| | `api/.../party/interfaces/listener/redis/message/PlaybackStartMessage.java` |
| | `api/.../party/application/service/PlaybackManagementService.java:122-128` |
| **종속** | TASK-01 선행 필요 (rotateDjs가 동작해야 곡 전환 자체가 정상 발생) |

**현재 코드:**
```java
// PlaybackDto.java — 리액션 카운트 필드 없음
public class PlaybackDto {
    private long id;
    private String linkId;
    private String name;
    private String duration;
    private String thumbnailImage;
    private long endTime;
    // likeCount, grabCount, dislikeCount 필드 없음!
}
```

```java
// PlaybackManagementService.java:124-127
new PlaybackDto(playbackData.getId(), playbackData.getLinkId(),
                playbackData.getName(), playbackData.getDuration(),
                playbackData.getThumbnailImage(), playbackData.getEndTime())
// grabCount, likeCount, dislikeCount를 전달하지 않음
```

**수정 방법:**

1. `PlaybackDto`에 카운트 필드 추가:
```java
public class PlaybackDto {
    private long id;
    private String linkId;
    private String name;
    private String duration;
    private String thumbnailImage;
    private long endTime;
    private int likeCount;      // ← 추가
    private int dislikeCount;   // ← 추가
    private int grabCount;      // ← 추가
}
```

2. `publishPlaybackChangedEvent()`에서 카운트 전달:
```java
new PlaybackDto(playbackData.getId(), playbackData.getLinkId(),
                playbackData.getName(), playbackData.getDuration(),
                playbackData.getThumbnailImage(), playbackData.getEndTime(),
                playbackData.getLikeCount(), playbackData.getDislikeCount(),
                playbackData.getGrabCount())
// 신규 Playback은 모두 0이므로 프론트엔드가 이 값으로 초기화
```

**변경 범위:** `PlaybackDto.java` (필드 추가), `PlaybackManagementService.java` (생성자 인자 추가)

---

### TASK-14: Mod 이상의 DJ 대기열 관리 기능 미구현
| 항목 | 내용 |
|------|------|
| **관련 이슈** | Mod 이상의 DJ대기열 관리 기능 미노출 |
| **심각도** | Mid |
| **파일** | `api/.../party/application/service/DjManagementService.java:86-88` |
| | `api/.../party/interfaces/api/rest/DjManagementController.java` |
| | `api/.../party/application/service/PartyroomInfoService.java:96-99` |
| **종속** | TASK-04 선행 필요 (tryRemoveInDjQueue 정상 동작 전제) |

**버그 1: 관리자 DJ 강제 제거 메서드가 빈 TODO**

현재 코드:
```java
// DjManagementService.java:86-88
@Transactional
public void dequeueDj(PartyroomId partyroomId, DjId djId) {
    // TODO 관리자 등급 여부를 체크
    // 실제 구현 코드 없음 — 빈 메서드
}
```

수정: 기존 `dequeueDj(PartyroomId)` 로직을 참고하여 구현
```java
@Transactional
public void dequeueDj(PartyroomId partyroomId, DjId djId) {
    PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
    Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
    if (optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
    PartyroomData partyroomData = partyroomConverter.toEntity(optional.get());
    Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

    // 관리자 등급 체크
    if (crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId()))
        throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

    // 대상 DJ의 CrewId를 찾아서 제거
    Dj targetDj = partyroom.getDjSet().stream()
        .filter(dj -> dj.getId().equals(djId.getId()))
        .findFirst()
        .orElseThrow(() -> ExceptionCreator.create(DjException.NOT_FOUND_DJ));

    boolean isCurrentDj = partyroom.isCurrentDj(targetDj.getCrewId());
    partyroom.tryRemoveInDjQueue(targetDj.getCrewId());
    partyroomRepository.save(partyroomConverter.toData(partyroom));

    if (isCurrentDj) {
        playbackManagementService.skipBySystem(partyroomId);
    }
}
```

> **참고:** `CrewDomainService`와 `GradeException`을 주입/import 해야 합니다.

**버그 2: `isAlreadyRegistered()`에서 `allMatch` 오용**

현재 코드:
```java
// PartyroomInfoService.java:96-99
public boolean isAlreadyRegistered(Partyroom partyroom) {
    PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
    return partyroom.getDjSet().stream()
        .allMatch(dj -> dj.getUserId().equals(partyContext.getUserId()));
    // ← allMatch: "모든 DJ가 본인이어야 true" → DJ 2명 이상이면 항상 false
}
```

수정:
```java
public boolean isAlreadyRegistered(Partyroom partyroom) {
    PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
    return partyroom.getDjSet().stream()
        .anyMatch(dj -> dj.getUserId().equals(partyContext.getUserId()));
    // ← anyMatch: "본인이 DJ 중 하나라도 있으면 true"
}
```

**버그 3: `DjManagementController`에 `@PreAuthorize` 누락**

현재 코드:
```java
// DjManagementController.java — 타인 DJ 제거 엔드포인트
@DeleteMapping("/{partyroomId}/djs/{djId}")
// @PreAuthorize 없음
public ResponseEntity<?> dequeueDj(@PathVariable Long partyroomId, @PathVariable Long djId) { ... }
```

수정:
```java
@DeleteMapping("/{partyroomId}/djs/{djId}")
@PreAuthorize("hasAnyRole('ROLE_MEMBER')")  // ← 추가 (서비스 레벨에서 등급 체크)
public ResponseEntity<?> dequeueDj(@PathVariable Long partyroomId, @PathVariable Long djId) { ... }
```

**변경 범위:** `DjManagementService.java` (메서드 구현), `PartyroomInfoService.java` (allMatch→anyMatch), `DjManagementController.java` (@PreAuthorize 추가)

---

### TASK-15: 파티룸 링크 비로그인 입장 시 메인 홈 노출
| 항목 | 내용 |
|------|------|
| **관련 이슈** | 파티룸 링크 복사 > 비로그인 계정으로 입장 시 메인 홈 화면 노출 |
| **심각도** | Mid |
| **파일** | `api/.../party/application/service/PartyroomAccessService.java:166-173` |
| | `api/.../common/config/security/SecurityConfig.java` |
| **종속** | 없음 (독립적, 프론트엔드 협업 필요) |

**현재 코드:**
```java
// PartyroomAccessService.java:166-173
@Transactional(readOnly = true)
public Map<String, Long> getRedirectUri(String linkDomain) {
    PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
            .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
    return Map.of("partyroomId", partyroomData.getId());
    // partyroomId만 반환, 게스트 자동 인증/리다이렉트 처리 없음
}
```

**현재 흐름:**
1. 비로그인 유저가 파티룸 링크 접속
2. 프론트엔드가 `GET /api/v1/partyrooms/link/{linkDomain}/enter` 호출 (permitAll)
3. `partyroomId`를 받음
4. `POST /api/v1/partyrooms/{id}/enter` 호출 시도 → 인증 필요 → 실패
5. 프론트엔드가 메인 홈으로 리다이렉트

**수정 방법:** 링크 진입 API에서 비로그인 유저에 대해 게스트 토큰을 자동 발급하고 partyroomId와 함께 반환

```java
@Transactional
public Map<String, Object> getRedirectUri(String linkDomain, HttpServletResponse response) {
    PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
            .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

    Map<String, Object> result = new HashMap<>();
    result.put("partyroomId", partyroomData.getId());

    // 비로그인 유저인 경우 게스트 토큰 자동 발급
    // (인증 정보가 없는 경우)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
        Guest guest = guestSignService.getGuestOrCreate();
        String accessToken = jwtService.generateAccessTokenForGuest(guest);
        cookieUtil.addAccessTokenCookie(response, accessToken);
        result.put("guestAuthenticated", true);
    }

    return result;
}
```

> **주의:** 이 접근법은 프론트엔드와의 협업이 필요합니다. 프론트엔드에서 링크 진입 시 게스트 토큰 수신 후 자동으로 파티룸 enter API를 호출하는 흐름을 구현해야 합니다. 또는 프론트엔드가 직접 게스트 로그인 후 리다이렉트하는 방식도 고려 가능합니다.

**변경 범위:** `PartyroomAccessService.java` (게스트 토큰 연계) + 프론트엔드 협업

---

## 작업 순서 요약

| 순서 | TASK | 이슈 | 난이도 | 변경 파일 수 |
|------|------|------|--------|-------------|
| 1 | TASK-01 | DJ 자동 재생 불가 | 1줄 변경 | 1 |
| 2 | TASK-02 | 로그아웃 불가 | 메서드 교체 | 1 |
| 3 | TASK-10 | 곡 검색 에러 | 1줄 제거 | 1 |
| 4 | TASK-04 | DJ 대기열 튕김 | 3곳 수정 | 3 |
| 5 | TASK-05 | 곡 추가 미반영 | 1줄 추가 | 1 |
| 6 | TASK-06 | 아바타 사라짐 | 2줄 수정 | 1 |
| 7 | TASK-13 | 리액션 카운트 미갱신 | 필드 추가 | 2 |
| 8 | TASK-14 | DJ 관리 기능 미구현 | 메서드 구현 | 3 |
| 9 | TASK-07 | 트위터 로그인 | 리팩터링 | 1 |
| 10 | TASK-08 | 파티룸 이동 오류 | 로직 추가 | 1 |
| 11 | TASK-09 | 프로필 50자 오류 | 컬럼 + DTO | 2 + SQL |
| 12 | TASK-15 | 링크 비로그인 입장 | 게스트 연계 | 1 + FE |
| 13 | TASK-11 | 곡 길이 파싱 | 메서드 확장 | 1 |
| 14 | TASK-12 | 아바타 좌표 | DB 마이그레이션 | 2 + SQL |

---

## 검증 방법

각 TASK 완료 후:
1. **TASK-01**: DJ 2명 이상 등록 → 곡 자동 전환 확인
2. **TASK-02/03**: 로그인 → 로그아웃 → 브라우저 쿠키 확인 → 재로그인
3. **TASK-04**: DJ 대기열 등록 → 해제 → 재등록 반복 (현재 DJ가 아닌 경우 skip 미발생 확인)
4. **TASK-05**: DJing 중 플레이리스트에 곡 추가 → 다음 재생 시 반영 확인
5. **TASK-06**: 파티룸 내 DJ 상태에서 새로고침 → 아바타 유지 확인
6. **TASK-07**: 비밀모드에서 트위터 로그인 → 정상 인증 확인
7. **TASK-08**: 파티룸 A 입장 → 홈 미경유 → 파티룸 B 직접 입장 → 정상 전환 확인
8. **TASK-09**: 프로필 설명 50자 입력 → 정상 저장 확인
9. **TASK-10**: 곡 검색 → platform 파라미터 없이 정상 검색 확인
10. **TASK-11**: 1시간 이상 길이의 곡 재생 시도 → 정상 처리 확인
11. **TASK-12**: 아바타 선택 → 중앙점 위치 정상 확인
12. **TASK-13**: 곡 전환 시 좋아요/그랩/싫어요 카운트가 0으로 초기화되는지 확인
13. **TASK-14**: Mod 이상 등급으로 DJ 대기열 관리 (타인 DJ 제거) 동작 확인 + isRegistered 값 정상 반환 확인
14. **TASK-15**: 파티룸 링크를 비로그인 상태로 접속 → 게스트 자동 인증 후 파티룸 입장 확인

---

## 수정 완료 결과 (2026-02-15)

**브랜치:** `fix/qa-issues` (from `feature/admin-preview`)
**총 커밋 수:** 14건 (TASK-03 제외 — TASK-02에 종속되어 자동 해결)

### 완료 현황

| TASK | 상태 | 커밋 해시 | 수정 파일 | 테스트 파일 |
|------|------|-----------|-----------|-------------|
| TASK-01 | **완료** | `bc4f4f8` | `Partyroom.java` | `PartyroomRotateDjsTest.java` |
| TASK-02 | **완료** | `21cd38b` | `CookieUtil.java` | `CookieUtilTest.java` |
| TASK-03 | **완료** | — (TASK-02에 종속) | — | — |
| TASK-04 | **완료** | `6731175` | `DjManagementService.java`, `Partyroom.java`, `DistributedLockExecutor.java` | `PartyroomDjQueueTest.java` |
| TASK-05 | **완료** | `913dde1` | `TrackCommandService.java` | `TrackCommandServiceTest.java` |
| TASK-06 | **완료** | `6266575` | `PartyroomAccessService.java` | `PartyroomAccessServiceTest.java` |
| TASK-07 | **완료** | `0355a6f` | `OAuthUrlService.java`, `OAuthCleanupScheduler.java` | `OAuthUrlServiceTest.java` |
| TASK-08 | **완료** | `48e2595` | `PartyroomAccessService.java` | `PartyroomAccessServiceTest.java` (TASK-06과 공유) |
| TASK-09 | **완료** | `22b0ccf` | `ProfileData.java`, `UpdateMyBioRequest.java`, `UserBioController.java` | `UpdateMyBioRequestTest.java` |
| TASK-10 | **완료** | `befb538` | `SearchMusicListRequest.java` | `SearchMusicListRequestTest.java` |
| TASK-11 | **완료** | `d77f858` | `Playback.java` | `PlaybackParseDurationTest.java` |
| TASK-12 | **완료** | `19c4407` | `AvatarResourceInitializeService.java`, `AvatarBodyResourceData.java`, `AvatarBodyResourceRepository.java`, `V20260215__update_avatar_body_positions.sql` | `AvatarBodyResourceDataTest.java` |
| TASK-13 | **완료** | `0155327` | `PlaybackDto.java` | `PlaybackDtoTest.java` |
| TASK-14 | **완료** | `e6cafe4` | `DjManagementService.java`, `PartyroomInfoService.java`, `DjException.java`, `Partyroom.java` | `PartyroomInfoServiceIsRegisteredTest.java` |
| TASK-15 | **완료** | `d329bc7` | `PartyroomAccessController.java` | `PartyroomAccessControllerLinkTest.java` |

### 수정 시 계획 대비 변경 사항

| TASK | 변경 사항 |
|------|-----------|
| TASK-04 | `DjManagementService`에서 `isCurrentDj()` 체크를 `tryRemoveInDjQueue()` 호출 전에 수행 (제거 후에는 상태가 변경되므로) |
| TASK-07 | `OAuthCleanupScheduler.java`도 함께 비워서 Redis TTL 자동 처리 방식으로 전환 |
| TASK-09 | `UserBioController.java`에 `@Valid` 어노테이션 추가 (DTO 유효성 검증이 동작하려면 필수) |
| TASK-14 | `DjException`에 `NOT_FOUND_DJ` 열거값 신규 추가, `Partyroom`에 `getDjById()` 메서드 추가 |
| TASK-15 | 게스트 토큰 발급 로직을 `PartyroomAccessService`가 아닌 `PartyroomAccessController`에 배치 (컨트롤러 레벨에서 `HttpServletResponse` 접근이 자연스러움) |

### 미실행 항목

- **테스트 실행:** 로컬 환경에 Gradle이 설치되어 있지 않아 테스트 실행 불가. CI/CD 파이프라인에서 검증 필요.
- **DB 마이그레이션 (TASK-12):** `docs/migration/V20260215__update_avatar_body_positions.sql` 스크립트 작성 완료. 운영 DB에 수동 적용 필요.
