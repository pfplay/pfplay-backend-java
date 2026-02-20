# ERD 및 데이터 계층 개선 변경 이력

> **작성일**: 2026-02-20
> **브랜치**: `refactor/ddd-cleanup`
> **범위**: DB 인덱스, 엔티티, DTO, Repository, Service 계층 정리
> **원칙**: 모든 변경은 코드 분석(grep, import 추적, 호출 그래프)에 기반한 명확한 근거를 가짐
> **성격**: 순수 정리/축소 — **새로 생성된 테이블·엔티티·DTO는 0건**. 모든 변경은 기존 구조의 수정 또는 삭제.

---

## 변경 총괄

### 변경 방향 요약

| 구분 | 추가 | 수정 | 삭제 |
|------|------|------|------|
| **테이블/엔티티** | 0 | 4 (`CrewData`, `PartyroomData`, `PlaybackData`, `DjData`) | 0 |
| **DTO 클래스** | 0 | 2 (`ActivePartyroomDto`, `PlaybackDto`) | 3 (`PartyroomDto`, `ActivePartyroomWithCrewDto`, `CurrentDjWithProfileDto`) |
| **DB 컬럼** | 0 | 1 (rename: `partroom_id` → `partyroom_id`) | 1 (`DJ.isQueued`) |
| **인덱스** | 2 (CREW 복합) | 1 (PARTYROOM 이름 수정) | 1 (CREW 기존 단일 인덱스 — 복합으로 대체) |

### 수치 비교

| 항목 | Before | After | 차이 |
|------|--------|-------|------|
| Partyroom 관련 DTO | 5개 | 3개 | -2 (`PartyroomDto`, `ActivePartyroomWithCrewDto` 삭제) |
| DJ 관련 DTO | 2개 | 1개 | -1 (`CurrentDjWithProfileDto` 삭제) |
| Repository 미사용 메서드 | 4개 | 0개 | -4 |
| CREW 인덱스 | 1개 (단일 컬럼) | 2개 (복합) | 최빈 쿼리 최적화 |
| DJ 테이블 컬럼 | 8개 (`isQueued` 포함) | 7개 | -1 (dead data 제거) |
| 인덱스/컬럼명 오타 | 3건 | 0건 | 전량 수정 |
| PlaybackDto 생성자 | 3개 (역할 혼합) | 1개 + static factory 1개 | 역할 분리 |

---

## Phase DB-1: 인덱스 추가 + 오타 수정

### 1-1. CREW 복합 인덱스 교체

**파일**: `CrewData.java` (line 21-26)

| Before | After |
|--------|-------|
| `@Index(name = "crew_partroom_id_IDX", columnList = "partyroom_id")` | `@Index(name = "crew_partyroom_id_user_id_IDX", columnList = "partyroom_id, user_id")` |
| *(없음)* | `@Index(name = "crew_user_id_is_active_IDX", columnList = "user_id, is_active")` |

**변경 근거**:

1. **`crew_partyroom_id_user_id_IDX`** — 기존 단일 `partyroom_id` 인덱스를 **대체(superset)**하면서, `findByPartyroomDataIdAndUserId()` 쿼리의 인덱스 커버리지를 확보한다.
   - `CrewRepository.findByPartyroomDataIdAndUserId()` 는 `PartyroomAccessService`, `PartyroomInfoService`, `PartyroomSetupQueryService` 등 **10개 이상 서비스**에서 호출되는 최빈 쿼리
   - 기존 인덱스(`partyroom_id` 단일)에서는 `user_id` 조건이 인덱스 밖 필터링(Index Filter)으로 처리됨
   - 복합 인덱스로 전환하면 `partyroom_id` 단일 검색도 leading column 매칭으로 동일 성능 보장

2. **`crew_user_id_is_active_IDX`** — `PartyroomRepositoryImpl.getActivePartyroomByUserId()` 최적화
   - 이 쿼리는 `WHERE crew.user_id = ? AND crew.is_active = true` 조건으로 CREW → PARTYROOM JOIN
   - **시스템 최빈 쿼리**: 입장, 퇴장, 프로필 변경, 로그아웃, 세션 캐시 생성 등 거의 모든 사용자 동작마다 호출
   - 기존에는 `user_id`에 인덱스 없이 full scan 발생

### 1-2. PARTYROOM 인덱스명 오타 수정

**파일**: `PartyroomData.java` (line 26)

```
Before: @Index(name = "paytyroom_host_id_IDX", ...)
After:  @Index(name = "partyroom_host_id_IDX", ...)
```

**변경 근거**: `paytyroom` → `partyroom` 오타. 기능에 영향 없으나 DDL 생성 시 혼란 방지.

### 1-3. PLAYBACK 컬럼명 오타 수정

**파일**: `PlaybackData.java` (line 27)

```
Before: @Column(name = "partroom_id")
After:  @Column(name = "partyroom_id")
```

**변경 근거**: `partroom_id` → `partyroom_id` 오타. JPA `@AttributeOverride`로 실제 DB 컬럼명이 결정되므로, 이 수정은 **DDL 스키마 변경을 수반**한다. Hibernate `ddl-auto=validate` 환경에서는 마이그레이션 스크립트 필요. QueryDSL Q클래스는 `clean :app:compileJava`로 재생성 완료.

---

## Phase DB-2: Dead Code 제거

### 2-1. PartyroomDto 삭제

**삭제 파일**: `party/application/dto/partyroom/PartyroomDto.java`

**변경 근거**: 프로젝트 전체에서 이 클래스를 import하는 소스 파일이 **0건**. `grep -r "PartyroomDto" --include="*.java"` 결과 선언부 자기 자신만 매칭. `PartyroomWithCrewDto`가 동일 역할을 수행하고 있어 완전히 대체된 dead code.

### 2-2. CrewRepository.findByPartyroomDataId() 제거

**파일**: `CrewRepository.java`

```
삭제: List<CrewData> findByPartyroomDataId(Long partyroomId);
```

**변경 근거**: `grep "findByPartyroomDataId"` 에서 `CrewRepository` 선언부만 매칭, 호출처 **0건**. 실제로는 `findByPartyroomDataIdAndIsActiveTrue` (활성 크루만) 또는 `findByPartyroomDataIdAndUserId` (특정 유저)만 사용됨.

### 2-3. DjRepository.findByPartyroomDataId() 제거

**파일**: `DjRepository.java`

```
삭제: List<DjData> findByPartyroomDataId(Long partyroomId);
```

**변경 근거**: `grep` 결과 호출처 **0건**. `findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc` (정렬+필터 버전)만 사용됨.

---

## Phase DB-3: ActivePartyroom DTO 통합

### 변경 대상

| Before | After |
|--------|-------|
| `ActivePartyroomDto(Long id, boolean isPlaybackActivated, boolean isQueueClosed, PlaybackId currentPlaybackId)` | `ActivePartyroomDto(Long id, boolean isPlaybackActivated, boolean isQueueClosed, PlaybackId currentPlaybackId, Long crewId)` |
| `ActivePartyroomWithCrewDto(Long id, boolean isPlaybackActivated, boolean isQueueClosed, PlaybackId currentPlaybackId, Long crewId)` | **삭제** |

### 변경 근거

**문제**: 두 DTO의 차이는 `crewId` 필드 1개. 이로 인해:
- `PartyroomRepositoryCustom`에 거의 동일한 쿼리 메서드 2개 존재 (`getActivePartyroomByUserId`, `getMyActivePartyroomWithCrewIdByUserId`)
- `PartyroomRepositoryImpl`에 동일 JOIN + WHERE 조건의 QueryDSL 코드 중복 (line 30-71)
- `PartyroomInfoService`에 래퍼 메서드 3개 중복 (`getMyActivePartyroom`, `getMyActivePartyroomWithCrewId`, `getMyActivePartyroomWithCrewOrThrow`)
- 호출처에서 crewId 필요 여부에 따라 다른 메서드를 선택해야 하는 불필요한 분기

**판단**: `crewId`는 이미 같은 JOIN에서 조회 가능 (CREW 테이블 기준 쿼리). 추가 비용 없이 항상 포함시키면 DTO 1개, 쿼리 1개, 서비스 메서드 2개로 통합 가능.

### 영향받은 파일 (소스 8개 + 테스트 5개)

**Repository 계층**:
| 파일 | 변경 |
|------|------|
| `PartyroomRepositoryCustom.java` | `getMyActivePartyroomWithCrewIdByUserId()` 삭제, `getActivePartyroomByUserId()` 반환 타입 5필드로 확장 |
| `PartyroomRepositoryImpl.java` | 두 메서드를 하나로 병합 — crewId를 포함하는 단일 쿼리 |

**Service 계층**:
| 파일 | 변경 |
|------|------|
| `PartyroomInfoService.java` | `getMyActivePartyroomWithCrewId()` 삭제, `getMyActivePartyroomWithCrewOrThrow()` → `getMyActivePartyroomOrThrow()`로 rename, `getProfileSummaryByCrewId()` 내부 호출 통합 |

**호출처**:
| 파일 | Before | After |
|------|--------|-------|
| `PartyroomAccessService.java` | `getMyActivePartyroomWithCrewId(userId)` → `ActivePartyroomWithCrewDto` | `getMyActivePartyroom(userId)` → `ActivePartyroomDto` |
| `CrewBlockService.java` (3곳) | `getMyActivePartyroomWithCrewOrThrow()` → `ActivePartyroomWithCrewDto` | `getMyActivePartyroomOrThrow()` → `ActivePartyroomDto` |
| `PartyroomSessionCacheManager.java` | `getMyActivePartyroomWithCrewId()` → `ActivePartyroomWithCrewDto` | `getMyActivePartyroom()` → `ActivePartyroomDto` |
| `CrewProfileChangeHandler.java` | `getMyActivePartyroomWithCrewId()` → `ActivePartyroomWithCrewDto` | `getMyActivePartyroom()` → `ActivePartyroomDto` |
| `LogoutService.java` | `getMyActivePartyroomWithCrewId()` → `ActivePartyroomWithCrewDto` | `getMyActivePartyroom()` → `ActivePartyroomDto` |
| `PlaybackManagementService.java` | `ActivePartyroomDto` (4필드) | `ActivePartyroomDto` (5필드, crewId 미사용) |
| `PlaybackReactionService.java` | 동일 | 동일 |
| `PartyroomSetupQueryService.java` | repo 직접 호출, 반환 타입 변경 | 동일 |

**테스트**: `PartyroomAccessServiceTest`, `PartyroomAccessServiceDjQueueChangeTest`, `LogoutServiceTest`, `PlaybackManagementServiceTest`, `PartyroomSetupQueryServiceTest` — import/타입/생성자 인자 수 업데이트.

---

## Phase DB-4: CurrentDjWithProfileDto 통합

### 변경 대상

| Before | After |
|--------|-------|
| `CurrentDjWithProfileDto(long crewId, String nickname, String avatarIconUri)` | **삭제** |
| `DjWithProfileDto(long crewId, long orderNumber, String nickname, String avatarIconUri)` | 유지 — `orderNumber=0`을 "현재 DJ" 컨벤션으로 사용 |

### 변경 근거

**문제**: `CurrentDjWithProfileDto`와 `DjWithProfileDto`의 차이는 `orderNumber` 필드 1개. `CurrentDjWithProfileDto`의 사용처는 `QueryPartyroomSummaryResponse` 단 **1곳**.

**판단**: `DjWithProfileDto`에 `orderNumber=0`을 전달하면 "현재 DJ"를 의미하는 컨벤션으로 충분. 별도 DTO를 유지할 이유 없음.

### 영향받은 파일

| 파일 | 변경 |
|------|------|
| `QueryPartyroomSummaryResponse.java` | 필드 타입 `CurrentDjWithProfileDto` → `DjWithProfileDto`, 생성 시 `orderNumber=0` 추가 |

---

## Phase DB-5: DJ Hard-Delete 전환

### 변경 대상

**DjData 엔티티**:
| Before | After |
|--------|-------|
| `isQueued` 필드 존재 (boolean) | **삭제** |
| `applyDequeued()` 메서드 — `isQueued = false` 설정 | **삭제** |
| `create()` 팩토리 — `.isQueued(true)` | `.isQueued` 제거 |
| Builder — `boolean isQueued` 파라미터 | **삭제** |

### 변경 근거

**문제**: `isQueued` 는 soft-delete 플래그인데, `isQueued=false`인 레코드를 **조회하는 코드가 0건**:
- `grep "isQueued" --include="*.java"` 결과, 모든 쿼리 메서드가 `IsQueuedTrue` 조건을 가짐
- DJ 재생 이력은 별도 `PLAYBACK` 테이블에 이미 보존됨
- 결과적으로 `isQueued=false` 레코드는 **dead data**로 누적만 됨 — 조회도, 통계도, 복구도 사용하지 않음

**판단**: DJ 레코드를 dequeue 시 물리 삭제(hard-delete)하면:
- DJ 테이블 크기가 시간에 따라 무한 증가하지 않음
- `IsQueuedTrue` 조건 불필요 → 쿼리 단순화
- `isQueued` 필드/메서드/팩토리 코드 제거 → 엔티티 단순화

### DjRepository 메서드 변경

| Before | After | 근거 |
|--------|-------|------|
| `findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc` | `findByPartyroomDataIdOrderByOrderNumberAsc` | 테이블에 queued DJ만 존재하므로 필터 불필요 |
| `existsByPartyroomDataIdAndIsQueuedTrue` | `existsByPartyroomDataId` | 동일 |
| `existsByPartyroomDataIdAndUserIdAndIsQueuedTrue` | `existsByPartyroomDataIdAndUserId` | 동일 |

### PartyroomAggregateService 로직 변경

| 메서드 | Before (soft-delete) | After (hard-delete) |
|--------|---------------------|---------------------|
| `removeDjFromQueue()` | `filter(match).forEach(applyDequeued)` → `saveAll(전체)` | `filter(match)` → `deleteAll(대상)` + `filter(!match)` → reindex → `saveAll(나머지)` |
| `deactivatePlayback()` | `forEach(applyDequeued)` → `saveAll` | `deleteAll(전체)` |
| `rotateDjQueue()` | 변경 없음 (queued DJ만 대상, 동일) | 메서드명만 변경 |
| `isCurrentDj()` | 변경 없음 | 메서드명만 변경 |
| `hasQueuedDjs()` | `existsByPartyroomDataIdAndIsQueuedTrue` | `existsByPartyroomDataId` |

### PartyroomAccessService.handleDjQueueOnLeave() 변경

```java
// Before — isQueued 필드로 DJ 큐 소속 여부 판별
boolean wasInDjQueue = djRepository.findByPartyroomDataIdAndCrewId(...)
        .map(DjData::isQueued).orElse(false);

// After — 레코드 존재 자체가 큐 소속을 의미
boolean wasInDjQueue = djRepository.findByPartyroomDataIdAndCrewId(...)
        .isPresent();
```

### 영향받은 파일 (소스 6개 + 테스트 7개)

**소스**: `DjData.java`, `DjRepository.java`, `PartyroomAggregateService.java`, `DjManagementService.java`, `PlaybackManagementService.java`, `PartyroomInfoService.java`, `PartyroomAccessService.java`, `AdminDemoService.java`

**테스트**: `PartyroomAggregateServiceTest`, `DjManagementServiceDjQueueChangeTest`, `PartyroomAccessServiceDjQueueChangeTest`, `PartyroomInfoServiceIsRegisteredTest`, `PartyroomInfoServiceGetDjsTest`, `PartyroomDjQueueTest`, `PartyroomRotateDjsTest` — `.isQueued(true)` 빌더 인자 제거, mock 메서드명 변경, soft-delete assertion → hard-delete assertion 전환.

---

## Phase DB-6: PlaybackDto 생성자 정리

### 변경 대상

**Before** — 3개 생성자, 역할 혼합:
```java
@AllArgsConstructor  // 9인자 — 사용처 0건
@NoArgsConstructor
public class PlaybackDto {
    @QueryProjection
    public PlaybackDto(long id, ..., String thumbnailImage) { ... }        // 5인자 — QueryDSL 전용
    public PlaybackDto(long id, ..., String thumbnailImage, long endTime) { ... } // 6인자 — 서비스 레이어
}
```

**After** — 역할 분리, 생성 경로 명확:
```java
@NoArgsConstructor
public class PlaybackDto {
    @QueryProjection
    public PlaybackDto(long id, ..., String thumbnailImage) { ... }        // QueryDSL 프로젝션 전용

    public static PlaybackDto withEndTime(long id, ..., long endTime) {    // 서비스 레이어 전용
        PlaybackDto dto = new PlaybackDto(id, ...);
        dto.setEndTime(endTime);
        return dto;
    }
}
```

### 변경 근거

**문제**:
1. `@AllArgsConstructor` (9인자)는 호출처 **0건** — Lombok이 자동 생성하지만 사용되지 않음
2. 6인자 생성자와 5인자 생성자가 시그니처만 다르고 내부 로직은 "5인자 + endTime setter" — 코드 중복
3. 어떤 생성자가 어떤 용도인지 코드만으로 구분 불가

**판단**:
- `@AllArgsConstructor` 제거 — 미사용 생성자 방지
- 6인자 생성자 → `static PlaybackDto.withEndTime(...)` 팩토리 — 이름으로 용도 명시, 내부적으로 5인자 생성자 재사용
- `@QueryProjection` 5인자 생성자는 QueryDSL이 요구하므로 유지

### 영향받은 파일

| 파일 | Before | After |
|------|--------|-------|
| `PlaybackManagementService.java:130` | `new PlaybackDto(6인자)` | `PlaybackDto.withEndTime(6인자)` |
| `PartyroomSetupQueryService.java:79` | `new PlaybackDto(6인자)` | `PlaybackDto.withEndTime(6인자)` |
| `PlaybackDtoTest.java:14` | `new PlaybackDto(6인자)` | `PlaybackDto.withEndTime(6인자)` |

---

## 변경하지 않은 것과 그 근거

| 항목 | 판단 | 근거 |
|------|------|------|
| **"Crew" 네이밍** | **유지** | 단순 M:N 조인 테이블이 아님 — `gradeType`, `isBanned`, `isActive`, `enforceBan()`, `deactivatePresence()` 등 **7개 비즈니스 메서드** 보유. 프로젝트 유비쿼터스 언어("크루")와 일치 |
| **PARTYROOM 테이블 분리** | **유지** | 14필드로 과도하지 않고, `@DynamicUpdate`가 이미 적용됨. 분리 시 모든 쿼리에 1:1 JOIN 추가되고, `getCrewDataByPartyroomId()` 같은 복합 QueryDSL의 복잡도 증가. 비용 > 효과 |
| **PartyroomSessionDto** | **유지** | `PartyroomChatService`, `PartyroomSessionCacheManager`, `OutgoingGroupChatMessage`에서 활발히 사용. Redis 세션 캐시 용도로 필수 |

---

## 검증 결과

```
./gradlew clean :app:compileJava                               # BUILD SUCCESSFUL
./gradlew :common:test :playlist:test :user:test :app:test     # BUILD SUCCESSFUL (전체 테스트 통과)
```
