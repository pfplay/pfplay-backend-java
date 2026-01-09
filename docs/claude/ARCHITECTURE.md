# Architecture Deep Dive

This document provides detailed explanation of the architectural patterns, design decisions, and implementation details of the PFPlay backend.

## Table of Contents

- [Layered Architecture](#layered-architecture)
- [Domain-Driven Design](#domain-driven-design)
- [Event-Driven Architecture](#event-driven-architecture)
- [Distributed Systems Patterns](#distributed-systems-patterns)
- [Data Flow](#data-flow)
- [Scalability Considerations](#scalability-considerations)

## Layered Architecture

### Overview

The system follows a strict 4-layer architecture with clear dependency rules:

```
┌─────────────────────────────────────────────────┐
│         Presentation Layer                      │
│   (Controllers, DTOs, Request/Response)         │
│                                                 │
│   Dependencies: ↓ Application Layer             │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Application Layer                       │
│   (Application Services, Use Case Orchestration)│
│                                                 │
│   Dependencies: ↓ Domain Layer                  │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Domain Layer                            │
│   (Domain Models, Business Logic, Events)       │
│                                                 │
│   Dependencies: None (Pure business logic)      │
└─────────────────────────────────────────────────┘
                      ↑
┌─────────────────────────────────────────────────┐
│         Infrastructure Layer                    │
│   (Repositories, External Services, Messaging)  │
│                                                 │
│   Dependencies: → Domain Layer                  │
└─────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### 1. Presentation Layer
**Location**: `presentation/` package in each domain

**Responsibilities**:
- HTTP request/response handling
- Input validation
- DTO transformation
- Exception handling
- Security enforcement (@PreAuthorize)

**What belongs here**:
- `*Controller.java` - REST controllers
- `*Request.java` - Request DTOs
- `*Response.java` - Response DTOs

**Example**:
```java
api/src/main/java/com/pfplaybackend/api/party/partyroom/presentation/
├── PartyroomController.java
├── dto/
│   ├── request/
│   │   ├── PartyroomCreateRequest.java
│   │   └── PartyroomUpdateRequest.java
│   └── response/
│       ├── PartyroomInfoResponse.java
│       └── PartyroomSummaryResponse.java
```

**Key Patterns**:
- Controllers are thin, delegating to application services
- DTOs are anemic (no business logic)
- Validation via `@Valid` and Bean Validation
- Exception handling via `@ExceptionHandler`

#### 2. Application Layer
**Location**: `application/service/` package in each domain

**Responsibilities**:
- Use case orchestration
- Transaction management
- Domain service coordination
- Event publishing
- Cross-domain coordination

**What belongs here**:
- `*ApplicationService.java` - Application services
- Transaction boundaries (@Transactional)
- Converter classes for DTO ↔ Domain mapping

**Example**:
```java
api/src/main/java/com/pfplaybackend/api/party/partyroom/application/service/
├── PartyroomApplicationService.java
├── PartyroomQueryService.java
└── converter/
    ├── PartyroomConverter.java
    └── CrewConverter.java
```

**Key Patterns**:
- One application service per aggregate root
- Each method represents a use case
- @Transactional at this layer
- Publishes events after successful operations

**Example Flow**:
```java
@Service
@Transactional
public class PartyroomApplicationService {

    public PartyroomInfoResponse createPartyroom(PartyroomCreateRequest request) {
        // 1. Convert DTO to domain model
        Partyroom partyroom = converter.toDomain(request);

        // 2. Execute business logic (domain service)
        partyroom.initialize(hostUserId);

        // 3. Persist (infrastructure)
        PartyroomData savedData = partyroomRepository.save(converter.toData(partyroom));

        // 4. Publish events (infrastructure)
        eventPublisher.publishPartyroomCreated(savedData.getId());

        // 5. Convert back to DTO
        return converter.toResponse(savedData);
    }
}
```

#### 3. Domain Layer
**Location**: `domain/` package in each domain

**Responsibilities**:
- Business logic and rules
- Domain model behavior
- Domain events
- Business validation
- Pure logic (no infrastructure dependencies)

**What belongs here**:
- `domainmodel/` - Rich domain models
- `service/` - Domain services
- `enums/` - Domain enums
- `valueobject/` - Value objects

**Example**:
```java
api/src/main/java/com/pfplaybackend/api/party/partyroom/domain/
├── domainmodel/
│   ├── Partyroom.java
│   └── Crew.java
├── service/
│   └── PartyroomDomainService.java
├── enums/
│   ├── StageType.java
│   └── GradeType.java
└── valueobject/
    ├── PartyroomId.java
    └── CrewId.java
```

**Key Patterns**:
- Rich domain models with behavior
- Value objects for type safety
- Domain services for cross-entity operations
- No infrastructure dependencies

**Domain Model Example**:
```java
public class Partyroom {
    private PartyroomId id;
    private UserId hostId;
    private String title;
    private StageType stageType;
    private boolean isTerminated;
    private Set<Crew> crews;

    // Business logic methods
    public void terminate() {
        if (isTerminated) {
            throw new PartyroomException(ALREADY_TERMINATED);
        }
        this.isTerminated = true;
    }

    public void addCrew(Crew crew) {
        validateCanAddCrew(crew);
        crews.add(crew);
    }

    private void validateCanAddCrew(Crew crew) {
        if (isTerminated) {
            throw new PartyroomException(PARTYROOM_TERMINATED);
        }
        if (crews.size() >= MAX_CREW_SIZE) {
            throw new PartyroomException(CREW_LIMIT_EXCEEDED);
        }
    }
}
```

#### 4. Infrastructure Layer
**Location**: `infrastructure/repository/` package in each domain

**Responsibilities**:
- Data persistence
- External service integration
- Messaging infrastructure
- Caching

**What belongs here**:
- `*Data.java` - JPA entities (anemic)
- `*Repository.java` - Spring Data repositories
- Custom repository implementations
- External API clients

**Example**:
```java
api/src/main/java/com/pfplaybackend/api/party/partyroom/infrastructure/repository/
├── PartyroomData.java           // JPA entity
├── PartyroomRepository.java     // Spring Data JPA
├── PartyroomQueryRepository.java // QueryDSL custom queries
└── converter/
    └── PartyroomDataConverter.java // Domain ↔ Data conversion
```

**Key Patterns**:
- JPA entities are anemic (just getters/setters)
- Use QueryDSL for complex queries
- Converter translates Domain Model ↔ Data Entity
- Repository interface exposed to application layer

**Data Entity Example**:
```java
@Entity
@Table(name = "PARTYROOM")
@Getter @Setter
public class PartyroomData extends BaseEntity {
    @Id
    @Column(name = "partyroom_id")
    private String id;

    @Column(name = "host_id", nullable = false)
    private String hostId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type")
    private StageType stageType;

    @Column(name = "is_terminated")
    private boolean isTerminated;

    @OneToMany(mappedBy = "partyroom", cascade = CascadeType.ALL)
    private Set<CrewData> crewDataSet = new HashSet<>();
}
```

### Dependency Rules

**Strict Rules**:
1. **Presentation** depends on **Application** only
2. **Application** depends on **Domain** only
3. **Domain** has NO dependencies (pure business logic)
4. **Infrastructure** depends on **Domain** (implements interfaces)

**Violated Pattern Detection**:
- Domain layer importing Spring, JPA, or other framework annotations = VIOLATION
- Domain layer importing infrastructure classes = VIOLATION
- Infrastructure layer not implementing domain interfaces = CODE SMELL

## Domain-Driven Design

### Separation of Concerns: Domain vs Data

This is a critical pattern in this codebase:

**Domain Model** (Business Logic)
```java
// Location: domain/domainmodel/Partyroom.java
public class Partyroom {
    // Rich behavior
    public void startPlayback(Track track, UserId djUserId) {
        validateCanStartPlayback(djUserId);
        this.currentPlayback = Playback.create(track, djUserId);
        this.isPlaybackActivated = true;
    }
}
```

**Data Entity** (Persistence)
```java
// Location: infrastructure/repository/PartyroomData.java
@Entity
@Table(name = "PARTYROOM")
public class PartyroomData {
    // Anemic - just data
    @Id private String id;
    @Column private String title;
    @Column private boolean isPlaybackActivated;
    // Only getters/setters
}
```

**Why This Separation?**
1. **Domain Purity**: Business logic independent of persistence
2. **Testing**: Easy to unit test domain logic
3. **Flexibility**: Can change persistence without affecting business rules
4. **Clarity**: Clear separation between "what" (domain) and "how" (persistence)

### Converters

Converters bridge Domain Models and Data Entities:

```java
@Component
public class PartyroomConverter {

    // Domain → Data (for persistence)
    public PartyroomData toData(Partyroom partyroom) {
        PartyroomData data = new PartyroomData();
        data.setId(partyroom.getId().getValue());
        data.setTitle(partyroom.getTitle());
        data.setIsPlaybackActivated(partyroom.isPlaybackActivated());
        return data;
    }

    // Data → Domain (after retrieval)
    public Partyroom toDomain(PartyroomData data) {
        return Partyroom.builder()
            .id(PartyroomId.from(data.getId()))
            .title(data.getTitle())
            .isPlaybackActivated(data.isPlaybackActivated())
            .build();
    }
}
```

### Value Objects

Value Objects provide type safety and encapsulation:

**Example: UserId**
```java
public class UserId {
    private final String value;

    private UserId(String value) {
        validateFormat(value);
        this.value = value;
    }

    public static UserId from(String value) {
        return new UserId(value);
    }

    private void validateFormat(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be blank");
        }
        // Additional validation
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

**Benefits**:
- Type safety: Can't mix up UserId and PartyroomId
- Validation in one place
- Immutable
- Value equality semantics

### Aggregates

**Aggregate Root**: Partyroom
- Owns: Crew, DJ, Playback
- All modifications go through Partyroom
- Transaction boundary

**Example**:
```java
// ✅ CORRECT: Modify through aggregate root
partyroom.addCrew(crew);
partyroom.removeCrew(crewId);

// ❌ WRONG: Don't modify child entities directly
crew.setGrade(GradeType.MODERATOR); // Bypass business rules!
```

## Event-Driven Architecture

### Event Flow

```
Application Service
      ↓
  [Business Logic]
      ↓
  [Persist Changes]
      ↓
  [Publish Event to Redis]
      ↓
Redis Pub/Sub
      ↓
  [All Server Instances]
      ↓
  [Message Listeners]
      ↓
  [WebSocket Broadcasting]
      ↓
  [Connected Clients]
```

### Event Publishing

**Publisher**:
```java
@Service
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishPlaybackStart(String partyroomId, PlaybackStartMessage message) {
        String topic = "partyroom:" + partyroomId + ":playback-start";
        redisTemplate.convertAndSend(topic, message);
    }
}
```

**Usage in Application Service**:
```java
@Service
@Transactional
public class PlaybackApplicationService {

    public void startPlayback(PartyroomId partyroomId, Track track) {
        // 1. Business logic
        Partyroom partyroom = partyroomRepository.findById(partyroomId);
        partyroom.startPlayback(track);

        // 2. Persist
        partyroomRepository.save(partyroom);

        // 3. Publish event (after successful transaction)
        PlaybackStartMessage message = createMessage(partyroom, track);
        redisMessagePublisher.publishPlaybackStart(partyroomId, message);
    }
}
```

### Event Subscription

**Listener**:
```java
@Component
public class PlaybackEventListener {

    @RedisMessageListener(topics = "partyroom:*:playback-start")
    public void handlePlaybackStart(PlaybackStartMessage message) {
        // Broadcast to WebSocket clients in this server instance
        messagingTemplate.convertAndSend(
            "/sub/events/" + message.getPartyroomId() + "/playback-start",
            message
        );

        // Schedule playback end task
        taskScheduler.schedulePlaybackEnd(message);
    }
}
```

### Event Types

| Event | Purpose | Subscribed By |
|-------|---------|---------------|
| chat-message | New chat message | All connected clients |
| partyroom-access | User join/leave | All connected clients |
| playback-start | Playback started | All clients, TaskScheduler |
| playback-skip | Track skipped | All clients, TaskScheduler |
| playback-reaction | Reaction added | All clients |
| playback-reaction-motion | Reaction animation | All clients |
| crew-grade | Grade changed | All clients |
| crew-penalty | Penalty applied | All clients |
| profile-update | Profile changed | All clients |
| notice-update | Notice updated | All clients |
| partyroom-deactivation | Room closed | All clients |

## Distributed Systems Patterns

### 1. Distributed Locking

**Why Needed**:
- Multiple server instances
- Concurrent requests to same resource
- Race conditions in critical sections

**Implementation**:
```java
@Service
public class DistributedLockExecutor {
    private final RedisLockService redisLockService;

    public <T> T execute(String lockKey, Supplier<T> operation) {
        String lock = null;
        try {
            // Acquire lock
            lock = redisLockService.lock(lockKey, Duration.ofSeconds(10));
            if (lock == null) {
                throw new ConcurrentAccessException("Could not acquire lock");
            }

            // Execute critical section
            return operation.get();

        } finally {
            // Release lock
            if (lock != null) {
                redisLockService.unlock(lockKey, lock);
            }
        }
    }
}
```

**Usage Example**:
```java
@Service
public class DjQueueApplicationService {

    public void enqueueDj(PartyroomId partyroomId, UserId userId, PlaylistId playlistId) {
        String lockKey = "partyroom:" + partyroomId.getValue() + ":dj-queue";

        distributedLockExecutor.execute(lockKey, () -> {
            // Critical section: modifying DJ queue
            Partyroom partyroom = partyroomRepository.findById(partyroomId);
            partyroom.enqueueDj(userId, playlistId);
            partyroomRepository.save(partyroom);
            return null;
        });
    }
}
```

**When to Use Locks**:
- ✅ DJ queue modifications
- ✅ Playback state changes
- ✅ Crew grade changes
- ✅ Counter increments (likes, grabs)
- ❌ Read operations
- ❌ User profile updates (single user scope)

### 2. Redis Pub/Sub for Cross-Instance Communication

**Problem**: WebSocket clients connected to different server instances

**Solution**: Redis Pub/Sub broadcasts events to all instances

```
Client A → Server 1 → Redis Pub/Sub → Server 1 → Client A ✅
                                    → Server 2 → Client B ✅
                                    → Server 3 → Client C ✅
```

### 3. Session Management

**WebSocket Sessions in Redis**:
```java
@Service
public class SessionCacheManager {
    private final RedisTemplate<String, SessionInfo> redisTemplate;

    public void saveSession(String sessionId, SessionInfo sessionInfo) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, sessionInfo, Duration.ofHours(24));
    }

    public SessionInfo getSession(String sessionId) {
        String key = "session:" + sessionId;
        return redisTemplate.opsForValue().get(key);
    }
}
```

**Why Redis?**:
- Sessions accessible from any server instance
- Automatic expiration (TTL)
- Fast in-memory access

### 4. Task Scheduling

**Dynamic Scheduling for Playback**:
```java
@Service
public class ExpirationTaskScheduler {
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void schedulePlaybackEnd(String partyroomId, Duration duration) {
        String taskKey = "playback:" + partyroomId;

        // Cancel existing task if any
        cancelTask(taskKey);

        // Schedule new task
        ScheduledFuture<?> future = taskScheduler.schedule(
            () -> handlePlaybackEnd(partyroomId),
            Instant.now().plus(duration)
        );

        scheduledTasks.put(taskKey, future);
    }

    private void handlePlaybackEnd(String partyroomId) {
        // Move to next DJ, start next track
        playbackService.handleTrackEnd(partyroomId);
    }
}
```

## Data Flow

### Read Flow (Query)

```
Client Request
    ↓
Controller (Presentation)
    ↓
QueryService (Application)
    ↓
Repository (Infrastructure)
    ↓
Database
    ↓
Data Entity
    ↓
Converter → Domain Model
    ↓
Converter → Response DTO
    ↓
Client Response
```

### Write Flow (Command)

```
Client Request
    ↓
Controller (Presentation)
    ↓
ApplicationService (Application)
    ↓
Domain Model (Business Logic)
    ↓
Converter → Data Entity
    ↓
Repository (Infrastructure)
    ↓
Database
    ↓
Event Publisher
    ↓
Redis Pub/Sub
    ↓
All Server Instances
    ↓
WebSocket Clients
```

## Scalability Considerations

### Horizontal Scaling

The application is designed for horizontal scaling:

1. **Stateless API**: No server-side session state
2. **Redis for Shared State**: Sessions, locks, cache
3. **Pub/Sub for Events**: Cross-instance communication
4. **Database Connection Pooling**: HikariCP
5. **Load Balancer Ready**: Any instance can handle any request

### Caching Strategy

```java
@Service
public class PartyroomQueryService {

    @Cacheable(value = "partyroom", key = "#partyroomId")
    public PartyroomInfoResponse getPartyroomInfo(String partyroomId) {
        // Cache miss: query database
        // Cache hit: return from Redis
    }

    @CacheEvict(value = "partyroom", key = "#partyroomId")
    public void evictCache(String partyroomId) {
        // Invalidate cache when data changes
    }
}
```

### Performance Patterns

1. **QueryDSL for Efficient Queries**: Avoid N+1 problems
2. **Fetch Joins**: Load related entities in one query
3. **Indexes**: On foreign keys and frequently queried columns
4. **Connection Pooling**: Reuse database connections
5. **Redis for Hot Data**: Frequently accessed data in Redis

---

**Related Documents**:
- [DOMAIN_MODELS.md](DOMAIN_MODELS.md) - Domain entities and relationships
- [REDIS_PATTERNS.md](REDIS_PATTERNS.md) - Redis usage patterns
- [COMMON_TASKS.md](COMMON_TASKS.md) - Implementation guides
