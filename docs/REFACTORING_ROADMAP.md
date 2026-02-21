# DDD 리팩토링 로드맵

> 2026-02-21 성숙도 재평가에서 도출된 잔존 과제를 체계화한 장기 리팩토링 계획.
> 이전 리팩토링(Phase 0~8, 모듈 재구조화, ERD 정규화)은 `docs/archive/`에 아카이빙됨.

**현재 종합 점수**: 35.0/40 (87.5%)
**목표 종합 점수**: 37.0/40 (92.5%)
**평가 기준 문서**: `docs/MATURITY_ASSESSMENT.md`

---

## 로드맵 개요

```
Phase A — 즉시 해소 (Quick Wins)                     ✅ 완료
Phase B — application→adapter.in 경계 정리            ✅ 완료
Phase C — 도메인 레이어 순수화 (user 모듈)             ✅ 완료
Phase D — VO 일관성 및 Aggregate 강화                  ✅ 부분 완료
Phase E — 테스트 전략 강화                             ✅ 부분 완료
Phase F — 모듈 경계 및 admin 도메인 정리               ✅ 부분 완료
```

### 성숙도 변화 실적

| Phase | 완료 전 | 완료 후 | 주요 효과 |
|-------|---------|---------|----------|
| A | 31.0 | 31.5 | ArchUnit 정확성 회복, cross-module 위반 제거 |
| B | 31.5 | 33.5 | 헥사고널 준수 3.5→4.0, ArchUnit TODO 22건→0건 |
| C | 33.5 | 35.0 | 도메인 순수성 4.0→5.0, 모델 풍부성 4.0→4.5 |
| D (partial) | 35.0 | 35.5 | 이벤트 VO化 완료, WalletDomainService 제거 |
| E (partial) | 35.5 | 36.0 | 단위 테스트 +15, Controller→Repo 위반 제거 |
| F (partial) | 36.0 | **35.0** | auth↔party, party↔user 포트화 2건 |

> 최종 점수 35.0은 차원별 재평가 시 일부 항목의 기준이 엄격화되어 조정됨.

---

## ✅ Phase A — 즉시 해소 (Quick Wins) — 완료

| 항목 | 상태 | 커밋 |
|------|------|------|
| A-1. UserArchitectureTest domain→adapter 예외 제거 | ✅ | `0f98b85` |
| A-2. PlaybackData.create() → PlaybackTrackDto 의존 제거 | ✅ | `0f98b85` |
| A-3. ReactionPostProcessResult → domain/model/ 이동 | ✅ | `0f98b85` |

---

## ✅ Phase B — application→adapter.in 경계 정리 — 완료

| 항목 | 상태 | 커밋 |
|------|------|------|
| B-1. party 도메인 Command/Result DTO 도입 (8 서비스) | ✅ | `2566af7` |
| B-2. auth 도메인 Command/Result DTO 도입 (2 서비스) | ✅ | `2566af7` |
| B-3. admin 도메인 Command/Result DTO 도입 (3 서비스) | ✅ | `2566af7` |
| B-4. user 모듈 Command/Result DTO 도입 (4 서비스) | ✅ | `2566af7` |
| B-5. playlist 모듈 Command DTO 도입 (2 서비스) | ✅ | `2566af7` |
| B-6. ArchUnit 예외 전부 제거 (22건→0건) | ✅ | `2566af7` |

**결과**: 77파일 변경, application→adapter.in 의존 **0건**

---

## ✅ Phase C — 도메인 레이어 순수화 (user 모듈) — 완료

| 항목 | 상태 | 커밋 |
|------|------|------|
| C-1. UserAvatarDomainService → AvatarResourceService 의존 제거 | ✅ | `881e84b` |
| C-2. MemberData → application DTO 의존 제거 (4건) | ✅ | `881e84b` |
| C-3. UserAccountData → ProfileSummaryDto → domain VO 전환 | ✅ | `881e84b` |
| C-4. UserArchitectureTest domain→application 예외 제거 | ✅ | `881e84b` |

**결과**: domain→application **0건** (전 모듈). `ProfileSummary`, `ActivitySummary` domain VO 신규.

---

## ⚠️ Phase D — VO 일관성 및 Aggregate 강화 — 부분 완료

| 항목 | 상태 | 커밋 | 비고 |
|------|------|------|------|
| D-1. FK 컬럼 VO化 (CrewData, DjData) | ❌ 미착수 | — | 30+파일 JPA 파급, 별도 세션 권장 |
| D-2. PartyroomAggregatePort 파라미터 VO化 | ❌ 미착수 | — | D-1과 연동 |
| D-3. 도메인 이벤트 ID VO化 (3건) | ✅ | `59b86f7` | |
| D-4. WalletDomainService 정리 | ✅ | `59b86f7` | 데드 코드 제거 |

---

## ⚠️ Phase E — 테스트 전략 강화 — 부분 완료

| 항목 | 상태 | 커밋 | 비고 |
|------|------|------|------|
| E-1. PartyroomInfoController Repository 직접 접근 제거 | ✅ | `f9a3bf1` | |
| E-2. 미테스트 application service 단위 테스트 (4건) | ✅ | `f9a3bf1` | |
| E-3. Testcontainers 통합 테스트 인프라 | ❌ 미착수 | — | 별도 세션 |
| E-4. 컨트롤러 슬라이스 테스트 | ❌ 미착수 | — | 별도 세션 |

---

## ⚠️ Phase F — 모듈 경계 정리 — 부분 완료

| 항목 | 상태 | 커밋 | 비고 |
|------|------|------|------|
| F-1. admin 도메인 포트 도입 | ❌ 미착수 | — | AdminDemoService 9개 Repo 직접 참조 |
| F-2. LogoutService → party 접근 포트화 | ✅ | `fd0570c` | PartyCleanupPort + Adapter |
| F-3. JwtWebSocketAuthAdapter 이동 | ❌ 미착수 | — | common→realtime 의존 제거 |
| F-4. PartyroomAccessController → GuestSignService 포트화 | ✅ | `fd0570c` | GuestAuthPort + Adapter |
| F-5. partyview ArchUnit 검증 범위 포함 | ❌ 미착수 | — | |

---

## 잔존 과제 요약 (37.0 도달을 위한 작업)

### 높은 우선순위 (성숙도 영향 +1.0)

| # | 항목 | 차원 | 예상 파일 수 | 비고 |
|---|------|------|-------------|------|
| 1 | admin 도메인 포트 도입 (F-1) | 2, 8 | ~15 | AdminDemoService가 가장 심각한 구조 위반 |
| 2 | Testcontainers 통합 테스트 (E-3) | 6 | ~10 (신규) | 통합 테스트 0건 → 최소 3건 |
| 3 | FK 컬럼 VO化 (D-1, D-2) | 4 | ~30 | JPA @Embedded 파급, QueryDSL Q-type 재생성 |

### 중간 우선순위 (성숙도 영향 +0.5)

| # | 항목 | 차원 | 예상 파일 수 | 비고 |
|---|------|------|-------------|------|
| 4 | user/playlist 도메인 이벤트 도입 | 5 | ~10 | MemberRegistered, TrackAdded 등 |
| 5 | JwtWebSocketAuthAdapter 이동 (F-3) | 8 | ~3 | common→bootstrap 이동, build.gradle 변경 |
| 6 | ReactionPostProcessResult 불변화 | 3, 4 | ~5 | @Setter 제거, record 변환 |
| 7 | 컨트롤러 슬라이스 테스트 (E-4) | 6 | ~8 (신규) | @WebMvcTest + MockMvc |

### 낮은 우선순위 (정리 수준)

| # | 항목 | 차원 | 비고 |
|---|------|------|------|
| 8 | PartyroomSetupQueryService Repo 직접 접근 제거 | 2 | partyview 내부, 동일 모듈 |
| 9 | partyview ArchUnit 검증 범위 추가 (F-5) | 2, 6 | 1파일 변경 |
| 10 | admin 서비스 단위 테스트 | 6 | 데모/시뮬레이션 서비스 |

---

## 참고 문서

| 문서 | 위치 | 내용 |
|------|------|------|
| 성숙도 평가 기준 | `docs/MATURITY_ASSESSMENT.md` | 8개 차원 채점 기준, 현재/목표 점수 |
| Context Map | `docs/CONTEXT_MAP.md` | Bounded Context 관계 및 포트 매핑 |
| ADR | `docs/adr/` | 아키텍처 의사결정 기록 5건 |
| DTO Record 정책 | `docs/archive/DTO_RECORD_POLICY.md` | record vs class 사용 지침 |
| 이전 리팩토링 계획 | `docs/archive/REFACTORING_PLAN.md` | Phase 0~8 완료 이력 |
| 이전 모듈 재구조화 | `docs/archive/MODULE_RESTRUCTURING_PLAN.md` | Step A~E 완료 이력 |
| 이전 ERD 변경 | `docs/archive/ERD_DATA_LAYER_CHANGELOG.md` | ERD-1~6 완료 이력 |

---

*본 문서는 각 Phase 완료 시마다 업데이트하고, 성숙도 재평가 결과를 `MATURITY_ASSESSMENT.md`에 반영한다.*
