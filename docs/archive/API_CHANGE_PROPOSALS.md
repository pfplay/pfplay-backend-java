# API Change Proposals

REST API 명세 변경이 필요하나, 이번 리팩토링에서 구현하지 않는 항목.

## 1. Music Search → Track Search

- **현재**: `GET /api/v1/music-search`
- **제안**: `GET /api/v1/tracks/search`
- **이유**: 내부 엔티티명 "Track"과 일관성 확보
- **영향**: 프론트엔드 API 호출 변경 필요
- **관련 클래스**: `MusicSearchController` → `TrackSearchController`

## 2. SearchMusicResponse → SearchTrackResponse

- **현재**: JSON 응답 내 구조가 "Music" 기반 (`musicList`, `MusicData`)
- **제안**: "Track" 기반으로 변경 (`trackList`, `TrackData`)
- **이유**: 내부 명명법과 일관성
- **관련 파일**: `SearchMusicResponse.java`, `SearchMusicListRequest.java`
