# API 변경 보고서 (Backend → Frontend 연동용)

> **작성일**: 2026-03-09
> **브랜치**: `chore/test-coverage-expansion`
> **대상**: 프론트엔드 엔지니어

---

## 변경 요약

이번 변경은 크게 **5가지 영역**을 다룹니다:

1. **REST API 경로(URL) 변경** — 동사를 제거하고 리소스 명사 중심으로 전환
2. **HTTP 상태 코드 정리** — RESTful 규약에 맞게 201/204/200 통일
3. **응답 패턴 일관성 확보** — 모든 API의 요청/응답 구조를 통일된 규칙으로 정리
4. **에러 메시지 한국어화** — 모든 도메인 에러 메시지를 한국어로 변환
5. **일부 에러 코드의 HTTP 상태 코드 변경** — 의미에 맞는 상태 코드로 교정

---

## 1. REST API 경로(URL) 변경

REST 리소스 중심 설계로 전환하여 동사(enter/exit/skip)를 제거하고, 리소스 명사로 통일했습니다.

| 기능 | 변경 전 | 변경 후 | 비고 |
|------|---------|---------|------|
| 파티룸 입장 | `POST /api/v1/partyrooms/{id}/enter` | `POST /api/v1/partyrooms/{id}/crews` | 크루 리소스 생성 |
| 파티룸 퇴장 | `POST /api/v1/partyrooms/{id}/exit` | `DELETE /api/v1/partyrooms/{id}/crews/me` | 크루 리소스 삭제 |
| DJ 등록 | `POST /api/v1/partyrooms/{id}/djs` | `POST /api/v1/partyrooms/{id}/dj-queue` | DJ 큐 리소스 생성 |
| DJ 본인 해제 | `DELETE /api/v1/partyrooms/{id}/djs/me` | `DELETE /api/v1/partyrooms/{id}/dj-queue/me` | DJ 큐 리소스 삭제 |
| DJ 강제 해제 | `DELETE /api/v1/partyrooms/{id}/djs/{djId}` | `DELETE /api/v1/partyrooms/{id}/dj-queue/{djId}` | DJ 큐 리소스 삭제 |
| 재생 스킵 | `POST /api/v1/partyrooms/{id}/playbacks/skip` | `DELETE /api/v1/partyrooms/{id}/playbacks/current` | HTTP 메서드도 POST→DELETE 변경 |

---

## 2. HTTP 상태 코드 & 응답 패턴 통일

### 전체 규칙 (RFC 7231 기반)

| 동작 | HTTP 상태 | 응답 구조 | 설명 |
|------|----------|----------|------|
| GET (조회) | 200 OK | `{ "data": { ... } }` | `ApiCommonResponse.success(data)` |
| POST (생성) | 201 Created | `{ "data": { "xxxId": 123 } }` | 생성된 리소스 ID 반환 |
| POST (동작, 데이터 반환) | 200 OK | `{ "data": { ... } }` | OAuth URL/Callback 등 |
| POST (동작, 데이터 없음) | 204 No Content | _(본문 없음)_ | 로그아웃 등 |
| PUT/PATCH (수정, 데이터 없음) | 204 No Content | _(본문 없음)_ | 응답 본문 파싱하지 말 것 |
| DELETE (삭제) | 204 No Content | _(본문 없음)_ | 응답 본문 파싱하지 말 것 |

> **핵심 원칙 (RFC 7231)**: 돌려줄 데이터가 있으면 **200/201 + body**, 없으면 **204 No Content (빈 본문)**. `200 + { "data": null }` 패턴은 안티패턴으로 제거했습니다.

### 변경 상세

#### 2-1. 생성 요청: → 201 Created + 생성된 리소스 ID 반환

| 엔드포인트 | 변경 전 | 변경 후 | 응답 본문 |
|-----------|---------|---------|----------|
| `POST /api/v1/partyrooms` (파티룸 생성) | 200 OK | **201 Created** | `{ "data": { ... } }` |
| `POST /api/v1/partyrooms/{id}/crews` (입장) | 200 OK | **201 Created** | `{ "data": null }` |
| `POST /api/v1/partyrooms/{id}/dj-queue` (DJ 등록) | 200 OK | **201 Created** | `{ "data": { "djId": 123 } }` |
| `POST /api/v1/crews/me/blocks` (크루 차단) | 200 OK | **201 Created** | `{ "data": { "blockId": 123 } }` |
| `POST /api/v1/partyrooms/{id}/penalties` (페널티 부과) | 202 Accepted | **201 Created** | `{ "data": { "penaltyId": 123 } }` |
| `POST /api/v1/playlists/{id}/tracks` (트랙 추가) | _(기존 동일)_ | 201 Created | `{ "data": { "trackId": 456 } }` |

#### 2-2. 삭제 요청: → 204 No Content (본문 없음)

| 엔드포인트 | 변경 전 | 변경 후 |
|-----------|---------|---------|
| `DELETE /api/v1/partyrooms/{id}` (파티룸 삭제) | 200 OK | **204 No Content** |
| `DELETE /api/v1/partyrooms/{id}/crews/me` (퇴장) | 200 OK | **204 No Content** |
| `DELETE /api/v1/partyrooms/{id}/dj-queue/me` (DJ 본인 해제) | 200 OK | **204 No Content** |
| `DELETE /api/v1/partyrooms/{id}/dj-queue/{djId}` (DJ 강제 해제) | 200 OK | **204 No Content** |
| `DELETE /api/v1/partyrooms/{id}/playbacks/current` (재생 스킵) | 200 OK | **204 No Content** |
| `DELETE /api/v1/crews/me/blocks/{blockId}` (차단 해제) | 200 OK | **204 No Content** |
| `DELETE /api/v1/partyrooms/{id}/penalties/{penaltyId}` (페널티 해제) | 202 Accepted | **204 No Content** |
| `DELETE /api/v1/playlists/{id}/tracks/{trackId}` (트랙 삭제) | 202 Accepted | **204 No Content** |
| `DELETE /api/v1/playlists` (플레이리스트 삭제) | 200 OK + 본문 | **204 No Content** |

#### 2-3. 수정 요청: → 204 No Content (본문 없음)

| 엔드포인트 | 변경 전 | 변경 후 |
|-----------|---------|---------|
| `PUT /api/v1/partyrooms/{id}` (파티룸 수정) | 200 OK + `{ "data": null }` | **204 No Content** |
| `PUT /api/v1/partyrooms/{id}/dj-queue` (DJ 큐 업데이트) | 200 OK + `{ "data": null }` | **204 No Content** |
| `PUT /api/v1/partyrooms/{id}/notice` (공지 등록) | 200 OK | **204 No Content** |
| `PATCH /api/v1/partyrooms/{id}/crews/{crewId}/grade` (등급 변경) | 200 OK + `{ "data": null }` | **204 No Content** |
| `PATCH /api/v1/playlists/{id}` (이름 변경) | 200 + `UpdatePlaylistNameResponse` | **204 No Content** — 별도 DTO 제거 |
| `PUT /api/v1/playlists/{id}/tracks/{trackId}` (순서 변경) | 202 Accepted | **204 No Content** |
| `PATCH /api/v1/playlists/{id}/tracks/{trackId}/move` (트랙 이동) | 202 Accepted | **204 No Content** |
| `PUT /api/v1/users/me/profile/avatar` (아바타 변경) | 200 OK + `{ "data": null }` | **204 No Content** |
| `PUT /api/v1/users/me/profile/bio` (자기소개 수정) | 200 OK + `{ "data": null }` | **204 No Content** |

#### 2-4. 동작 요청 (Action POST): → 204 No Content

| 엔드포인트 | 변경 전 | 변경 후 |
|-----------|---------|---------|
| `POST /api/v1/auth/logout` (로그아웃) | 200 OK + `{ "data": null }` | **204 No Content** |

> **프론트엔드 조치**: PUT/PATCH/DELETE 응답 및 로그아웃 시 본문 파싱을 하지 마세요. HTTP 상태 코드(204)로만 성공을 판단하세요.

---

## 3. Auth API 응답 구조 변경

Auth API도 `ApiCommonResponse` 래퍼로 통일했습니다. 기존의 `success`, `message` 필드는 **제거**되었습니다.

### 3-1. OAuth URL 생성 (`POST /api/v1/auth/oauth/url`)

**변경 전:**
```json
{
  "authUrl": "https://accounts.google.com/...",
  "state": "abc123",
  "provider": "google",
  "expiresIn": 300,
  "success": true
}
```

**변경 후:**
```json
{
  "data": {
    "authUrl": "https://accounts.google.com/...",
    "state": "abc123",
    "provider": "google",
    "expiresIn": 300
  }
}
```

### 3-2. OAuth 콜백 로그인 (`POST /api/v1/auth/oauth/callback`)

**변경 전:**
```json
{
  "tokenType": "Cookie",
  "expiresIn": 3600,
  "issuedAt": "2026-03-09T...",
  "success": true,
  "message": "Authentication successful"
}
```

**변경 후:**
```json
{
  "data": {
    "tokenType": "Cookie",
    "expiresIn": 3600,
    "issuedAt": "2026-03-09T..."
  }
}
```

### 3-3. 로그아웃 (`POST /api/v1/auth/logout`)

**변경 전:** 빈 본문 (200 OK)

**변경 후:** 204 No Content _(본문 없음)_

### 3-4. Auth 에러 응답 변경

기존에 Auth API는 자체적으로 `{ "success": false, "message": "..." }` 형태의 에러를 반환했으나, 이제 다른 API와 동일한 글로벌 에러 핸들러를 통해 에러가 반환됩니다.

**신규 에러 코드:**

| 코드 | 메시지 | HTTP | 설명 |
|------|--------|------|------|
| AUTH-003 | 유효하지 않은 OAuth 제공자입니다 | 400 | 잘못된 provider 값 |
| AUTH-004 | 유효하지 않거나 만료된 state 파라미터입니다 | 400 | state 검증 실패 |

> **프론트엔드 조치**: `$.success` / `$.message` 필드 참조를 제거하고, `$.data`에서 실제 응답 데이터를 꺼내도록 변경하세요. 에러 발생 시 HTTP 상태 코드와 에러 응답 구조(`errorCode`, `message`)로 판단하세요.

---

## 4. 제거된 API / 응답 DTO

| 항목 | 설명 |
|------|------|
| `GET /api/v1/users/members/sign` | 미사용 API — 완전 삭제 |
| `DeletePlaylistsResponse` | 삭제 응답에 같은 ID를 되돌려주는 것은 불필요 → 204 빈 본문으로 대체 |
| `UpdatePlaylistNameResponse` | 수정 요청의 같은 이름을 되돌려주는 것은 불필요 → `ApiCommonResponse.ok()` 로 대체 |

---

## 5. 에러 메시지 한국어화

모든 도메인 에러 메시지가 영어에서 한국어로 변경되었습니다. **에러 코드(`errorCode`)는 변경 없음**.

### Partyroom (PTR)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| PTR-001 | Can not find Partyroom | 파티룸을 찾을 수 없습니다 | 404 |
| PTR-002 | Already Terminated Partyroom | 이미 종료된 파티룸입니다 | 403 |
| PTR-003 | Exceeded Entrance Limit | 입장 인원 제한을 초과했습니다 | 403 |
| PTR-004 | Already Active in Another Partyroom | 이미 다른 파티룸에 입장 중입니다 | 403 |
| PTR-005 | Authority Restriction | 권한이 부족합니다 | 403 |
| PTR-006 | Already Host in Another Partyroom | 이미 다른 파티룸의 호스트입니다 | 403 |

> **주의**: 기존 PTR-005 (CACHE_MISSED_SESSION)은 인프라 이슈로 도메인 예외에서 제거됨. PTR-005~007이 PTR-005~006으로 재번호 매김.

### Crew (CRW)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| CRW-001 | Can not find My Active Room | 참여 중인 파티룸을 찾을 수 없습니다 | 404 |
| CRW-002 | Invalid My Active Room | 유효하지 않은 파티룸 참여 상태입니다 | 409 |

### DJ

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| DJ-001 | Already Registered Dj | 이미 DJ로 등록되어 있습니다 | 409 |
| DJ-002 | Dj Queue is Closed | DJ 대기열이 닫혀 있습니다 | 403 |
| DJ-003 | Cannot Register Empty Playlist | 빈 플레이리스트로 등록할 수 없습니다 | 403 |
| DJ-004 | DJ not found in queue | DJ 대기열에서 해당 DJ를 찾을 수 없습니다 | **404** (기존 400) |

### Grade (GRD)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| GRD-001 | Manager grade is required... | 이 작업을 수행하려면 관리자 등급이 필요합니다 | 403 |
| GRD-002 | Unable to set Host | 호스트 등급을 설정할 수 없습니다 | 403 |
| GRD-003 | The current grade level is insufficient... | 현재 등급으로는 해당 작업을 수행할 수 없습니다 | 403 |
| GRD-004 | The specified grade level exceeds... | 지정한 등급이 허용 범위를 초과합니다 | 403 |
| GRD-005 | Guest is Only possible Listener | 게스트는 리스너 등급만 가능합니다 | 403 |

### Block (BLK)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| BLK-001 | No block history found | 차단 이력을 찾을 수 없습니다 | 404 |
| BLK-002 | Crew member is already blocked | 이미 차단된 크루입니다 | **409** (기존 400) |

### Penalty (PNT)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| PNT-001 | Banned User | 이용이 정지된 사용자입니다 | 403 |
| PNT-002 | No penalty history found | 페널티 이력을 찾을 수 없습니다 | 404 |

### Reaction (RCT)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| RCT-001 | Invalid reaction type | 유효하지 않은 리액션 타입입니다 | 403 |

### Auth (AUTH)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| AUTH-001 | Failed to generate code challenge | 코드 챌린지 생성에 실패했습니다 | 400 |
| AUTH-002 | OAuth provider not configured | OAuth 제공자가 설정되지 않았습니다 | 400 |
| AUTH-003 | _(신규)_ | 유효하지 않은 OAuth 제공자입니다 | 400 |
| AUTH-004 | _(신규)_ | 유효하지 않거나 만료된 state 파라미터입니다 | 400 |

### Playlist (PLL)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| PLL-001 | Wallet connection required | 지갑 연결이 필요합니다 | 403 |
| PLL-002 | Playlist limit exceeded | 플레이리스트 생성 한도를 초과했습니다 | 409 |
| PLL-003 | Playlist not found | 플레이리스트를 찾을 수 없습니다 | 404 |

### Track (TRK)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| TRK-001 | Track cannot be added... | 이미 플레이리스트에 존재하는 트랙입니다 | 409 |
| TRK-002 | Track limit exceeded... | 플레이리스트의 트랙 한도를 초과했습니다 | 409 |
| TRK-003 | Track does not exist | 트랙을 찾을 수 없습니다 | 404 |
| TRK-004 | Invalid track order number | 유효하지 않은 트랙 순서입니다 | 400 |

### Avatar (UAV)

| 코드 | 변경 전 | 변경 후 | HTTP |
|------|---------|---------|------|
| UAV-001 | Cannot Select Due To Restrictions | 제한으로 인해 선택할 수 없습니다 | 403 |

---

## 6. ErrorType 매핑 변경 (HTTP 상태 코드 변경)

| 코드 | 변경 전 HTTP | 변경 후 HTTP | 이유 |
|------|-------------|-------------|------|
| BLK-002 | 400 Bad Request | **409 Conflict** | "이미 차단됨"은 중복 충돌 |
| DJ-004 | 400 Bad Request | **404 Not Found** | "DJ를 찾을 수 없음"은 리소스 부재 |

---

## 7. 제거된 에러 코드

| 코드 | 메시지 | 이유 |
|------|--------|------|
| ~~PTR-005~~ | ~~No cached data found for sessionId~~ | 인프라 이슈(Redis 캐시 미스)로 도메인 예외에서 제거 |

> **기존 PTR-005~007 번호가 PTR-005~006으로 재배정**되었습니다.

---

## 프론트엔드 체크리스트

### URL & 메서드 변경
- [ ] API URL 경로 변경 6건 반영 (enter/exit/djs/skip)
- [ ] 재생 스킵: HTTP 메서드 `POST` → `DELETE` 변경

### 상태 코드 & 응답 처리
- [ ] **204 No Content** 응답 시 본문 파싱 건너뛰기 (DELETE 9건 + PUT/PATCH 9건 + 로그아웃 1건 = 총 19개 엔드포인트)
- [ ] **201 Created** 응답 처리 (6개 POST 생성 엔드포인트)
- [ ] POST 생성 응답에서 리소스 ID 추출:
  - DJ 등록: `response.data.djId`
  - 크루 차단: `response.data.blockId`
  - 페널티 부과: `response.data.penaltyId`
  - 트랙 추가: `response.data.trackId`

### Auth API
- [ ] OAuth URL/Callback 응답: `$.data.xxx`로 접근 경로 변경 (기존 `$.xxx`)
- [ ] `$.success`, `$.message` 필드 참조 제거
- [ ] 로그아웃: 200 → **204 No Content** 변경, 본문 파싱 제거
- [ ] Auth 에러: HTTP 상태 코드 + 글로벌 에러 응답 구조로 판단

### 플레이리스트
- [ ] 플레이리스트 삭제: 200 + 본문 → **204 + 빈 본문**
- [ ] 플레이리스트 이름 변경: 200 + 본문 → **204 + 빈 본문**

### 유저 프로필
- [ ] 아바타 변경: 200 → **204 No Content**, 본문 파싱 제거
- [ ] 자기소개 수정: 200 → **204 No Content**, 본문 파싱 제거

### 에러 처리
- [ ] BLK-002: 400 → 409 변경 반영
- [ ] DJ-004: 400 → 404 변경 반영
- [ ] PTR-005~006 에러 코드 재매핑 확인
- [ ] 에러 메시지 표시 로직 확인 (한국어로 변경됨)
- [ ] `GET /api/v1/users/members/sign` 호출 코드 제거 (API 삭제됨)
