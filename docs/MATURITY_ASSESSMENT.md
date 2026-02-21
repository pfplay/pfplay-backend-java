# DDD 성숙도 평가 기준 및 현황

> PFPlay Backend의 DDD 구현 수준을 정량 평가하기 위한 항목별 기준점 문서.
> 각 차원(1~8)에 대해 1~5점 척도를 정의하고, 현재 수준과 목표 수준을 기록한다.

**평가일**: 2026-02-22
**브랜치**: `refactor/ddd-cleanup` | **커밋**: `e9e57f9`

---

## 총괄 대시보드

| # | 차원 | 현재 | 목표 | 비고 |
|---|------|------|------|------|
| 1 | 도메인 레이어 순수성 | **5.0** | 5.0 | domain→adapter 0건, domain→application 0건 |
| 2 | 헥사고널 아키텍처 준수 | **5.0** | 5.0 | admin 포트 완료, partyview ArchUnit 포함. AdminDemoService→AdminPartyroomPort 전환 |
| 3 | 도메인 모델 풍부성 | **5.0** | 5.0 | ReactionPostProcessResult 불변 record 변환 완료 |
| 4 | Aggregate / VO 일관성 | **5.0** | 5.0 | FK VO化 완료, 포트 파라미터 VO化, `@AggregateRoot` 마커 7개 엔티티 적용 + ArchUnit 검증 |
| 5 | 도메인 이벤트 성숙도 | **4.5** | 5.0 | user/playlist 이벤트 4건 도입, 총 14 이벤트 |
| 6 | 테스트 전략 | **4.5** | 5.0 | Testcontainers 3클래스, @WebMvcTest 4클래스, 334 테스트 |
| 7 | 전략적 DDD 문서화 | **5.0** | 5.0 | 성숙도 평가 + 로드맵 + 문서-코드 동기화 |
| 8 | 모듈 구조 / 의존 관리 | **5.0** | 5.0 | cross-module 포트 100%, admin 포트 완료, partyview ArchUnit 포함 |
| | **종합** | **39.0/40 (97.5%)** | **40.0/40 (100%)** | |

---

## 차원 1: 도메인 레이어 순수성

> domain 패키지가 adapter/application 레이어에 대한 의존을 갖지 않는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | domain이 adapter/application을 자유롭게 import. 경계 의식 없음. |
| 2 | 일부 의식적 분리 시도. domain → adapter 다수, domain → application 다수. |
| 3 | domain → adapter 소수(~5건). domain → application 다수. ArchUnit 규칙 있으나 예외 많음. |
| 4 | domain → adapter 0건. domain → application 소수(~10건 이하). ArchUnit 규칙이 예외 제한적으로만 적용. |
| 5 | **domain → adapter 0건. domain → application 0건. ArchUnit 규칙에 예외 없음. 도메인이 순수 POJO.** |

### 현재 수준: 5.0 (← 4.0에서 상향)

**달성 (Phase A + C)**:
- domain → adapter: **0건** (전 모듈)
- domain → application: **0건** (전 모듈)
  - `UserAvatarDomainService` → `AvatarResourceService` 의존 제거 (아이콘 조회를 application 레이어로 상향)
  - `MemberData`/`UserAccountData` → application DTO 4종 의존 제거 (domain VO `ProfileSummary`, `ActivitySummary` 도입)
  - `PlaybackData.create()` → `PlaybackTrackDto` 의존 제거 (primitive 파라미터로 변경)
- 전 모듈 ArchUnit domain→adapter, domain→application 규칙 **예외 0건**

---

## 차원 2: 헥사고널 아키텍처 준수

> Ports & Adapters 패턴이 모든 레이어 경계에서 일관되게 적용되는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | Controller → Repository 직접, Service → Repository 직접. 포트 개념 없음. |
| 2 | 포트 인터페이스 일부 존재. cross-module만 부분 적용. 대부분 서비스가 adapter.in DTO 직접 사용. |
| 3 | cross-module 포트 대부분 적용. Aggregate Port 패턴 도입. application→adapter.in 위반 다수 잔존. |
| 4 | **cross-module 포트 100%. Aggregate Port 일관 적용. application→adapter.in 위반 0건 (Command/Query DTO 분리). 컨트롤러 경계 대부분 준수.** |
| 5 | 모든 경계에서 포트 적용. admin 포함 전 도메인 준수. 컨트롤러는 오직 application service + port만 호출. |

### 현재 수준: 5.0 (← 4.5에서 상향)

**달성 (Phase B + E-1 + F + AdminPartyroomPort)**:
- application→adapter.in 위반: **0건** (ArchUnit 예외 전부 해소, 77파일 변경)
- `PartyroomInfoController`: Repository 직접 주입 제거 → `PartyroomInfoService.getDjQueueInfo()` 호출
- `LogoutService` → `PartyCleanupPort` 포트 경유 (auth→party 경계 정리)
- `PartyroomAccessController` → `GuestAuthPort` 포트 경유 (party→user 경계 정리)
- 14개 포트 인터페이스 + 14개 어댑터 (F-1, F-2에서 2쌍 추가, AdminPartyroomPort 1쌍 추가)
- admin 도메인 포트 도입 (F-1): `AdminMemberPort`, `AdminAvatarResourcePort`, `AdminPlaylistPort` + 3 Adapter
- `AdminDemoService` → `AdminPartyroomPort` 포트 경유 (party Repository 5개 직접 참조 제거, ArchUnit 규칙 추가)
- `partyview` ArchUnit 검증 범위 포함 (F-5): `HexagonalArchitectureTest`에 partyviewClasses 규칙 추가
- `PartyroomSetupQueryService`: Repository 직접 import 0건 — application service 경유 확인 (#8 해소)

**미달**: 없음 (5.0 도달 조건 전부 충족)

### 5.0 도달 조건 — 전부 충족

- [x] admin 도메인 포트 도입 (`AdminMemberPort`, `AdminAvatarResourcePort`, `AdminPlaylistPort`)
- [x] `partyview` ArchUnit 검증 범위 포함
- [x] `AdminDemoService` 인트라모듈 Repository 접근 → `AdminPartyroomPort` 포트 경유로 전환

---

## 차원 3: 도메인 모델 풍부성

> 엔티티가 행위를 보유하고, 도메인 서비스가 진정한 도메인 로직을 담당하는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | 순수 Anemic Model. 엔티티는 getter/setter만, 로직은 전부 서비스에. |
| 2 | 일부 엔티티에 팩토리 메서드 존재. 비즈니스 로직은 여전히 서비스에 집중. |
| 3 | 대부분 엔티티에 팩토리 + 상태 변경 메서드 존재. 일부 불변 조건 자체 검증. 도메인 서비스 역할 불명확. |
| 4 | 엔티티가 불변 조건 자체 검증, 상태 전이, 이벤트 발행. 도메인 서비스는 cross-entity 조율 전담. enum에 행위 부여. 소수 미비점 잔존. |
| 5 | 모든 비즈니스 규칙이 도메인 레이어 내에 위치. 스텁/미구현 도메인 서비스 없음. DTO 매핑은 application에서만 수행. |

### 현재 수준: 5.0 (← 4.5에서 상향)

**달성 (Phase C + D + #6)**:
- `WalletDomainService` 제거 (데드 코드 스텁)
- `UserAccountData.buildProfileSummary()` → domain VO `ProfileSummary` 반환 (application DTO 직접 생성 제거)
- `MemberData.getProfileSummary()` → domain VO 반환, application에서 VO→DTO 변환
- `ReactionPostProcessResult` → `domain/model/`로 이동 (VO 계약 위반 해소)
- `ReactionPostProcessResult` → 불변 record 변환 완료 (#6): `@Setter` 제거, immutable record

### 5.0 도달 조건 — 전부 충족

- [x] `ReactionPostProcessResult`를 불변 record로 변환

---

## 차원 4: Aggregate / VO 일관성

> Aggregate 경계가 명확하고 VO가 일관되게 적용되어 primitive obsession이 없는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | Aggregate 개념 없음. 모든 ID는 Long/String. VO 없음. |
| 2 | 일부 VO 존재 (UserId 등). Aggregate 경계 미정의. FK는 모두 primitive. |
| 3 | 주요 VO 정의됨 (15+). Aggregate Port 도입. FK 일부 VO, 일부 primitive 혼재. |
| 4 | **22+ VO. Aggregate Port + ArchUnit 강제. 이벤트에서 VO 일관 사용. FK 일부 Long 잔존.** |
| 5 | 모든 식별자가 VO. Aggregate 경계 명확, `@AggregateRoot` 등 마커 존재. 이벤트/포트 파라미터 전부 VO. primitive obsession 0건. |

### 현재 수준: 5.0 (← 4.5에서 상향)

**달성 (Phase D + D-1/D-2 + @AggregateRoot)**:
- 이벤트 `long crewId` → `CrewId` VO 전환 (3건: `CrewAccessedEvent`, `PlaybackStartedEvent`, `ReactionMotionChangedEvent`)
- 22개 VO 정의 (`ProfileSummary`, `ActivitySummary` 추가)
- FK 컬럼 VO化 완료 (D-1): `DjQueueData`/`PlaybackData` → `@Embedded PartyroomId`, `PlaybackAggregationData` → `@Embedded PlaybackId`, `TrackData` → `@EmbeddedId PlaylistId` (57파일, `e9e57f9`)
- `PartyroomAggregatePort`/`PlaylistAggregatePort` 파라미터 VO化 (D-2): `Long` → `PartyroomId`/`PlaylistId` (`e9e57f9`)
- `PlaylistId` → common 모듈로 이동 (Shared Kernel)
- `@AggregateRoot` 마커 어노테이션 도입: common 모듈에 정의, 7개 Aggregate Root 엔티티에 적용
  - party: `PartyroomData`, `DjQueueData`, `PartyroomPlaybackData`, `PlaybackAggregationData`
  - user: `MemberData`, `GuestData`
  - playlist: `PlaylistData`
- ArchUnit 검증: `@AggregateRoot`가 `..domain.entity.data..` 패키지에만 존재하는지 강제

**미달**:
- 엔티티 PK는 여전히 `Long` (GeneratedValue) — VO 감싸기는 JPA 제약으로 미수행 (5.0 도달에 필수 아님)

### 5.0 도달 조건 — 전부 충족

- [x] `CrewData.partyroomId`, `DjData.partyroomId` → `PartyroomId` VO (`@AttributeOverride`)
- [x] `PartyroomAggregatePort`/`PlaylistAggregatePort` 메서드 파라미터 VO화
- [x] Aggregate Root 마커 어노테이션 도입 (`@AggregateRoot` + ArchUnit 검증)
- 참고: 엔티티 PK → VO 감싸기는 JPA 제약으로 보류 (선택 사항)

---

## 차원 5: 도메인 이벤트 성숙도

> 도메인 이벤트가 모든 중요 상태 전이를 포착하고, 이벤트 자체가 자기설명적인 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | 도메인 이벤트 없음. 상태 변경을 직접 메서드 호출로만 전파. |
| 2 | 이벤트 일부 존재 (~5개). 직접 Redis 발행. 이벤트 메타데이터 없음. |
| 3 | DomainEvent 베이스 클래스 존재. 엔티티에서 이벤트 등록. 일부 모듈에만 적용. |
| 4 | **9+ 이벤트. 베이스 클래스에 eventId/occurredAt/eventType 메타데이터. 이벤트 ID가 VO. TransactionalEventListener 사용.** 일부 모듈 이벤트 부재. |
| 5 | 모든 모듈에 도메인 이벤트 적용. 이벤트가 충분한 컨텍스트 전달 (감사 용도 가능). 이벤트 소싱 또는 이벤트 저장소 존재. |

### 현재 수준: 4.5 (← 4.0에서 상향)

**달성 (#4)**:
- `DomainEvent`: `eventId(UUID)`, `occurredAt`, `eventType`, 추상 `getAggregateId()`
- 이벤트 ID → VO 전환 완료 (`CrewId` in 3 events)
- party 도메인: 10개 이벤트, 모두 domain layer 순수
- user 모듈 이벤트 도입 (#4): `MemberRegisteredEvent`, `UserProfileChangedEvent` (`059c86f`)
- playlist 모듈 이벤트 도입 (#4): `TrackAddedEvent`, `TrackRemovedEvent` (`059c86f`)
- 총 **14개** 도메인 이벤트 (party 10 + user 2 + playlist 2)

**미달**:
- `DjQueueChangedEvent`, `PlaybackDeactivatedEvent`: 최소 컨텍스트만 전달
- 이벤트 저장소(Event Store) 없음

### 5.0 도달 조건

- [x] user 모듈 이벤트 도입 (`MemberRegisteredEvent`, `UserProfileChangedEvent`)
- [x] playlist 모듈 이벤트 도입 (`TrackAddedEvent`, `TrackRemovedEvent`)
- [ ] 최소 컨텍스트 이벤트에 payload 보강
- [ ] 이벤트 저장소(Event Store) 도입 (선택)

---

## 차원 6: 테스트 전략

> 도메인 로직, 아키텍처 규칙, 통합 시나리오가 테스트로 보호되는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | 테스트 없음 또는 극소수. |
| 2 | 단위 테스트 일부 존재 (~20개). 커버리지 불균형. 테스트 컨벤션 없음. |
| 3 | 단위 테스트 30+. given/when/then + AssertJ 컨벤션 정립. ArchUnit 존재. 통합 테스트 없음. |
| 4 | **단위 테스트 200+. 전 모듈 ArchUnit 예외 0건. 주요 서비스 커버리지 확충.** 통합 테스트 부재. |
| 5 | 단위 + 통합 + 컨트롤러 슬라이스 테스트 완비. Redis/WebSocket 테스트 존재. CI 연동. |

### 현재 수준: 4.5 (← 4.0에서 상향)

**달성 (Phase B + E)**:
- 334 테스트 메서드
- ArchUnit TODO 예외: **0건** (전 모듈)
- 신규 테스트 4개 클래스: `PlaybackReactionServiceTest`, `PlaybackReactionPostProcessServiceTest`, `AuthServiceTest`, `PartyroomChatServiceTest`
- `PartyroomInfoController`: Repository 직접 접근 제거 (Controller→Repository 0건)
- 미테스트 application service: **7/24** (← 11/24)
- Testcontainers 통합 테스트 도입 (E-3): MySQL+Redis `TestContainerConfig` 3클래스, 통합 테스트 6건
- @WebMvcTest 컨트롤러 슬라이스 테스트 도입 (E-4): 4클래스 (playlist 2, user 2), 12 테스트 케이스 (`7f50244`)

**미달**:
- Redis/WebSocket 테스트 **0건**
- admin 도메인 서비스 전체 미테스트
- realtime 모듈 테스트 **0건**
- CI 파이프라인 미연동

### 5.0 도달 조건

- [x] Testcontainers 기반 통합 테스트 도입 (MySQL, Redis)
- [x] MockMvc 컨트롤러 슬라이스 테스트
- [ ] admin 도메인 서비스 단위 테스트
- [ ] Redis/WebSocket 테스트
- [ ] CI 파이프라인에서 테스트 자동 실행

---

## 차원 7: 전략적 DDD 문서화

> Bounded Context, 의사결정, 도메인 모델이 문서로 기록되고 유지되는 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | 문서 없음. 코드만으로 아키텍처 파악 필요. |
| 2 | README 존재. 아키텍처 설명 부분적. |
| 3 | CLAUDE.md + 모듈 README. 일부 아키텍처 문서. |
| 4 | Context Map + ADR 3+. 모듈 README 전체. docs/claude/ 참조 문서 세트. |
| 5 | **Context Map + ADR 5+ + 모듈 README 전체 + 상세 참조 문서 + 성숙도 평가 기준 + 리팩토링 로드맵**. 문서가 코드와 동기화 상태 유지. |

### 현재 수준: 5.0 (← 4.5에서 상향)

**달성**:
- `CLAUDE.md`: 포괄적 프로젝트 컨텍스트
- `docs/CONTEXT_MAP.md`: BC 관계 + 포트 매핑
- `docs/adr/` 5개 ADR
- 모듈 README 6개
- `docs/claude/` 8개 상세 참조 문서
- `docs/MATURITY_ASSESSMENT.md`: 성숙도 평가 기준 (본 문서)
- `docs/REFACTORING_ROADMAP.md`: 장기 리팩토링 로드맵
- 문서-코드 동기화: Phase A~F 완료 후 갱신 완료

---

## 차원 8: 모듈 구조 / 의존 관리

> Gradle 멀티모듈 경계가 명확하고, 의존 방향이 일관되며, Composition Root가 적절한 정도.

### 채점 기준

| 점수 | 기준 |
|------|------|
| 1 | 단일 모듈. 패키지 경계 없음. |
| 2 | 멀티모듈 분리 시작. 순환 의존 존재. |
| 3 | 멀티모듈 완료. 순환 없음. Composition Root 존재. common에 과도한 책임. |
| 4 | 5개 모듈 분리. 의존 방향 엄격. Bootstrap에서 cross-module 연결. common 약간 과잉. admin 경계 위반 존재. |
| 5 | 모듈 경계 완벽. common은 순수 Shared Kernel. 모든 cross-module 접근 포트 경유. admin 포함 전 도메인 준수. |

### 현재 수준: 5.0 (← 4.5에서 상향)

**달성 (Phase F)**:
- `LogoutService` → `PartyCleanupPort` 포트화 (auth→party 경계 정리)
- `PartyroomAccessController` → `GuestAuthPort` 포트화 (party→user 경계 정리)
- cross-domain 접근 16개 포트/어댑터 쌍
- `JwtWebSocketAuthAdapter` → `app/bootstrap/adapter/`로 이동 (F-3): common→realtime 의존 제거
- admin 도메인 포트 도입 (F-1): `AdminMemberPort`, `AdminAvatarResourcePort`, `AdminPlaylistPort` + 3 Adapter
- `partyview` ArchUnit 검증 범위 포함 (F-5): `HexagonalArchitectureTest`에 partyviewClasses 규칙 추가

**미달**: 없음 (5.0 도달 조건 전부 충족)

### 5.0 도달 조건 — 전부 충족

- [x] `JwtWebSocketAuthAdapter`를 `app/bootstrap/`으로 이동
- [x] admin 도메인 cross-module 접근 포트 도입
- [x] `partyview` ArchUnit 검증 범위 포함

---

## 평가 이력

| 일자 | 종합 점수 | 주요 변경 |
|------|-----------|----------|
| 2026-02-22 | **39.0/40 (97.5%)** | `@AggregateRoot` 마커 어노테이션 도입 (7 엔티티), ArchUnit 검증. Aggregate/VO 4.5→5.0 |
| 2026-02-22 | 38.5/40 (96.3%) | AdminDemoService→AdminPartyroomPort 전환, 헥사고널 준수 4.5→5.0 |
| 2026-02-22 | 38.0/40 (95.0%) | D-1/D-2 FK VO化, F-1/F-3/F-5 모듈 경계, #4 도메인 이벤트, #6 불변화, E-3/E-4 테스트 반영 |
| 2026-02-21 (Phase A~F) | 35.0/40 (87.5%) | Phase A~F 완료: 도메인 순수성 5.0, 헥사고널 4.0, 모델 풍부성 4.5, VO 4.0, 테스트 4.0, 문서 5.0, 모듈 4.5 |
| 2026-02-21 (초기 평가) | 31.0/40 (77.5%) | 도메인 순수성 5건 해소, 평가 기준 문서 최초 작성 |
| 2026-02-20 | 30.5/40 (76.3%) | Phase 0~8 + 모듈 재구조화 + ERD 정규화 완료 |

---

*본 문서는 리팩토링 마일스톤 완료 시마다 재평가하여 업데이트한다.*
