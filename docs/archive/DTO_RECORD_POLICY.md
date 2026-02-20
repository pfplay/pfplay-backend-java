# DTO Record vs Class 판단 기준 및 현황

> 프로젝트 전체 DTO/Message/Request/Response 파일에 대한 record/class 사용 정책과 근거를 정리한 문서.

## 판단 기준

### Record 사용 조건 (모두 충족 시 record)

| 조건 | 설명 |
|------|------|
| **불변** | 생성 후 setter 호출 없음 |
| **상속 없음** | record는 `final class` — 상속 불가 |
| **@QueryProjection 미사용** | QueryDSL 생성자 프로젝션은 class 필요 |
| **@Builder.Default 미사용** | record는 Lombok 기본값 패턴 미지원 |
| **중첩 inner class 없음** | inner class 포함 시 class 유지 (가독성) |

### 패키지별 기본 방침

| 패키지 | 기본 타입 | 근거 |
|--------|-----------|------|
| `application/dto/**` | **record** | 레이어 간 불변 전달 객체 |
| `application/dto/command/` | **record** | 명령은 생성 시점에 확정 |
| `application/dto/result/` | **record** | 조회 결과는 불변 |
| `adapter/in/listener/message/` | **record** | 이벤트 메시지는 불변 |
| `adapter/in/web/dto/` (내부 조립용) | **record** | 응답 빌드용 중간 객체, 불변 |
| `common/dto/` | **record** | 범용 불변 DTO |
| `common/config/**/dto/` | **상황별** | 위 기준에 따라 판단 |
| `adapter/in/web/payload/request/` | **class** | Bean Validation + Jackson `@NoArgsConstructor` |
| `adapter/in/web/payload/response/` | **class** | `@Builder` 패턴, 복잡 중첩 구조 |
| `adapter/in/web/dto/` (WebSocket 입력) | **class** | 역직렬화 시 `@Setter` 필요 |

### Class 유지 예외 (패키지 무관)

아래 조건 중 하나라도 해당하면 패키지 기본 방침과 무관하게 class 유지:

- 생성 후 필드 변경 (mutable)
- `@QueryProjection` 사용
- `@Builder.Default` 사용
- 중첩 inner class 포함

---

## 전체 현황

### Record — 51개

#### auth/application/dto/ (2개)

| 파일 | 근거 |
|------|------|
| `OAuthTokenResponse` | 외부 OAuth 응답 매핑, 불변 (`@JsonProperty`) |
| `OAuthUserProfile` | OAuth 사용자 프로필, 불변 |

#### common/ (2개)

| 파일 | 근거 |
|------|------|
| `common/dto/PaginationDto` | 페이지네이션 메타 정보, 불변 (`@Schema`) |
| `common/config/security/jwt/dto/TokenClaimsRequest` | JWT 클레임 값 운반, 불변 |

#### party/adapter/in/listener/message/ (13개)

| 파일 | 근거 |
|------|------|
| `CrewGradeMessage` | 등급 변경 이벤트, 불변 (중첩 record: `AdjusterInfo`, `AdjustedInfo`) |
| `CrewPenaltyMessage` | 제재 이벤트, 불변 (중첩 record: `PunisherInfo`, `PunishedInfo`) |
| `CrewProfileMessage` | 프로필 변경 브로드캐스트, 불변 (`implements GroupBroadcastMessage`) |
| `CrewProfilePreCheckMessage` | 프로필 사전검증 메시지, 불변 |
| `DjQueueChangeMessage` | DJ 큐 변경 이벤트, 불변 (`create()` 팩토리) |
| `OutgoingGroupChatMessage` | 채팅 발신 메시지, 불변 (중첩 record: `CrewInfo`, `ChatContent`) |
| `PartyroomAccessMessage` | 입장/퇴장 이벤트, 불변 (`create()` 팩토리) |
| `PartyroomClosedMessage` | 방 종료 이벤트, 불변 (`implements GroupBroadcastMessage`) |
| `PartyroomDeactivationMessage` | 방 비활성화 이벤트, 불변 (`implements GroupBroadcastMessage`) |
| `PlaybackDurationWaitMessage` | 재생 대기 이벤트, 불변 |
| `PlaybackStartMessage` | 재생 시작 이벤트, 불변 (`implements GroupBroadcastMessage`) |
| `ReactionAggregationMessage` | 반응 집계 이벤트, 불변 (`implements GroupBroadcastMessage`) |
| `ReactionMotionMessage` | 모션 반응 이벤트, 불변 (중첩 record: `CrewMotionInfo`) |

#### party/adapter/in/web/dto/ (1개)

| 파일 | 근거 |
|------|------|
| `PartyroomElement` | 파티룸 목록 응답 조립용 중간 객체, 불변 |

#### party/application/dto/crew/ (3개)

| 파일 | 근거 |
|------|------|
| `CrewDto` | 크루 정보 전달, 불변 |
| `CrewSetupDto` | 초기 설정 전달, 불변 (`from()` 팩토리) |
| `CrewSummaryDto` | 요약 정보, 불변 (`exitOnly()`, `from()` 팩토리) |

#### party/application/dto/dj/ (3개)

| 파일 | 근거 |
|------|------|
| `CurrentDjDto` | 현재 DJ 정보, 불변 |
| `CurrentDjWithProfileDto` | 현재 DJ + 프로필, 불변 |
| `DjWithProfileDto` | DJ + 프로필, 불변 |

#### party/application/dto/partyroom/ (5개)

| 파일 | 근거 |
|------|------|
| `ActivePartyroomDto` | 활성 파티룸 정보, 불변 |
| `ActivePartyroomWithCrewDto` | 활성 파티룸 + 크루, 불변 |
| `PartyroomDto` | 파티룸 기본 정보, 불변 |
| `PartyroomSessionDto` | 세션 정보, 불변 (`Serializable`) |
| `PartyroomWithCrewDto` | 파티룸 + 크루 합성, 불변 (`from()` 팩토리) |

#### party/application/dto/playback/ (5개)

| 파일 | 근거 |
|------|------|
| `AggregationDto` | 반응 집계 수치, 불변 |
| `DisplayDto` | 디스플레이 합성 객체, 불변 |
| `PlaybackHistoryDto` | 재생 이력, 불변 (`@JsonProperty("musicName")`) |
| `PlaybackTrackDto` | 트랙 정보 운반, 불변 |
| `ReactionDto` | 반응 정보, 불변 (`from()` 팩토리) |

#### party/application/dto/result/ (3개)

| 파일 | 근거 |
|------|------|
| `BlockedCrewResult` | 차단 결과, 불변 (`from()` 팩토리) |
| `CrewProfileSummaryResult` | 프로필 요약 결과, 불변 (`from()` 팩토리) |
| `PenaltyResult` | 제재 결과, 불변 (`from()` 팩토리) |

#### playlist/application/dto/ (4개)

| 파일 | 근거 |
|------|------|
| `PlaylistDto` | 플레이리스트 정보, 불변 (`@Schema`) |
| `PlaylistSummary` | 요약 정보, 불변 |
| `PlaylistTrackDto` | 트랙 정보, 불변 |
| `search/SearchResultDto` | 검색 결과 항목, 불변 |
| `search/SearchResultRawData` | 원시 검색 데이터, 불변 |

#### profile/application/dto/ (1개)

| 파일 | 근거 |
|------|------|
| `ProfileChangedEvent` | 프로필 변경 이벤트, 불변 |

#### user/application/dto/command/ (4개)

| 파일 | 근거 |
|------|------|
| `UpdateAvatarBodyCommand` | 아바타 바디 변경 명령, 불변 |
| `UpdateAvatarFaceCommand` | 아바타 얼굴 변경 명령, 불변 |
| `UpdateBioCommand` | 자기소개 변경 명령, 불변 |
| `UpdateWalletCommand` | 지갑 주소 변경 명령, 불변 |

#### user/application/dto/shared/ (5개)

| 파일 | 근거 |
|------|------|
| `ActivitySummaryDto` | 활동 요약, 불변 |
| `AvatarFaceDto` | 아바타 얼굴 정보, 불변 |
| `AvatarIconDto` | 아바타 아이콘 정보, 불변 |
| `ProfileSettingDto` | 프로필 설정, 불변 |
| `ProfileSummaryDto` | 프로필 요약, 불변 |

---

### Class — 유지 (기술적 사유 있음)

#### 가변 상태 (setter 실제 사용)

| 파일 | 사유 |
|------|------|
| `party/.../playback/ReactionPostProcessDto` | 7개 필드 개별 `setter` 호출 (`PlaybackReactionDomainService`) |
| `party/.../web/dto/IncomingGroupChatMessage` | WebSocket 역직렬화 시 `@Setter` 필요 |
| `party/.../web/dto/IncomingPrivateChatMessage` | WebSocket 입력 stub (빈 클래스) |

#### @QueryProjection

| 파일 | 사유 |
|------|------|
| `party/.../playback/PlaybackDto` | QueryDSL 생성자 프로젝션 + 다중 생성자 |

#### @Builder.Default

| 파일 | 사유 |
|------|------|
| `auth/.../response/AuthResponse` | `@Builder.Default` (`tokenType`, `success`) + inner class `UserInfo` + 3개 팩토리 |
| `auth/.../response/OAuthUrlResponse` | `@Builder.Default` (`success=true`) |

#### @Builder(toBuilder=true) / 복합 로직

| 파일 | 사유 |
|------|------|
| `user/.../shared/AvatarBodyDto` | `toBuilder` + `create()` 팩토리 + 혼합 가변성 |

#### 중첩 inner class 응답

| 파일 | 사유 |
|------|------|
| `admin/.../response/AdminPartyroomListResponse` | inner class `PartyroomItem` |
| `admin/.../response/BulkPreviewEnvironmentResponse` | inner class `PartyroomSummary` |
| `admin/.../response/DemoEnvironmentResponse` | inner class `PartyroomDetail` |
| `admin/.../response/SimulateReactionsResponse` | inner class `SimulatedReaction`, `AggregationCounts` |
| `playlist/.../response/SearchMusicResponse` | inner class `MusicData` |

#### Bean Validation Request (20개)

Jackson `@NoArgsConstructor` + Bean Validation 어노테이션 필요:

| 도메인 | 파일 |
|--------|------|
| admin | `AdminCreatePartyroomRequest`, `BulkPreviewEnvironmentRequest`, `InitializeDemoEnvironmentRequest`, `SimulateReactionsRequest`, `StartChatSimulationRequest`, `VirtualMemberAvatarUpdateRequest`, `VirtualMemberCreateRequest` |
| auth | `OAuthLoginRequest`, `OAuthUrlRequest` |
| party | `AddBlockRequest` |
| playlist | `AddTrackRequest`, `CreatePlaylistRequest`, `DeletePlaylistListRequest`, `MoveTrackRequest`, `PaginationRequest`, `SearchMusicListRequest`, `UpdatePlaylistNameRequest`, `UpdateTrackOrderRequest` |
| profile | `SetAvatarRequest`, `AvatarBodyRequest`, `AvatarFaceRequest`, `FaceTransformRequest`, `UpdateMyBioRequest` |
| user | `GetOtherProfileSummaryRequest`, `SignGuestRequest`, `SignMemberRequest`, `UpdateMyAvatarBodyRequest`, `UpdateMyAvatarFaceRequest`, `UpdateMyWalletRequest` |

#### @Builder 응답 DTO (기존 패턴 유지)

`adapter/in/web/payload/response/` 패키지의 응답 DTO는 `@Builder` 패턴을 사용하며, class로 유지:

| 도메인 | 파일 |
|--------|------|
| admin | `AdminPartyroomResponse`, `DemoEnvironmentStatusResponse`, `VirtualMemberResponse` |
| party | `EnterPartyroomResponse`, `PartyroomSharedLinkResponse`, `QueryCrewListResponse`, `QueryDjQueueResponse`, `QueryPartyroomListResponse`, `QueryPartyroomNoticeResponse`, `QueryPartyroomSetupResponse`, `QueryPartyroomSummaryResponse`, `CreatePartyroomResponse` |
| playlist | `CreatePlaylistResponse`, `DeletePlaylistListResponse`, `QueryPlaylistResponse`, `QueryTrackListResponse`, `UpdatePlaylistNameResponse` |
| user | `MyAvatarBodiesResponse`, `MyInfoResponse`, `MyProfileSummaryResponse`, `OtherProfileSummaryResponse`, `SignGuestResponse` |

---

## 접근자 패턴 요약

| 타입 | 접근자 | 예시 |
|------|--------|------|
| record | `.fieldName()` | `dto.nickname()`, `dto.partyroomId()` |
| class (Lombok) | `.getFieldName()` | `data.getName()`, `data.getPartyroomId()` |
| class (boolean, Lombok) | `.isFieldName()` | `data.isActivated()` |
| record (boolean) | `.fieldName()` | `dto.isPlaybackActivated()` — 필드명이 `is`로 시작 시 그대로 |

---

## 최종 수량

| 구분 | 수량 | 비율 |
|------|------|------|
| **record** | 51 | — |
| **class** (기술적 사유) | 7 | 가변, @QueryProjection, @Builder.Default, toBuilder |
| **class** (중첩 inner class) | 5 | 응답에 inner class 포함 |
| **class** (Bean Validation request) | 29 | `@NoArgsConstructor` + validation 필수 |
| **class** (@Builder response) | 22 | 기존 `@Builder` 응답 패턴 |
| **총계** | **114** | — |

---

*최종 갱신: 2026-02-20*
