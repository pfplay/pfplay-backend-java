# Domain Models

This document provides comprehensive information about the domain models, their relationships, business rules, and data structures.

## Table of Contents

- [Domain Overview](#domain-overview)
- [User Domain](#user-domain)
- [Party Domain](#party-domain)
- [Playlist Domain](#playlist-domain)
- [Profile Domain](#profile-domain)
- [Avatar Resource Domain](#avatar-resource-domain)
- [Entity Relationships](#entity-relationships)
- [Value Objects](#value-objects)
- [Business Rules](#business-rules)

## Domain Overview

The system is organized into the following domain modules:

```
┌─────────────────┐      ┌─────────────────┐
│   User Domain   │──────│ Profile Domain  │
│ (Member, Guest) │      │  (UserProfile)  │
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
│ Playlist Domain │      │ Avatar Resource │
│ (Playlist,Track)│      │     Domain      │
└─────────────────┘      └─────────────────┘
```

## User Domain

### Member (OAuth-authenticated users)

**Location**: `api/src/main/java/com/pfplaybackend/api/user/member/`

**Data Entity** (`infrastructure/repository/MemberData.java`):
```java
@Entity
@Table(name = "MEMBER")
public class MemberData extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_tier", nullable = false)
    private AuthorityTier authorityTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;  // GOOGLE, TWITTER

    @Column(name = "provider_id")
    private String providerId;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private UserProfileData profile;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<ActivityData> activityDataMap;
}
```

**Business Rules**:
- Email must be unique
- Can only have one OAuth provider per email (for now)
- Authority tier determines permissions (FM > AM > GT)
- Profile is automatically created on first login

**Authority Tiers**:
- **FM (Full Member)**: Full access to all member features
- **AM (Associate Member)**: Limited member features
- **GT (Guest)**: Read-only access (shouldn't appear on Member, but included for consistency)

### Guest (Anonymous users)

**Location**: `api/src/main/java/com/pfplaybackend/api/user/guest/`

**Data Entity** (`infrastructure/repository/GuestData.java`):
```java
@Entity
@Table(name = "GUEST")
public class GuestData extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "agent")
    private String agent;  // User agent string

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_tier", nullable = false)
    private AuthorityTier authorityTier;  // Always GT

    @OneToOne
    @JoinColumn(name = "profile_id")
    private UserProfileData profile;
}
```

**Business Rules**:
- Always have GT (Guest) authority tier
- Cannot create playlists
- Limited party room permissions
- Profile auto-generated with random nickname

### ActivityData (User activities tracking)

**Data Entity**:
```java
@Entity
@Table(name = "ACTIVITY_DATA")
public class ActivityData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MemberData owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type")
    private ActivityType activityType;

    @Column(name = "count")
    private Integer count;
}
```

**Activity Types**:
- DJ_POINT
- LIKE_COUNT
- GRAB_COUNT
- etc.

## Party Domain

### Partyroom (Aggregate Root)

**Location**: `api/src/main/java/com/pfplaybackend/api/party/partyroom/`

**Data Entity** (`infrastructure/repository/PartyroomData.java`):
```java
@Entity
@Table(name = "PARTYROOM")
public class PartyroomData extends BaseEntity {
    @Id
    @Column(name = "partyroom_id")
    private String id;

    @Column(name = "host_id", nullable = false)
    private String hostId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type", nullable = false)
    private StageType stageType;  // MAIN, GENERAL

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "playback_time_limit")
    private Integer playbackTimeLimit;  // in seconds

    @Column(name = "link_domain", unique = true)
    private String linkDomain;

    @Column(name = "notice")
    private String notice;

    @Column(name = "current_playback_id")
    private String currentPlaybackId;

    @Column(name = "is_playback_activated")
    private boolean isPlaybackActivated;

    @Column(name = "is_queue_closed")
    private boolean isQueueClosed;

    @Column(name = "is_terminated")
    private boolean isTerminated;

    @OneToMany(mappedBy = "partyroom", cascade = CascadeType.ALL)
    private Set<CrewData> crewDataSet;

    @OneToMany(mappedBy = "partyroom", cascade = CascadeType.ALL)
    private Set<DjData> djDataSet;
}
```

**Business Rules**:
1. **Creation**:
   - Host must be a Member (not Guest)
   - Link domain must be unique
   - Stage type required (MAIN or GENERAL)

2. **Termination**:
   - Only host can terminate
   - Once terminated, cannot be reactivated
   - All operations fail after termination

3. **Playback**:
   - Only one active playback at a time
   - Must have DJs in queue to start playback
   - Playback time limited by `playbackTimeLimit`

4. **DJ Queue**:
   - Can be opened/closed by host or managers
   - When closed, no new DJs can join
   - Existing DJs remain in queue

**Stage Types**:
- **MAIN**: Featured party rooms (may have special permissions/visibility)
- **GENERAL**: Regular party rooms

### Crew (Party room members)

**Location**: `api/src/main/java/com/pfplaybackend/api/party/crew/`

**Data Entity** (`infrastructure/repository/CrewData.java`):
```java
@Entity
@Table(name = "CREW")
public class CrewData extends BaseEntity {
    @Id
    @Column(name = "crew_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "partyroom_id", nullable = false)
    private PartyroomData partyroom;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false)
    private GradeType gradeType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_banned", nullable = false)
    private boolean isBanned;

    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    @Column(name = "exited_at")
    private LocalDateTime exitedAt;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL)
    private List<CrewGradeHistoryData> gradeHistory;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL)
    private List<CrewPenaltyHistoryData> penaltyHistory;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL)
    private List<CrewBlockHistoryData> blockHistory;
}
```

**Grade Hierarchy** (from highest to lowest):
1. **HOST**: Room owner
   - Full control over room
   - Can modify room settings
   - Can promote/demote any crew
   - Can close/open DJ queue
   - Can terminate room

2. **COMMUNITY_MANAGER**: Senior moderator
   - Can promote/demote up to MODERATOR
   - Can apply penalties
   - Can block users
   - Cannot modify room settings

3. **MODERATOR**: Basic moderator
   - Can apply penalties (chat ban, expulsion)
   - Cannot promote/demote
   - Cannot block users permanently

4. **CLUBBER**: Active participant
   - Can DJ
   - Can chat
   - Can react to music
   - Normal user permissions

5. **LISTENER**: Basic access
   - Can chat
   - Can react to music
   - Cannot DJ

**Business Rules**:
1. **Grade Changes**:
   - Only higher grades can modify lower grades
   - HOST cannot be demoted (unless they transfer ownership)
   - Grade changes recorded in history

2. **Active Status**:
   - `isActive = true` when user is in the room
   - `isActive = false` when user exits
   - Can re-enter room (new Crew entry created)

3. **Ban**:
   - Banned users cannot re-enter room
   - Ban is permanent (until manually unbanned)
   - `isBanned = true` prevents access

4. **Entry/Exit Tracking**:
   - `enteredAt` set when joining
   - `exitedAt` set when leaving
   - Used for analytics and history

### DJ (DJ queue entries)

**Location**: `api/src/main/java/com/pfplaybackend/api/party/dj/`

**Data Entity** (`infrastructure/repository/DjData.java`):
```java
@Entity
@Table(name = "DJ")
public class DjData extends BaseEntity {
    @Id
    @Column(name = "dj_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "partyroom_id", nullable = false)
    private PartyroomData partyroom;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "crew_id", nullable = false)
    private String crewId;

    @Column(name = "playlist_id", nullable = false)
    private String playlistId;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "is_queued", nullable = false)
    private boolean isQueued;
}
```

**Business Rules**:
1. **Queue Management**:
   - DJs ordered by `orderNumber`
   - When DJ finishes, moves to end of queue
   - `orderNumber` adjusted automatically

2. **Queue Status**:
   - `isQueued = true`: In active queue
   - `isQueued = false`: Removed from queue (but record kept)

3. **Playlist Requirement**:
   - Must have at least one track in playlist
   - Playlist must belong to the DJ user

4. **Restrictions**:
   - User must be Crew member (CLUBBER or higher)
   - Cannot join queue if `isQueueClosed = true`
   - Cannot join if already in queue

### Playback (Current/historical playback)

**Location**: `api/src/main/java/com/pfplaybackend/api/party/playback/`

**Data Entity** (`infrastructure/repository/PlaybackData.java`):
```java
@Entity
@Table(name = "PLAYBACK")
public class PlaybackData extends BaseEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "partyroom_id", nullable = false)
    private String partyroomId;

    @Column(name = "user_id", nullable = false)
    private String userId;  // DJ user ID

    @Column(name = "name", nullable = false)
    private String name;  // Track name

    @Column(name = "link_id", nullable = false)
    private String linkId;  // YouTube video ID

    @Column(name = "duration", nullable = false)
    private Integer duration;  // in seconds

    @Column(name = "thumbnail_image")
    private String thumbnailImage;  // YouTube thumbnail URL

    @Column(name = "grab_count")
    private Integer grabCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;

    @Column(name = "end_time")
    private LocalDateTime endTime;  // When playback should end

    @OneToMany(mappedBy = "playback", cascade = CascadeType.ALL)
    private List<PlaybackReactionHistoryData> reactionHistory;
}
```

**Business Rules**:
1. **Lifecycle**:
   - Created when DJ's turn starts
   - `endTime` calculated as `startTime + duration`
   - Becomes historical after playback ends

2. **Reactions**:
   - Each user can react once per playback
   - Counts aggregated in real-time
   - History maintained for analytics

3. **Automatic Progression**:
   - Scheduled task ends playback at `endTime`
   - System automatically starts next DJ's track
   - If queue empty, playback stops

### Reaction (User reactions to playback)

**Location**: `api/src/main/java/com/pfplaybackend/api/party/reaction/`

**Reaction Types**:
```java
public enum ReactionType {
    LIKE,
    DISLIKE,
    GRAB  // Save track to personal playlist
}
```

**History Data**:
```java
@Entity
@Table(name = "PLAYBACK_REACTION_HISTORY")
public class PlaybackReactionHistoryData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playback_id")
    private PlaybackData playback;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type")
    private ReactionType reactionType;

    @Column(name = "reacted_at")
    private LocalDateTime reactedAt;
}
```

**Business Rules**:
1. One reaction per user per playback
2. Changing reaction removes previous reaction
3. GRAB adds track to user's default playlist
4. Reaction counts updated atomically

## Playlist Domain

### Playlist

**Location**: `api/src/main/java/com/pfplaybackend/api/playlist/`

**Data Entity** (`infrastructure/repository/PlaylistData.java`):
```java
@Entity
@Table(name = "PLAYLIST")
public class PlaylistData extends BaseEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PlaylistType type;

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackData> tracks;
}
```

**Playlist Types**:
- **GENERAL**: Standard user-created playlist
- **SYSTEM**: System-generated (e.g., default playlist, grabbed tracks)

**Business Rules**:
1. Only Members can create playlists (Guests cannot)
2. Playlist name must be unique per user
3. Empty playlists are allowed
4. Order number determines display order

### Track

**Data Entity** (`infrastructure/repository/TrackData.java`):
```java
@Entity
@Table(name = "TRACK")
public class TrackData extends BaseEntity {
    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlaylistData playlist;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "link_id", nullable = false)
    private String linkId;  // YouTube video ID

    @Column(name = "duration", nullable = false)
    private Integer duration;  // in seconds

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;
}
```

**Business Rules**:
1. Tracks ordered by `orderNumber` within playlist
2. Duplicate tracks allowed in same playlist
3. Track deletion is soft (orphanRemoval handles cleanup)
4. Reordering updates all affected `orderNumber` values

## Profile Domain

### UserProfile

**Location**: `api/src/main/java/com/pfplaybackend/api/profile/`

**Data Entity** (`infrastructure/repository/UserProfileData.java`):
```java
@Entity
@Table(name = "USER_PROFILE")
public class UserProfileData extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "bio")
    private String bio;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "profile_status")
    private String profileStatus;

    // Avatar URIs
    @Column(name = "avatar_body_resource_uri")
    private String avatarBodyResourceUri;

    @Column(name = "avatar_face_resource_uri")
    private String avatarFaceResourceUri;

    @Column(name = "avatar_icon_resource_uri")
    private String avatarIconResourceUri;

    // Avatar transform parameters
    @Column(name = "avatar_transform_position_x")
    private Double avatarTransformPositionX;

    @Column(name = "avatar_transform_position_y")
    private Double avatarTransformPositionY;

    @Column(name = "avatar_transform_offset_x")
    private Double avatarTransformOffsetX;

    @Column(name = "avatar_transform_offset_y")
    private Double avatarTransformOffsetY;

    @Column(name = "avatar_transform_scale")
    private Double avatarTransformScale;
}
```

**Business Rules**:
1. **Nickname**:
   - Must be unique across all users
   - Auto-generated for new users
   - Can be changed by user

2. **Avatar**:
   - Composed of body, face, and icon
   - Transform parameters for positioning
   - URIs reference avatar resources

3. **Wallet**:
   - Optional blockchain wallet address
   - Used for future NFT/crypto features

## Avatar Resource Domain

### Avatar Resources

**Location**: `api/src/main/java/com/pfplaybackend/api/avatarresource/`

**Types**:
1. **AvatarBodyResource**: Full body avatar
2. **AvatarFaceResource**: Face composition
3. **AvatarIconResource**: Small icon/thumbnail

**Example - AvatarFaceResource**:
```java
@Entity
@Table(name = "AVATAR_FACE_RESOURCE")
public class AvatarFaceResourceData {
    @Id
    @Column(name = "uri")
    private String uri;

    @Enumerated(EnumType.STRING)
    @Column(name = "face_composition_type")
    private FaceCompositionType faceCompositionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "face_source_type")
    private FaceSourceType faceSourceType;

    // Transform parameters
    @Column(name = "transform_position_x")
    private Double transformPositionX;

    @Column(name = "transform_position_y")
    private Double transformPositionY;

    @Column(name = "transform_offset_x")
    private Double transformOffsetX;

    @Column(name = "transform_offset_y")
    private Double transformOffsetY;

    @Column(name = "transform_scale")
    private Double transformScale;
}
```

**Business Rules**:
- Resources are pre-defined (not user-uploaded)
- URI is unique identifier
- Transform parameters define how to render

## Entity Relationships

### Complete ER Diagram

```
MEMBER ──1:1── USER_PROFILE
  │
  │ 1:N
  │
  ├──── PLAYLIST ──1:N── TRACK
  │
  │ 1:N
  │
  └──── ACTIVITY_DATA

GUEST ──1:1── USER_PROFILE

USER (Member or Guest)
  │
  │ creates
  │
PARTYROOM ──1:N── CREW ──1:N── CrewGradeHistory
  │                │         ├── CrewPenaltyHistory
  │                │         └── CrewBlockHistory
  │
  ├──1:N── DJ
  │
  └──1:N── PLAYBACK ──1:N── PlaybackReactionHistory

PLAYLIST ──used by── DJ

AVATAR_BODY_RESOURCE ─┐
AVATAR_FACE_RESOURCE ─┼── referenced by ── USER_PROFILE
AVATAR_ICON_RESOURCE ─┘
```

### Aggregates and Boundaries

**User Aggregate**:
- Root: Member/Guest
- Owned: UserProfile, ActivityData
- Boundary: All user-related data

**Partyroom Aggregate**:
- Root: Partyroom
- Owned: Crew, DJ, Playback
- Boundary: All party room operations

**Playlist Aggregate**:
- Root: Playlist
- Owned: Track
- Boundary: User's music collection

## Value Objects

### UserId
```java
public class UserId {
    private final String value;
    // Validation, equality, immutability
}
```

### PartyroomId
```java
public class PartyroomId {
    private final String value;
}
```

### CrewId, DjId, PlaylistId, TrackId
Similar pattern for all entity identifiers.

### CrewInfo, DjInfo, PlaybackInfo
Value objects for transferring entity information between layers.

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
   - History tables for important changes
   - Who, what, when tracked

4. **Concurrency Control**:
   - Optimistic locking via @Version (where needed)
   - Distributed locks for critical sections
   - Event-driven eventual consistency

5. **Validation**:
   - Domain validation in domain models
   - Input validation in DTOs
   - Database constraints as last line of defense

---

**Related Documents**:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - How entities interact
- [COMMON_TASKS.md](COMMON_TASKS.md) - Working with entities
