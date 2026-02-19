# Business Flows

This document describes the key business flows and processes in the PFPlay backend system.

## Table of Contents

- [User Authentication Flows](#user-authentication-flows)
- [Party Room Flows](#party-room-flows)
- [DJ Queue Flows](#dj-queue-flows)
- [Playback Flows](#playback-flows)
- [Moderation Flows](#moderation-flows)
- [Real-time Messaging Flows](#real-time-messaging-flows)
- [Playlist Flows](#playlist-flows)

## User Authentication Flows

### 1. Member OAuth Login Flow

**Sequence**:

```
Client                  Backend                 OAuth Provider          Redis           Database
  │                       │                           │                   │                │
  │──1. GET /oauth/url───>│                           │                   │                │
  │                       │──2. Generate state────────>│                   │                │
  │                       │<─────store state──────────────────────────────│                │
  │                       │──3. Build auth URL───>│                       │                │
  │<──4. Return URL───────│                           │                   │                │
  │                       │                           │                   │                │
  │──5. Redirect to OAuth Provider────────────────────>│                   │                │
  │<─────6. User authorizes───────────────────────────│                   │                │
  │                       │                           │                   │                │
  │──7. Callback with code & state────────────────────>│                   │                │
  │                       │──8. Validate state───────────────────────────>│                │
  │                       │<─────state valid──────────────────────────────│                │
  │                       │──9. Exchange code for token─────────────────>│                │
  │                       │<─10. Access token────────────────────────────│                │
  │                       │──11. Fetch user info──────────────────────────>│                │
  │                       │<─12. User profile─────────────────────────────│                │
  │                       │──13. Create/Update user────────────────────────────────────────>│
  │                       │<─14. User saved───────────────────────────────────────────────│
  │                       │──15. Generate JWT tokens─>│                   │                │
  │<──16. Set cookies─────│                           │                   │                │
  │    (access_token,     │                           │                   │                │
  │     refresh_token)    │                           │                   │                │
```

**Implementation Details**:

**Step 1-4: Generate OAuth URL**
```java
// PartyroomController.java
@PostMapping("/oauth/url")
public OAuthUrlResponse generateOAuthUrl(@RequestBody OAuthUrlRequest request) {
    ProviderType provider = request.getProvider(); // GOOGLE or TWITTER

    // Generate random state
    String state = UUID.randomUUID().toString();

    // Store state in Redis (expires in 10 minutes)
    redisStateStore.save(state, provider, Duration.ofMinutes(10));

    // Build authorization URL
    String authUrl = oAuthService.buildAuthorizationUrl(provider, state);

    return new OAuthUrlResponse(authUrl);
}
```

**Step 7-16: Handle Callback**
```java
// AuthController.java
@PostMapping("/oauth/callback")
public ResponseEntity<Void> handleCallback(@RequestParam String code,
                                           @RequestParam String state) {
    // Validate state from Redis
    ProviderType provider = redisStateStore.validateAndGet(state);
    if (provider == null) {
        throw new OAuthException(INVALID_STATE);
    }

    // Exchange code for token
    String accessToken = oAuthClient.getAccessToken(provider, code);

    // Fetch user profile
    OAuthUserInfo userInfo = oAuthClient.getUserInfo(provider, accessToken);

    // Create or update member
    Member member = memberService.createOrUpdate(userInfo, provider);

    // Generate JWT tokens
    String jwtAccessToken = jwtService.generateAccessToken(member);
    String jwtRefreshToken = jwtService.generateRefreshToken(member);

    // Set HTTP-only cookies
    ResponseCookie accessCookie = createCookie("access_token", jwtAccessToken, 24 * 3600);
    ResponseCookie refreshCookie = createCookie("refresh_token", jwtRefreshToken, 7 * 24 * 3600);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .build();
}
```

### 2. Guest Sign-in Flow

**Sequence**:

```
Client                  Backend                 Database
  │                       │                         │
  │──1. GET /guests/sign─>│                         │
  │                       │──2. Generate guest ID──>│
  │                       │──3. Create UserProfile─>│
  │                       │──4. Create Guest───────>│
  │                       │<─5. Guest saved────────│
  │                       │──6. Generate JWT───────>│
  │<──7. Set cookies──────│                         │
```

**Implementation**:
```java
@GetMapping("/guests/sign/{agent}")
public ResponseEntity<Void> guestSignIn(@PathVariable String agent) {
    // Create guest with random ID
    Guest guest = guestService.createGuest(agent);

    // Generate JWT
    String accessToken = jwtService.generateAccessToken(guest);

    // Set cookie
    ResponseCookie cookie = createCookie("access_token", accessToken, 24 * 3600);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
}
```

## Party Room Flows

### 1. Create Party Room Flow

**Sequence**:

```
Client                  Backend                      Redis               Database
  │                       │                             │                    │
  │──1. POST /partyrooms─>│                             │                    │
  │                       │──2. Validate host is Member─────────────────────>│
  │                       │──3. Check link unique──────────────────────────>│
  │                       │──4. Create Partyroom───────────────────────────>│
  │                       │──5. Add host as Crew(HOST)────────────────────>│
  │                       │<─6. Saved──────────────────────────────────────│
  │<──7. Return response──│                             │                    │
```

**Implementation**:
```java
@Service
@Transactional
public class PartyroomApplicationService {

    public PartyroomInfoResponse createPartyroom(UserId hostId, PartyroomCreateRequest request) {
        // 1. Validate host is Member
        Member host = memberRepository.findById(hostId)
            .orElseThrow(() -> new UnauthorizedException("Only members can create party rooms"));

        // 2. Validate link domain is unique
        if (partyroomRepository.existsByLinkDomain(request.getLinkDomain())) {
            throw new ConflictException("Link domain already exists");
        }

        // 3. Create partyroom
        Partyroom partyroom = Partyroom.builder()
            .id(PartyroomId.generate())
            .hostId(hostId)
            .title(request.getTitle())
            .stageType(request.getStageType())
            .linkDomain(request.getLinkDomain())
            .build();

        // 4. Save partyroom
        PartyroomData savedData = partyroomRepository.save(partyroom);

        // 5. Add host as Crew with HOST grade
        crewService.addCrew(savedData.getId(), hostId, GradeType.HOST);

        return toResponse(savedData);
    }
}
```

### 2. Join Party Room Flow

**Sequence**:

```
Client                  Backend                      Redis               Database
  │                       │                             │                    │
  │──1. POST /access─────>│                             │                    │
  │                       │──2. Validate room active───────────────────────>│
  │                       │──3. Check not banned───────────────────────────>│
  │                       │──4. Create Crew(LISTENER)──────────────────────>│
  │                       │──5. Publish access event──>│                    │
  │                       │                             │──6. Broadcast─────>│
  │<──7. Return response──│                             │   to all clients   │
```

**Implementation**:
```java
@Service
@Transactional
public class PartyroomAccessApplicationService {

    public CrewInfoResponse accessPartyroom(PartyroomId partyroomId, UserId userId) {
        // 1. Get partyroom
        Partyroom partyroom = partyroomRepository.findById(partyroomId)
            .orElseThrow(() -> new NotFoundException("Partyroom not found"));

        // 2. Validate not terminated
        if (partyroom.isTerminated()) {
            throw new PartyroomException(PARTYROOM_TERMINATED);
        }

        // 3. Check not banned
        if (crewRepository.existsBannedCrew(partyroomId, userId)) {
            throw new ForbiddenException("User is banned from this room");
        }

        // 4. Create crew
        Crew crew = Crew.builder()
            .id(CrewId.generate())
            .partyroomId(partyroomId)
            .userId(userId)
            .gradeType(GradeType.LISTENER)
            .isActive(true)
            .enteredAt(LocalDateTime.now())
            .build();

        CrewData savedCrew = crewRepository.save(crew);

        // 5. Publish event
        AccessEventMessage message = createAccessMessage(savedCrew);
        redisMessagePublisher.publishPartyroomAccess(partyroomId, message);

        return toResponse(savedCrew);
    }
}
```

## DJ Queue Flows

### 1. Join DJ Queue Flow

**Sequence**:

```
Client                  Backend                      Lock                Redis               Database
  │                       │                             │                   │                    │
  │──1. POST /enqueue────>│                             │                   │                    │
  │                       │──2. Acquire lock───────────>│                   │                    │
  │                       │<─3. Lock acquired───────────│                   │                    │
  │                       │──4. Get current queue──────────────────────────────────────────────>│
  │                       │──5. Validate can join──────────────────────────────────────────────>│
  │                       │──6. Calculate order number─>│                   │                    │
  │                       │──7. Create DJ──────────────────────────────────────────────────────>│
  │                       │──8. Release lock───────────>│                   │                    │
  │                       │──9. Publish event──────────────────────────────>│                    │
  │<──10. Return response─│                             │                   │                    │
```

**Implementation**:
```java
@Service
public class DjQueueApplicationService {

    public DjInfoResponse enqueueDj(PartyroomId partyroomId, UserId userId, PlaylistId playlistId) {
        String lockKey = "partyroom:" + partyroomId.getValue() + ":dj-queue";

        return distributedLockExecutor.execute(lockKey, () -> {
            // 1. Validate user is crew
            Crew crew = crewRepository.findActiveCrewByPartyroomAndUser(partyroomId, userId)
                .orElseThrow(() -> new ForbiddenException("Must be crew member"));

            // 2. Validate grade (CLUBBER or higher)
            if (crew.getGradeType().isLowerThan(GradeType.CLUBBER)) {
                throw new ForbiddenException("LISTENER cannot DJ");
            }

            // 3. Validate queue not closed
            Partyroom partyroom = partyroomRepository.findById(partyroomId)
                .orElseThrow(() -> new NotFoundException("Partyroom not found"));

            if (partyroom.isQueueClosed()) {
                throw new DjException(QUEUE_CLOSED);
            }

            // 4. Check not already in queue
            if (djRepository.existsQueuedDj(partyroomId, userId)) {
                throw new ConflictException("Already in queue");
            }

            // 5. Validate playlist has tracks
            Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

            if (playlist.getTracks().isEmpty()) {
                throw new BadRequestException("Playlist must have at least one track");
            }

            // 6. Get next order number
            Integer maxOrder = djRepository.findMaxOrderNumber(partyroomId).orElse(0);
            Integer nextOrder = maxOrder + 1;

            // 7. Create DJ
            Dj dj = Dj.builder()
                .id(DjId.generate())
                .partyroomId(partyroomId)
                .userId(userId)
                .crewId(crew.getId())
                .playlistId(playlistId)
                .orderNumber(nextOrder)
                .isQueued(true)
                .build();

            DjData savedDj = djRepository.save(dj);

            // 8. Publish event
            redisMessagePublisher.publishDjEnqueued(partyroomId, savedDj);

            return toResponse(savedDj);
        });
    }
}
```

### 2. DJ Rotation Flow

**Sequence**:

```
Scheduler               Backend                      Lock                Database
  │                       │                             │                    │
  │──1. Playback ended───>│                             │                    │
  │                       │──2. Acquire lock───────────>│                    │
  │                       │──3. Get current DJ─────────────────────────────>│
  │                       │──4. Move to end of queue───────────────────────>│
  │                       │──5. Get next DJ────────────────────────────────>│
  │                       │──6. Start next playback────────────────────────>│
  │                       │──7. Release lock───────────>│                    │
```

**Implementation**:
```java
@Service
public class DjQueueRotationService {

    public void rotateDj(PartyroomId partyroomId) {
        String lockKey = "partyroom:" + partyroomId.getValue() + ":rotation";

        distributedLockExecutor.execute(lockKey, () -> {
            // 1. Get all queued DJs ordered by orderNumber
            List<Dj> djQueue = djRepository.findQueuedDjsByPartyroom(partyroomId);

            if (djQueue.isEmpty()) {
                // No DJs in queue, stop playback
                stopPlayback(partyroomId);
                return null;
            }

            // 2. Get current DJ (orderNumber = 1)
            Dj currentDj = djQueue.get(0);

            // 3. Move current DJ to end
            Integer maxOrder = djQueue.get(djQueue.size() - 1).getOrderNumber();
            currentDj.setOrderNumber(maxOrder + 1);
            djRepository.save(currentDj);

            // 4. Shift all other DJs up
            for (int i = 1; i < djQueue.size(); i++) {
                Dj dj = djQueue.get(i);
                dj.setOrderNumber(i);
                djRepository.save(dj);
            }

            // 5. Start next DJ's first track
            Dj nextDj = djQueue.get(1);
            playbackService.startPlayback(partyroomId, nextDj);

            return null;
        });
    }
}
```

## Playback Flows

### 1. Start Playback Flow

**Sequence**:

```
Client/System           Backend                      Lock                Redis               Database
  │                       │                             │                   │                    │
  │──1. Start playback───>│                             │                   │                    │
  │                       │──2. Acquire lock───────────>│                   │                    │
  │                       │──3. Get current DJ─────────────────────────────────────────────────>│
  │                       │──4. Get first track────────────────────────────────────────────────>│
  │                       │──5. Create Playback────────────────────────────────────────────────>│
  │                       │──6. Update partyroom───────────────────────────────────────────────>│
  │                       │──7. Release lock───────────>│                   │                    │
  │                       │──8. Publish event──────────────────────────────>│                    │
  │                       │──9. Schedule end task──────────────────────────>│                    │
  │                       │                             │                   │                    │
```

**Implementation**:
```java
@Service
@Transactional
public class PlaybackApplicationService {

    public PlaybackInfoResponse startPlayback(PartyroomId partyroomId) {
        String lockKey = "partyroom:" + partyroomId.getValue() + ":playback";

        return distributedLockExecutor.execute(lockKey, () -> {
            // 1. Get partyroom
            Partyroom partyroom = partyroomRepository.findById(partyroomId)
                .orElseThrow(() -> new NotFoundException("Partyroom not found"));

            // 2. Validate can start playback
            if (partyroom.isPlaybackActivated()) {
                throw new PlaybackException(ALREADY_PLAYING);
            }

            // 3. Get current DJ (lowest orderNumber)
            Dj currentDj = djRepository.findCurrentDj(partyroomId)
                .orElseThrow(() -> new PlaybackException(NO_DJ_IN_QUEUE));

            // 4. Get first track from DJ's playlist
            Track track = trackRepository.findFirstTrackByPlaylist(currentDj.getPlaylistId())
                .orElseThrow(() -> new PlaybackException(EMPTY_PLAYLIST));

            // 5. Calculate end time
            LocalDateTime endTime = LocalDateTime.now().plusSeconds(track.getDuration());

            // 6. Create playback
            Playback playback = Playback.builder()
                .id(PlaybackId.generate())
                .partyroomId(partyroomId)
                .userId(currentDj.getUserId())
                .name(track.getName())
                .linkId(track.getLinkId())
                .duration(track.getDuration())
                .thumbnailImage(track.getThumbnailImage())
                .endTime(endTime)
                .build();

            PlaybackData savedPlayback = playbackRepository.save(playback);

            // 7. Update partyroom
            partyroom.setIsPlaybackActivated(true);
            partyroom.setCurrentPlaybackId(savedPlayback.getId());
            partyroomRepository.save(partyroom);

            // 8. Publish event
            PlaybackStartMessage message = createMessage(savedPlayback, currentDj);
            redisMessagePublisher.publishPlaybackStart(partyroomId, message);

            // 9. Schedule playback end
            taskScheduler.schedulePlaybackEnd(partyroomId, Duration.ofSeconds(track.getDuration()));

            return toResponse(savedPlayback);
        });
    }
}
```

### 2. Skip Playback Flow

**Sequence**:

```
Client                  Backend                      Lock                Redis               Database
  │                       │                             │                   │                    │
  │──1. POST /skip───────>│                             │                   │                    │
  │                       │──2. Validate permissions───────────────────────────────────────────>│
  │                       │──3. Acquire lock───────────>│                   │                    │
  │                       │──4. Stop current playback──────────────────────────────────────────>│
  │                       │──5. Cancel scheduled task──────────────────────>│                    │
  │                       │──6. Rotate DJ──────────────────────────────────────────────────────>│
  │                       │──7. Start next playback────────────────────────────────────────────>│
  │                       │──8. Release lock───────────>│                   │                    │
  │                       │──9. Publish event──────────────────────────────>│                    │
```

**Implementation**:
```java
@Service
public class PlaybackSkipService {

    public void skipPlayback(PartyroomId partyroomId, UserId requesterId) {
        // 1. Validate requester has permission
        Crew requester = crewRepository.findActiveCrewByPartyroomAndUser(partyroomId, requesterId)
            .orElseThrow(() -> new ForbiddenException("Not a crew member"));

        if (!requester.canManagePlayback()) {
            throw new ForbiddenException("Insufficient permissions");
        }

        String lockKey = "partyroom:" + partyroomId.getValue() + ":skip";

        distributedLockExecutor.execute(lockKey, () -> {
            // 2. Get current playback
            Partyroom partyroom = partyroomRepository.findById(partyroomId)
                .orElseThrow(() -> new NotFoundException("Partyroom not found"));

            if (!partyroom.isPlaybackActivated()) {
                throw new PlaybackException(NO_ACTIVE_PLAYBACK);
            }

            // 3. Stop current playback
            playbackRepository.findById(partyroom.getCurrentPlaybackId())
                .ifPresent(playback -> {
                    playback.setEndTime(LocalDateTime.now());
                    playbackRepository.save(playback);
                });

            // 4. Update partyroom
            partyroom.setIsPlaybackActivated(false);
            partyroom.setCurrentPlaybackId(null);
            partyroomRepository.save(partyroom);

            // 5. Cancel scheduled task
            taskScheduler.cancelPlaybackEnd(partyroomId);

            // 6. Publish skip event
            redisMessagePublisher.publishPlaybackSkip(partyroomId, requesterId);

            // 7. Rotate to next DJ
            djQueueRotationService.rotateDj(partyroomId);

            return null;
        });
    }
}
```

### 3. Reaction Flow

**Sequence**:

```
Client                  Backend                      Lock                Redis               Database
  │                       │                             │                   │                    │
  │──1. POST /reaction───>│                             │                   │                    │
  │                       │──2. Acquire lock───────────>│                   │                    │
  │                       │──3. Check existing reaction────────────────────────────────────────>│
  │                       │──4. Update/Add reaction────────────────────────────────────────────>│
  │                       │──5. Update counts──────────────────────────────────────────────────>│
  │                       │──6. Release lock───────────>│                   │                    │
  │                       │──7. Publish reaction───────────────────────────>│                    │
  │                       │──8. Publish motion─────────────────────────────>│                    │
```

**Implementation**:
```java
@Service
public class ReactionApplicationService {

    public void addReaction(PlaybackId playbackId, UserId userId, ReactionType reactionType) {
        String lockKey = "playback:" + playbackId.getValue() + ":reaction";

        distributedLockExecutor.execute(lockKey, () -> {
            // 1. Get playback
            Playback playback = playbackRepository.findById(playbackId)
                .orElseThrow(() -> new NotFoundException("Playback not found"));

            // 2. Check existing reaction
            Optional<PlaybackReactionHistory> existing =
                reactionHistoryRepository.findByPlaybackAndUser(playbackId, userId);

            if (existing.isPresent()) {
                // Remove old reaction count
                decrementCount(playback, existing.get().getReactionType());

                // Update reaction
                existing.get().setReactionType(reactionType);
                reactionHistoryRepository.save(existing.get());
            } else {
                // Create new reaction
                PlaybackReactionHistory history = PlaybackReactionHistory.builder()
                    .playbackId(playbackId)
                    .userId(userId)
                    .reactionType(reactionType)
                    .reactedAt(LocalDateTime.now())
                    .build();
                reactionHistoryRepository.save(history);
            }

            // 3. Increment new reaction count
            incrementCount(playback, reactionType);
            playbackRepository.save(playback);

            // 4. Handle GRAB - add to user's playlist
            if (reactionType == ReactionType.GRAB) {
                playlistService.addTrackToDefaultPlaylist(userId, playback.toTrack());
            }

            // 5. Publish events
            redisMessagePublisher.publishReaction(playback.getPartyroomId(), playbackId, userId, reactionType);
            redisMessagePublisher.publishReactionMotion(playback.getPartyroomId(), userId, reactionType);

            return null;
        });
    }
}
```

## Moderation Flows

### 1. Change Crew Grade Flow

**Sequence**:

```
Client                  Backend                      Redis               Database
  │                       │                             │                    │
  │──1. PUT /grade───────>│                             │                    │
  │                       │──2. Validate permissions───────────────────────>│
  │                       │──3. Change grade───────────────────────────────>│
  │                       │──4. Create history─────────────────────────────>│
  │                       │──5. Publish event──────────>│                    │
```

**Implementation**:
```java
@Service
@Transactional
public class CrewGradeApplicationService {

    public void changeGrade(CrewId crewId, UserId changerId, GradeType newGrade) {
        // 1. Get crew to change
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new NotFoundException("Crew not found"));

        // 2. Get changer
        Crew changer = crewRepository.findActiveCrewByPartyroomAndUser(
            crew.getPartyroomId(), changerId)
            .orElseThrow(() -> new ForbiddenException("Not authorized"));

        // 3. Validate permission
        if (!changer.canChangeGrade(crew, newGrade)) {
            throw new ForbiddenException("Cannot change to this grade");
        }

        // 4. Save old grade
        GradeType oldGrade = crew.getGradeType();

        // 5. Change grade
        crew.setGradeType(newGrade);
        crewRepository.save(crew);

        // 6. Create history
        CrewGradeHistory history = CrewGradeHistory.builder()
            .crewId(crewId)
            .changerId(changerId)
            .oldGrade(oldGrade)
            .newGrade(newGrade)
            .changedAt(LocalDateTime.now())
            .build();
        gradeHistoryRepository.save(history);

        // 7. Publish event
        redisMessagePublisher.publishGradeChange(crew.getPartyroomId(), crewId, oldGrade, newGrade);
    }
}
```

### 2. Apply Penalty Flow

**Penalty Types**:
- **CHAT_MESSAGE_REMOVAL**: Remove specific message
- **CHAT_BAN**: Ban from chat for 30 seconds
- **ONE_TIME_EXPULSION**: Kick from room (can rejoin)
- **PERMANENT_BAN**: Permanent ban (cannot rejoin)

**Implementation**:
```java
@Service
@Transactional
public class CrewPenaltyApplicationService {

    public void applyPenalty(CrewId crewId, UserId enforcerId, PenaltyType penaltyType, String reason) {
        // 1. Validate permissions
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new NotFoundException("Crew not found"));

        Crew enforcer = crewRepository.findActiveCrewByPartyroomAndUser(
            crew.getPartyroomId(), enforcerId)
            .orElseThrow(() -> new ForbiddenException("Not authorized"));

        if (!enforcer.canApplyPenalty(penaltyType)) {
            throw new ForbiddenException("Cannot apply this penalty");
        }

        // 2. Apply penalty
        switch (penaltyType) {
            case CHAT_BAN:
                applyChatBan(crew);
                break;
            case ONE_TIME_EXPULSION:
                expelCrew(crew);
                break;
            case PERMANENT_BAN:
                banCrew(crew);
                break;
        }

        // 3. Create history
        CrewPenaltyHistory history = CrewPenaltyHistory.builder()
            .crewId(crewId)
            .enforcerId(enforcerId)
            .penaltyType(penaltyType)
            .reason(reason)
            .appliedAt(LocalDateTime.now())
            .build();
        penaltyHistoryRepository.save(history);

        // 4. Publish event
        redisMessagePublisher.publishPenalty(crew.getPartyroomId(), crewId, penaltyType);
    }
}
```

## Real-time Messaging Flows

### 1. Chat Message Flow

**Sequence**:

```
Client A                Backend                      Redis               All Clients
  │                       │                             │                    │
  │──1. Send message─────>│                             │                    │
  │   (WebSocket STOMP)   │                             │                    │
  │                       │──2. Validate sender────────>│                    │
  │                       │──3. Check chat ban─────────>│                    │
  │                       │──4. Publish to Redis───────>│                    │
  │                       │                             │──5. Broadcast────>│
  │                       │                             │    to all          │
  │                       │                             │    instances       │
  │<──6. Receive message──────────────────────────────────────────────────────│
```

**Implementation**:

**Send Message** (in `party/adapter/in/stomp/PartyroomChatController.java`):
```java
@Controller
public class PartyroomChatController {

    @MessageMapping("/groups/{chatroomId}/send")
    public void sendGroupMessage(@DestinationVariable String chatroomId,
                                  IncomingGroupChatMessage request,
                                  StompHeaderAccessor accessor) {
        // 1. Extract user from session
        // 2. Validate sender and chat ban
        // 3. Publish to Redis (will be received by all instances)
        redisMessagePublisher.publishChatMessage(chatroomId, message);
    }
}
```

**Receive and Broadcast** (in `party/adapter/in/listener/ChatTopicListener.java`):
```java
@Component
@RequiredArgsConstructor
public class ChatTopicListener implements MessageListener {

    private final SimpMessageSender simpMessageSender;  // from realtime module

    @Override
    public void onMessage(Message message, byte[] pattern) {
        OutgoingGroupChatMessage parsed = deserialize(message);
        // Broadcast via realtime module's SimpMessageSender
        simpMessageSender.sendToTopic(
            "/sub/groups/" + parsed.getChatroomId(),
            parsed
        );
    }
}
```

> **Note**: Chat files were moved from the `liveconnect` package to `party/adapter/in/stomp/` and `party/adapter/in/listener/` as part of Phase 4 refactoring.

## Playlist Flows

### 1. Create Playlist and Add Tracks Flow

**Sequence**:

```
Client                  Backend                      Database
  │                       │                             │
  │──1. POST /playlists──>│                             │
  │                       │──2. Validate is Member─────>│
  │                       │──3. Create Playlist────────>│
  │<──4. Return response──│                             │
  │                       │                             │
  │──5. POST /tracks─────>│                             │
  │                       │──6. Search YouTube API─────>│ (External)
  │                       │──7. Get track metadata─────>│
  │                       │──8. Add Track──────────────>│
  │<──9. Return response──│                             │
```

**Implementation**:
```java
@Service
@Transactional
public class PlaylistApplicationService {

    public PlaylistInfoResponse createPlaylist(UserId userId, String name) {
        // 1. Validate user is Member
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("Only members can create playlists"));

        // 2. Check name unique for user
        if (playlistRepository.existsByOwnerAndName(userId, name)) {
            throw new ConflictException("Playlist name already exists");
        }

        // 3. Get max order number
        Integer maxOrder = playlistRepository.findMaxOrderByOwner(userId).orElse(0);

        // 4. Create playlist
        Playlist playlist = Playlist.builder()
            .id(PlaylistId.generate())
            .ownerId(userId)
            .name(name)
            .type(PlaylistType.GENERAL)
            .orderNumber(maxOrder + 1)
            .build();

        PlaylistData saved = playlistRepository.save(playlist);

        return toResponse(saved);
    }

    public TrackInfoResponse addTrack(PlaylistId playlistId, String linkId) {
        // 1. Get playlist
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // 2. Fetch track metadata from YouTube
        YouTubeVideo video = youTubeService.getVideoInfo(linkId);

        // 3. Get max order number
        Integer maxOrder = trackRepository.findMaxOrderByPlaylist(playlistId).orElse(0);

        // 4. Create track
        Track track = Track.builder()
            .id(TrackId.generate())
            .playlistId(playlistId)
            .name(video.getTitle())
            .linkId(linkId)
            .duration(video.getDuration())
            .thumbnailImage(video.getThumbnail())
            .orderNumber(maxOrder + 1)
            .build();

        TrackData saved = trackRepository.save(track);

        return toResponse(saved);
    }
}
```

---

**Related Documents**:
- [DOMAIN_MODELS.md](DOMAIN_MODELS.md) - Entity details
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns
- [WEBSOCKET_EVENTS.md](WEBSOCKET_EVENTS.md) - Real-time events
- [SECURITY.md](SECURITY.md) - Authentication details
