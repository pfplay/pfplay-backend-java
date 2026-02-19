# Architecture Deep Dive

This document provides detailed explanation of the architectural patterns, design decisions, and implementation details of the PFPlay backend.

## Table of Contents

- [Hexagonal Architecture](#hexagonal-architecture)
- [Gradle Multi-Module Structure](#gradle-multi-module-structure)
- [Domain-Driven Design](#domain-driven-design)
- [Event-Driven Architecture](#event-driven-architecture)
- [Distributed Systems Patterns](#distributed-systems-patterns)
- [Data Flow](#data-flow)
- [Scalability Considerations](#scalability-considerations)

## Hexagonal Architecture

### Overview

The system follows a **hexagonal (ports & adapters) architecture** within each domain module:

```
┌───────────────────────────────────────────────────────────────┐
│                      Inbound Adapters                         │
│  adapter/in/web/      (REST Controllers)                      │
│  adapter/in/listener/ (Redis Topic Listeners)                 │
│  adapter/in/stomp/    (WebSocket STOMP Controllers)           │
│                                                               │
│  Dependencies: ↓ Application Layer                            │
└───────────────────────────────────────────────────────────────┘
                          ↓
┌───────────────────────────────────────────────────────────────┐
│                    Application Layer                           │
│  application/service/  (Use Case Orchestration)               │
│  application/port/out/ (Outbound Port Interfaces)             │
│  application/dto/      (Application DTOs)                     │
│  application/aspect/   (Cross-cutting Aspects)                │
│                                                               │
│  Dependencies: ↓ Domain Layer, → Outbound Ports               │
└───────────────────────────────────────────────────────────────┘
                          ↓
┌───────────────────────────────────────────────────────────────┐
│                      Domain Layer                              │
│  domain/entity/data/   (JPA Data Entities with business logic)│
│  domain/service/       (Domain Services)                      │
│  domain/enums/         (Domain Enums)                         │
│  domain/value/         (Value Objects)                         │
│  domain/exception/     (Domain Exceptions)                    │
│                                                               │
│  Dependencies: None (Pure business logic + JPA annotations)   │
└───────────────────────────────────────────────────────────────┘
                          ↑
┌───────────────────────────────────────────────────────────────┐
│                    Outbound Adapters                           │
│  adapter/out/persistence/  (JPA Repositories, QueryDSL)       │
│  adapter/out/external/     (Cross-domain Adapters)            │
│                                                               │
│  Dependencies: → Domain Layer, implements Outbound Ports      │
└───────────────────────────────────────────────────────────────┘
```

### Package Structure per Domain

Each domain module follows this consistent hexagonal structure:

```
com.pfplaybackend.api.{domain}/
├── adapter/
│   ├── in/
│   │   ├── web/                    # REST Controllers
│   │   │   ├── *Controller.java
│   │   │   └── payload/
│   │   │       ├── request/        # Request DTOs
│   │   │       └── response/       # Response DTOs
│   │   ├── listener/               # Redis Topic Listeners
│   │   │   ├── *TopicListener.java
│   │   │   └── message/            # Inbound/Outbound message DTOs
│   │   └── stomp/                  # WebSocket STOMP Controllers (optional)
│   └── out/
│       ├── persistence/            # JPA Repositories
│       │   ├── *Repository.java    # Spring Data interfaces
│       │   ├── custom/             # Custom query interfaces
│       │   │   └── *RepositoryCustom.java
│       │   └── impl/               # QueryDSL implementations
│       │       └── *RepositoryImpl.java
│       └── external/               # Cross-domain port adapters
│           └── *Adapter.java
├── application/
│   ├── service/                    # Application Services
│   │   └── *Service.java
│   ├── port/
│   │   └── out/                    # Outbound Port Interfaces
│   │       └── *Port.java
│   ├── dto/                        # Application DTOs
│   └── aspect/                     # Cross-cutting Aspects
└── domain/
    ├── entity/
    │   └── data/                   # JPA Data Entities (*Data.java)
    │       └── history/            # History entities (optional)
    ├── service/                    # Domain Services
    │   └── *DomainService.java
    ├── enums/                      # Domain Enums
    ├── value/                      # Value Objects
    │   └── *Id.java
    └── exception/                  # Domain Exceptions
```

### Layer Responsibilities

#### 1. Inbound Adapters (`adapter/in/`)

**Responsibilities**:
- HTTP request/response handling (REST controllers)
- Redis message consumption (topic listeners)
- WebSocket STOMP message handling
- Input validation and DTO transformation
- Security enforcement (@PreAuthorize)

**Key Patterns**:
- Controllers are thin, delegating to application services
- Request/Response DTOs live under `payload/` in each adapter
- Redis listeners receive messages and broadcast via `SimpMessageSender`

**Example**:
```java
adapter/in/web/
├── PartyroomAccessController.java
├── PartyroomInfoController.java
├── PartyroomManagementController.java
└── payload/
    ├── request/
    │   └── CreatePartyroomRequest.java
    └── response/
        └── QueryPartyroomListResponse.java

adapter/in/listener/
├── CrewProfilePreCheckTopicListener.java
├── PlaybackDurationWaitTopicListener.java
└── message/
    ├── PlaybackStartMessage.java
    └── ReactionAggregationMessage.java
```

#### 2. Application Layer (`application/`)

**Responsibilities**:
- Use case orchestration
- Transaction management (@Transactional)
- Domain service coordination
- Event publishing
- Cross-domain coordination via outbound ports

**Key Patterns**:
- Application services define transaction boundaries
- Outbound port interfaces in `application/port/out/` for cross-domain dependencies
- Adapters in `adapter/out/external/` implement these ports

**Example**:
```java
application/
├── service/
│   ├── PartyroomAccessService.java
│   ├── PartyroomManagementService.java
│   └── lock/
│       └── DistributedLockExecutor.java
├── port/
│   └── out/
│       ├── ProfileQueryPort.java
│       └── PlaylistQueryPort.java
├── dto/
│   ├── crew/CrewDto.java
│   └── playback/PlaybackDto.java
└── aspect/
    └── PartyroomContextAspect.java
```

#### 3. Domain Layer (`domain/`)

**Responsibilities**:
- Business logic and rules (within Data entities)
- Domain services for cross-entity operations
- Value objects for type safety
- Domain exceptions

**Key Pattern: Data Entities with Business Logic**

After Phase 1 refactoring, domain models and JPA entities were merged. `*Data.java` entities contain both JPA persistence annotations and business logic methods. There are no separate `domainmodel/` classes or converters.

```java
// domain/entity/data/CrewData.java — JPA entity with business logic
@Entity
@Table(name = "CREW")
@Getter @Setter
public class CrewData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId userId;

    @Enumerated(EnumType.STRING)
    private GradeType gradeType;

    private boolean isActive;
    private boolean isBanned;

    // Business logic directly in the entity
    public boolean canManagePlayback() {
        return gradeType.isHigherThanOrEqual(GradeType.MODERATOR);
    }
}
```

#### 4. Outbound Adapters (`adapter/out/`)

**Responsibilities**:
- Data persistence (JPA repositories with QueryDSL)
- Cross-domain port implementation (adapter/out/external/)

**Key Patterns**:
- Spring Data repositories in `adapter/out/persistence/`
- Custom QueryDSL queries in `impl/` subpackage
- `JPAQueryFactory` injected as Bean (not `new JPAQueryFactory(em)`)
- Cross-domain adapters implement port interfaces from other domains

**Example**:
```java
adapter/out/persistence/
├── PartyroomRepository.java          # Spring Data JPA interface
├── custom/
│   └── PartyroomRepositoryCustom.java # Custom query interface
└── impl/
    └── PartyroomRepositoryImpl.java   # QueryDSL implementation

adapter/out/external/
└── ProfileQueryAdapter.java           # Implements ProfileQueryPort
```

### Cross-Domain Communication: Port/Adapter Pattern

When one domain needs data from another, it uses an outbound port interface (defined in the consuming domain) with an adapter implementation:

```
party domain                              profile domain
─────────────────                         ─────────────────
application/port/out/                     adapter/out/persistence/
  ProfileQueryPort.java ◄── implements ── ProfileQueryAdapter.java
                                          (in party/adapter/out/external/)
```

This pattern ensures domains have no compile-time dependencies on each other's internals.

## Gradle Multi-Module Structure

### Module Layout

```
pfplay-backend-java/                    (Gradle root project)
├── settings.gradle                      # rootProject.name = 'pfplay'
│                                        # include 'api', 'realtime'
├── build.gradle                         # Shared: Java 17, Lombok, Spring DM BOM
├── gradlew, gradlew.bat, gradle/
├── api/
│   ├── build.gradle                     # implementation project(':realtime')
│   └── src/                             # All domain code
└── realtime/
    ├── build.gradle                     # websocket + spring-security-web only
    └── src/                             # WebSocket infrastructure
```

### Module Dependencies

```
api ──depends on──► realtime
```

**One-directional**: `api` → `realtime`. No reverse dependency. The `realtime` module has zero domain imports.

### realtime Module (10 files)

The `realtime` module contains WebSocket infrastructure, decoupled from domain logic via port interfaces:

```
com.pfplaybackend.realtime/
├── config/WebSocketConfig.java          # STOMP configuration
├── controller/HeartbeatController.java  # Heartbeat STOMP handler
├── sender/SimpMessageSender.java        # WebSocket message sender
├── interceptor/
│   └── WebSocketHandshakeInterceptor.java  # Auth via WebSocketAuthPort
├── port/
│   ├── WebSocketAuthPort.java           # Auth port (implemented by api module)
│   └── SessionCachePort.java            # Session cache port (implemented by api)
└── event/
    ├── ConnectionEventListener.java
    ├── DisconnectionEventListener.java
    ├── SubscriptionEventListener.java
    └── UnsubscriptionEventListener.java
```

### Port Implementations in api Module

| Port Interface (realtime) | Implementation (api) |
|---|---|
| `WebSocketAuthPort` | `common/adapter/realtime/JwtWebSocketAuthAdapter.java` |
| `SessionCachePort` | `party/application/service/cache/PartyroomSessionCacheManager.java` |

### Build Commands

```bash
JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11"

# Compile
./gradlew :api:compileJava
./gradlew :realtime:compileJava

# Test
./gradlew :api:test

# Clean build
./gradlew clean :api:test
```

## Domain-Driven Design

### Data Entities as Domain Models

After the Phase 1 refactoring, the codebase uses a **unified entity model**: `*Data.java` files serve as both JPA entities and domain models. There are no separate `domainmodel/` classes or `Converter` classes.

**Benefits**:
- No object mapping overhead
- JPA relationship navigation works naturally
- Business rules enforced directly on entities
- Simpler codebase with fewer files

**Pattern**:
```java
@Entity
@Table(name = "PARTYROOM")
@Getter @Setter
public class PartyroomData extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private UserId hostId;

    private String title;
    private boolean isPlaybackActivated;
    private boolean isTerminated;

    // Business logic methods on the entity itself
    public void terminate() {
        if (isTerminated) {
            throw new PartyroomException(PartyroomExceptionType.ALREADY_TERMINATED);
        }
        this.isTerminated = true;
    }
}
```

### Value Objects

Value Objects provide type safety and encapsulation:

```java
// UUID-based value object
@Embeddable
public class UserId {
    private UUID id;

    public UserId() {
        this.id = UUID.randomUUID();
    }

    // equals, hashCode based on value
}
```

**Key Value Objects**: `UserId`, `PartyroomId`, `CrewId`, `DjId`, `PlaybackId`, `PlaylistId`

### Aggregates

After Phase 2 refactoring, Crew and DJ were separated from the Partyroom aggregate:

| Aggregate | Root Entity | Owned Entities |
|-----------|-------------|----------------|
| **Partyroom** | `PartyroomData` | (standalone — no child collections) |
| **Crew** | `CrewData` | `CrewGradeHistoryData`, `CrewPenaltyHistoryData`, `CrewBlockHistoryData` |
| **DJ** | `DjData` | (standalone) |
| **Playback** | `PlaybackData` | `PlaybackReactionHistoryData` |
| **Playlist** | `PlaylistData` | `TrackData` |

Crew/DJ reference Partyroom via `@ManyToOne` FK but are accessed through their own repositories.

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
  [Topic Listeners in adapter/in/listener/]
      ↓
  [SimpMessageSender (realtime module)]
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

### Event Subscription

**Topic Listener** (in `adapter/in/listener/`):
```java
@Component
@RequiredArgsConstructor
public class PlaybackStartTopicListener implements MessageListener {

    private final SimpMessageSender simpMessageSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        PlaybackStartMessage parsed = deserialize(message);
        simpMessageSender.sendToTopic(
            "/sub/events/" + parsed.getPartyroomId() + "/playback-start",
            parsed
        );
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

**Implementation**:
```java
@Service
public class DistributedLockExecutor {
    private final RedisLockService redisLockService;

    public <T> T execute(String lockKey, Supplier<T> operation) {
        String lock = null;
        try {
            lock = redisLockService.lock(lockKey, Duration.ofSeconds(10));
            if (lock == null) {
                throw new ConcurrentAccessException("Could not acquire lock");
            }
            return operation.get();
        } finally {
            if (lock != null) {
                redisLockService.unlock(lockKey, lock);
            }
        }
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

```
Client A → Server 1 → Redis Pub/Sub → Server 1 → Client A ✅
                                     → Server 2 → Client B ✅
                                     → Server 3 → Client C ✅
```

### 3. Session Management

WebSocket session management uses the **Port/Adapter pattern** across modules:

- `realtime` module defines `SessionCachePort` interface
- `api` module implements it via `PartyroomSessionCacheManager`
- Sessions stored in Redis with 24h TTL

### 4. Task Scheduling

```java
@Service
public class ExpirationTaskScheduler {
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void schedulePlaybackEnd(String partyroomId, Duration duration) {
        String taskKey = "playback:" + partyroomId;
        cancelTask(taskKey);
        ScheduledFuture<?> future = taskScheduler.schedule(
            () -> handlePlaybackEnd(partyroomId),
            Instant.now().plus(duration)
        );
        scheduledTasks.put(taskKey, future);
    }
}
```

## Data Flow

### Read Flow (Query)

```
Client Request
    ↓
Controller (adapter/in/web/)
    ↓
Application Service (application/service/)
    ↓
Repository (adapter/out/persistence/)
    ↓
Database → Data Entity (*Data.java)
    ↓
Application DTO (application/dto/)
    ↓
Response DTO (adapter/in/web/payload/response/)
    ↓
Client Response
```

### Write Flow (Command)

```
Client Request
    ↓
Controller (adapter/in/web/)
    ↓
Application Service (application/service/)
    ↓
Data Entity business logic (domain/entity/data/)
    ↓
Repository (adapter/out/persistence/)
    ↓
Database
    ↓
Redis Event Publisher
    ↓
Topic Listeners (adapter/in/listener/) → SimpMessageSender (realtime)
    ↓
WebSocket Clients
```

## Scalability Considerations

### Horizontal Scaling

1. **Stateless API**: No server-side session state
2. **Redis for Shared State**: Sessions, locks, cache
3. **Pub/Sub for Events**: Cross-instance communication
4. **Database Connection Pooling**: HikariCP
5. **Load Balancer Ready**: Any instance can handle any request

### Performance Patterns

1. **QueryDSL with fetchJoin**: Prevent N+1 problems (Phase 5 applied)
2. **Single-table queries**: Avoid unnecessary cross-joins
3. **Indexes**: On foreign keys and frequently queried columns
4. **Connection Pooling**: Reuse database connections
5. **Redis for Hot Data**: Frequently accessed data in Redis

---

**Last Updated**: 2026-02-20

**Related Documents**:
- [DOMAIN_MODELS.md](DOMAIN_MODELS.md) - Domain entities and relationships
- [REDIS_PATTERNS.md](REDIS_PATTERNS.md) - Redis usage patterns
- [COMMON_TASKS.md](COMMON_TASKS.md) - Implementation guides
