# Domain Models

This document provides comprehensive information about the domain entities, their relationships, business rules, and data structures.

## Table of Contents

- [Domain Overview](#domain-overview)
- [User Domain](#user-domain)
- [Party Domain](#party-domain)
- [Playlist Domain](#playlist-domain)
- [Profile Domain](#profile-domain)
- [Auth Domain](#auth-domain)
- [Entity Relationships](#entity-relationships)
- [Value Objects](#value-objects)
- [Business Rules](#business-rules)

## Domain Overview

The system is organized into the following domain modules within the `api` Gradle module, plus an independent `realtime` module:

```
┌─────────────────┐      ┌─────────────────┐
│   User Domain   │──────│ Profile Domain  │
│ (Member, Guest) │      │  (ProfileData)  │
└────────┬────────┘      └─────────────────┘
         │
         │ owns
         │
┌────────▼────────────────────────────────────┐
│           Party Domain                      │
│  (Partyroom, Crew, DJ, Playback, Reaction) │
└────────┬────────────────────────────────────┘
         │
         │ uses
         │
┌────────▼────────┐      ┌─────────────────┐
│ Playlist Domain │      │ Auth Domain     │
│ (Playlist,Track)│      │ (OAuth, JWT)    │
└─────────────────┘      └─────────────────┘

                          ┌─────────────────┐
                          │ realtime module │
                          │ (WebSocket/STOMP)│
                          └─────────────────┘
```

> **Note**: After Phase 1 refactoring, domain models and JPA entities were merged. All entities use the `*Data.java` naming convention and contain both persistence annotations and business logic. There are no separate `domainmodel/` classes or Converter classes.

## User Domain

### Package Structure

```
api/.../user/
├── adapter/in/web/         # Controllers (GuestSignController, MemberSignController, etc.)
├── adapter/out/persistence/ # MemberRepository, GuestRepository + QueryDSL impls
├── application/service/     # GuestSignService, MemberSignService, UserInfoService, etc.
├── domain/entity/data/      # MemberData, GuestData, ActivityData, AvatarResource entities
├── domain/enums/            # ActivityType, ObtainmentType, PairType
├── domain/service/          # GuestDomainService, UserDomainService, UserAvatarDomainService
├── domain/value/            # UserId, AvatarBodyUri, AvatarFaceUri, WalletAddress
└── domain/exception/        # UserAvatarException
```

### MemberData (OAuth-authenticated users)

**Location**: `user/domain/entity/data/MemberData.java`

```java
@Entity
@Table(name = "MEMBER")
public class MemberData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileData profileData;

    @OneToMany(mappedBy = "memberData", cascade = CascadeType.ALL)
    private Set<ActivityData> activityDataMap;
}
```

**Business Rules**:
- Email must be unique
- Authority tier determines permissions (FM > AM > GT)
- Profile is automatically created on first login
- ActivityData tracks DJ_POINT, LIKE_COUNT, GRAB_COUNT, etc.

### GuestData (Anonymous users)

**Location**: `user/domain/entity/data/GuestData.java`

```java
@Entity
@Table(name = "GUEST")
public class GuestData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId userId;

    private String agent;  // User agent string

    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;  // Always GT

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileData profileData;
}
```

**Business Rules**:
- Always have GT (Guest) authority tier
- Cannot create playlists
- Limited party room permissions
- Profile auto-generated with random nickname

### QueryDSL: User Repositories

Both `MemberRepositoryImpl` and `GuestRepositoryImpl` use `leftJoin().fetchJoin()` to eagerly load profile and activity data in a single query, avoiding N+1 problems.

## Party Domain

### Package Structure

```
api/.../party/
├── adapter/
│   ├── in/
│   │   ├── web/             # 11 controllers (Access, Info, Management, Crew*, Dj*, Playback*, etc.)
│   │   ├── listener/        # Redis topic listeners + message DTOs
│   │   └── stomp/           # PartyroomChatController (WebSocket chat)
│   └── out/
│       ├── persistence/     # PartyroomRepository, CrewRepository, DjRepository, PlaybackRepository + impls
│       └── external/        # Cross-domain port adapters
├── application/
│   ├── service/             # Application services + lock/ + task/ + cache/
│   ├── port/out/            # Outbound port interfaces (ProfileQueryPort, etc.)
│   ├── dto/                 # crew/, dj/, partyroom/, playback/, result/ DTOs
│   └── aspect/              # PartyroomContextAspect
├── domain/
│   ├── entity/data/         # PartyroomData, CrewData, DjData, PlaybackData + history/ entities
│   ├── service/             # PlaybackDomainService, PlaybackReactionDomainService
│   ├── enums/               # GradeType, StageType, PenaltyType, ReactionType, etc.
│   ├── value/               # PartyroomId, CrewId, DjId, PlaybackId, PlaylistId
│   ├── model/               # ReactionState, ReactionStateResolver
│   └── exception/           # PartyroomException, CrewException, DjException, etc.
```

### PartyroomData

```java
@Entity
@Table(name = "PARTYROOM")
public class PartyroomData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId hostId;

    @Enumerated(EnumType.STRING)
    private StageType stageType;  // MAIN, GENERAL

    private String title;
    private String introduction;
    private Integer playbackTimeLimit;
    private String linkDomain;
    private String notice;

    @Embedded
    private PlaybackId currentPlaybackId;

    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;
}
```

**Business Rules**:
1. Host must be a Member (not Guest)
2. Link domain must be unique
3. Only one active playback at a time
4. Once terminated, cannot be reactivated

### CrewData

```java
@Entity
@Table(name = "CREW")
public class CrewData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyroom_id")
    private PartyroomData partyroomData;

    @Embedded
    private UserId userId;

    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @Enumerated(EnumType.STRING)
    private GradeType gradeType;

    private boolean isActive;
    private boolean isBanned;
    private LocalDateTime enteredAt;
    private LocalDateTime exitedAt;
}
```

**Grade Hierarchy** (highest to lowest):
1. **HOST**: Room owner — full control
2. **COMMUNITY_MANAGER**: Senior moderator — promote/demote up to MODERATOR
3. **MODERATOR**: Basic moderator — apply penalties
4. **CLUBBER**: Active participant — can DJ and chat
5. **LISTENER**: Basic access — chat and react only

### DjData

```java
@Entity
@Table(name = "DJ")
public class DjData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyroom_id")
    private PartyroomData partyroomData;

    @Embedded
    private UserId userId;

    @Embedded
    private CrewId crewId;

    @Embedded
    private PlaylistId playlistId;

    private Integer orderNumber;
    private boolean isQueued;
}
```

### PlaybackData

```java
@Entity
@Table(name = "PLAYBACK")
public class PlaybackData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private PartyroomId partyroomId;  // @Embedded VO, not FK join

    @Embedded
    private UserId userId;

    private String name;
    private String linkId;
    private Integer duration;
    private String thumbnailImage;
    private Integer grabCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime endTime;
}
```

**QueryDSL note**: `getRecentPlaybackHistory()` queries `PlaybackData` directly using `partyroomId.id` (embedded VO), without joining the PARTYROOM table.

## Playlist Domain

### Package Structure

```
api/.../playlist/
├── adapter/in/web/          # PlaylistCommandController, PlaylistQueryController, Track*, MusicSearch*
├── adapter/out/persistence/ # PlaylistRepository, TrackRepository + QueryDSL impls
├── application/service/     # PlaylistCommandService, PlaylistQueryService, Track*, GrabMusic*, search/
├── application/dto/         # PlaylistSummary, PlaylistMusicDto, search/
├── domain/entity/data/      # PlaylistData, TrackData
├── domain/enums/            # PlaylistType, PlaylistOrder
├── domain/service/          # PlaylistDomainService, MusicSearchDomainService
├── domain/value/            # MusicMetadata
└── domain/exception/        # PlaylistException, TrackException
```

### PlaylistData

```java
@Entity
@Table(name = "PLAYLIST")
public class PlaylistData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId ownerId;

    private String name;

    @Enumerated(EnumType.STRING)
    private PlaylistType type;  // GENERAL, SYSTEM

    private Integer orderNumber;
}
```

### TrackData

```java
@Entity
@Table(name = "TRACK")
public class TrackData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private PlaylistData playlistData;

    private String name;
    private String linkId;
    private Integer duration;
    private String thumbnailImage;
    private Integer orderNumber;
}
```

## Profile Domain

### Package Structure

```
api/.../profile/
├── adapter/in/web/          # UserAvatarController, UserBioController, UserProfileController
├── adapter/out/persistence/ # UserProfileRepository
├── application/service/     # UserAvatarService, UserBioService, UserProfileService
├── application/event/       # UserProfileEventService
├── domain/                  # ProfileData.java (entity)
├── domain/enums/            # AvatarCompositionType, FaceSourceType, ProfileStatus
└── domain/vo/               # Avatar, AvatarBody, AvatarFace, AvatarIcon, FaceTransform
```

### ProfileData

```java
@Entity
@Table(name = "USER_PROFILE")
public class ProfileData {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId userId;

    private String nickname;
    private String introduction;
    private String bio;
    private String walletAddress;
    private String profileStatus;

    // Avatar URIs and transform parameters
    private String avatarBodyResourceUri;
    private String avatarFaceResourceUri;
    private String avatarIconResourceUri;
    // ... transform position/offset/scale fields
}
```

## Auth Domain

### Package Structure

```
api/.../auth/
├── adapter/in/web/              # AuthController + request/response DTOs
├── adapter/out/external/        # GoogleOAuthClient, TwitterOAuthClient + WebClientConfig
├── adapter/out/persistence/     # RedisStateStoreAdapter (implements StateStorePort)
├── application/service/         # AuthService, OAuthClientService, OAuthUrlService, LogoutService
├── application/port/out/        # StateStorePort
└── domain/enums/                # OAuthProvider
```

**Port/Adapter Pattern**: `StateStorePort` defines the interface for OAuth state storage; `RedisStateStoreAdapter` implements it using Redis.

## Entity Relationships

### Complete ER Diagram

```
MEMBER ──1:1── PROFILE_DATA
  │                ↑
  │ 1:N            │ 1:1
  │                │
  ├──── ACTIVITY_DATA
  │
  └──── PLAYLIST ──1:N── TRACK

GUEST ──1:1── PROFILE_DATA

USER (Member or Guest)
  │
  │ creates
  │
PARTYROOM
  │
  ├──referenced by── CREW ──1:N── CrewGradeHistory
  │                    │         ├── CrewPenaltyHistory
  │                    │         └── CrewBlockHistory
  │
  ├──referenced by── DJ
  │
  └──referenced by── PLAYBACK ──1:N── PlaybackReactionHistory

PLAYLIST ──used by── DJ

AVATAR_BODY_RESOURCE ─┐
AVATAR_FACE_RESOURCE ─┼── referenced by ── PROFILE_DATA
AVATAR_ICON_RESOURCE ─┘
```

### Aggregates and Boundaries

After Phase 2 refactoring, Crew and DJ are **independent aggregates** (not owned by Partyroom):

| Aggregate | Root | Owned Entities | Repository |
|-----------|------|----------------|------------|
| **User** | MemberData / GuestData | ProfileData, ActivityData | MemberRepository, GuestRepository |
| **Partyroom** | PartyroomData | — (standalone) | PartyroomRepository |
| **Crew** | CrewData | GradeHistory, PenaltyHistory, BlockHistory | CrewRepository |
| **DJ** | DjData | — (standalone) | DjRepository |
| **Playback** | PlaybackData | ReactionHistoryData | PlaybackRepository |
| **Playlist** | PlaylistData | TrackData | PlaylistRepository |

Crew/DJ have `@ManyToOne` FK to PartyroomData but are accessed via their own independent repositories.

## Value Objects

### Embedded Value Objects (JPA @Embeddable)

| Value Object | Type | Usage |
|---|---|---|
| `UserId` | UUID | All user references |
| `PartyroomId` | Long wrapper | Partyroom references |
| `CrewId` | Long wrapper | Crew references |
| `DjId` | Long wrapper | DJ references |
| `PlaybackId` | Long wrapper | Playback references |
| `PlaylistId` | Long wrapper | Playlist references |

**Usage**: `new UserId()` generates a random UUID. Value objects are `@Embeddable` and used as `@Embedded` fields in entities.

### Domain Value Objects

| Value Object | Domain | Purpose |
|---|---|---|
| `AvatarBodyUri`, `AvatarFaceUri`, `AvatarIconUri` | user | Avatar resource references |
| `WalletAddress` | user | Blockchain wallet address |
| `MusicMetadata` | playlist | Track metadata from search |
| `Avatar`, `AvatarBody`, `AvatarFace`, `FaceTransform` | profile | Avatar composition VOs |

## Business Rules

### Cross-Cutting Rules

1. **Authority Tier Enforcement**:
   - FM: Full member features
   - AM: Limited member features
   - GT: Guest features only

2. **Soft Delete Pattern**:
   - Entities marked inactive rather than deleted
   - Historical data preserved
   - Queries filter by active status

3. **Audit Trail**:
   - All entities extend BaseEntity (createdAt, updatedAt)
   - History tables for grade changes, penalties, blocks, reactions

4. **Concurrency Control**:
   - Distributed locks via Redis for critical sections
   - Event-driven eventual consistency

5. **Validation**:
   - Domain validation in entity methods
   - Input validation in request DTOs
   - Database constraints as last line of defense

---

**Last Updated**: 2026-02-20

**Related Documents**:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - How entities interact
- [COMMON_TASKS.md](COMMON_TASKS.md) - Working with entities
