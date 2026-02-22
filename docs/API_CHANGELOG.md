# API Changelog: refactor/snowflake-id → chore/test-coverage-expansion

> `refactor/snowflake-id` 브랜치 대비 현재 브랜치에서 달라진 REST API 및 WebSocket 이벤트 규격

---

## 1. REST API 변경사항

### 1-1. PlaybackDto — 반응 카운트 필드 제거

`PlaybackDto`에서 `likeCount`, `dislikeCount`, `grabCount` 3개 필드가 제거되었다.
ERD 정규화로 `PlaybackAggregationData` 엔티티가 분리되면서, 반응 집계 데이터는 별도의 `AggregationDto`로 제공된다.

**영향 받는 API**: `GET /api/v1/partyrooms/setup` (DisplayDto.playback)

```
// Before (snowflake-id)
{
  "display": {
    "playbackActivated": true,
    "playback": {
      "id": 1, "linkId": "...", "name": "...",
      "duration": "3:00", "thumbnailImage": "...", "endTime": 999,
      "likeCount": 5,       // REMOVED
      "dislikeCount": 1,    // REMOVED
      "grabCount": 2        // REMOVED
    },
    "reaction": {
      "history": { "isLiked": false, "isDislike": false, "isGrabbed": false },
      "aggregation": { "likeCount": 5, "dislikeCount": 1, "grabCount": 2 }
    }
  }
}

// After (current)
{
  "display": {
    "playbackActivated": true,
    "playback": {
      "id": 1, "linkId": "...", "name": "...",
      "duration": "3:00", "thumbnailImage": "...", "endTime": 999
    },
    "reaction": {
      "history": { "isLiked": false, "isDislike": false, "isGrabbed": false },
      "aggregation": { "likeCount": 5, "dislikeCount": 1, "grabCount": 2 }
    }
  }
}
```

**클라이언트 대응**: `display.playback.likeCount` 대신 `display.reaction.aggregation.likeCount` 사용

---

## 2. WebSocket 이벤트 변경사항

### 2-1. PLAYBACK_START 이벤트 — playback 페이로드 축소

`PlaybackStartMessage.playback` 필드가 `PlaybackDto`(9필드) → `PlaybackSnapshot`(6필드)로 변경되어 반응 카운트가 제거되었다.

**토픽**: `playback_start`

```
// Before (snowflake-id)
{
  "partyroomId": { "id": 10 },
  "eventType": "PLAYBACK_START",
  "crewId": 1,
  "playback": {
    "id": 1, "linkId": "...", "name": "...",
    "duration": "3:00", "thumbnailImage": "...", "endTime": 999,
    "likeCount": 0,       // REMOVED
    "dislikeCount": 0,    // REMOVED
    "grabCount": 0        // REMOVED
  }
}

// After (current)
{
  "partyroomId": { "id": 10 },
  "eventType": "PLAYBACK_START",
  "crewId": 1,
  "playback": {
    "id": 1, "linkId": "...", "name": "...",
    "duration": "3:00", "thumbnailImage": "...", "endTime": 999
  }
}
```

**클라이언트 대응**: 재생 시작 시 카운트는 항상 0이므로, 클라이언트에서 초기값(0, 0, 0)을 설정하면 된다. 이후 `reaction_aggregation` 이벤트로 실시간 업데이트 수신.

---

## 요약

| 구분 | 변경 내용 | 영향 범위 | 원인 |
|------|----------|----------|------|
| REST | `PlaybackDto`에서 `likeCount/dislikeCount/grabCount` 제거 | Setup API 응답 | ERD 정규화 (PlaybackAggregationData 분리) |
| WS | `PlaybackStartMessage.playback`에서 반응 카운트 제거 | playback_start 이벤트 | PlaybackSnapshot 도입 |

> 그 외 class→record 변환에 따른 boolean `is*` 필드 직렬화 차이는 record 필드명에서 `is` prefix를 제거하여 원본 API 규격과 동일하게 복원 완료.
