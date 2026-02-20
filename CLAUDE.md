# PFPlay Backend - Claude Code Context

> **Context documentation for Claude Code to understand and work effectively with this codebase**

This document serves as the primary entry point for Claude Code when working on the PFPlay backend. It provides essential context about the project's architecture, conventions, and patterns.

## Project Overview

**PFPlay** is a real-time music party platform where users create virtual party rooms, share music as DJs, and interact in real-time. This backend service powers:

- Real-time music party rooms with synchronized playback
- DJ queue system with automatic rotation
- Live chat and event broadcasting via WebSocket
- OAuth-based authentication (Google, Twitter)
- Comprehensive moderation tools

## Technology Stack

- **Java 17** with **Spring Boot 3.2.3**
- **MySQL 8.0.30** (primary database)
- **Redis** (caching, pub/sub, distributed locks)
- **WebSocket** (STOMP protocol)
- **JPA/Hibernate** with **QueryDSL**
- **JWT** authentication with **OAuth2**

## Core Architecture Principles

### 1. Hexagonal Architecture (Ports & Adapters)

Each domain module follows a hexagonal package structure:

```
adapter/in/web/         (REST Controllers)
adapter/in/listener/    (Redis Topic Listeners)
adapter/in/stomp/       (WebSocket STOMP Controllers)
    ↓
application/service/    (Use Case Orchestration)
application/port/out/   (Outbound Port Interfaces)
    ↓
domain/entity/data/     (JPA Data Entities with business logic)
domain/service/         (Domain Services)
domain/value/           (Value Objects)
    ↑
adapter/out/persistence/ (JPA Repositories, QueryDSL)
adapter/out/external/    (Cross-domain Port Adapters)
```

**Key Pattern: Unified Data Entities**
- `*Data.java` - JPA entities that also contain business logic (no separate domain models)
- No converters — entities are used directly across layers
- Value Objects (UserId, PartyroomId, etc.) for type safety
- Cross-domain dependencies resolved via Port/Adapter pattern

### 2. Event-Driven Architecture

- **Redis Pub/Sub** for cross-instance communication
- **15+ event types** for real-time updates
- Decoupled components via message passing
- Scalable distributed architecture

### 3. Distributed Systems Patterns

- **Redis-based distributed locks** for consistency
- **Stateless API design** for horizontal scaling
- **Session management** across instances via Redis
- **Dynamic task scheduling** for playback timing

## Domain Boundaries

The project is organized into clear domain modules:

| Module/Domain | Gradle Module | Purpose | Key Entities |
|--------|---------|---------|--------------|
| **common** | `common` | Shared Kernel + infra config | UserId, Duration, Config, exceptions |
| **realtime** | `realtime` | WebSocket infrastructure | WebSocketAuthPort, SessionCachePort, SimpMessageSender |
| **playlist** | `playlist` | Music playlists | PlaylistData, TrackData |
| **user** | `user` | User management + profiles + avatars | MemberData, GuestData, ProfileData, AvatarResource* |
| **party** | `app` | Party room core + chat | PartyroomData, CrewData, DjData, PlaybackData |
| **auth** | `app` | Authentication & OAuth | OAuth tokens, StateStorePort |
| **admin** | `app` | Administrative tools | Admin operations |
| **bootstrap** | `app` | Composition Root | Cross-module adapters |

## Package Structure Convention

Each domain module follows this hexagonal structure:

```
com.pfplaybackend.api.{domain}/
├── adapter/
│   ├── in/
│   │   ├── web/              # REST Controllers + payload/request, response/
│   │   ├── listener/         # Redis Topic Listeners + message/
│   │   └── stomp/            # WebSocket STOMP Controllers (optional)
│   └── out/
│       ├── persistence/      # JPA Repositories + custom/ + impl/ (QueryDSL)
│       └── external/         # Cross-domain Port Adapters
├── application/
│   ├── service/              # Application Services (use cases)
│   ├── port/out/             # Outbound Port Interfaces (cross-domain)
│   ├── dto/                  # Application DTOs
│   └── aspect/               # Cross-cutting Aspects
└── domain/
    ├── entity/data/          # JPA Data Entities (*Data.java, with business logic)
    ├── service/              # Domain Services (*DomainService.java)
    ├── enums/                # Domain Enums
    ├── value/                # Value Objects (*Id.java)
    └── exception/            # Domain Exceptions
```

### Gradle Multi-Module (5 modules)

```
pfplay-backend-java/          (root)
├── common/                    # Shared Kernel + infra config (51 files)
├── realtime/                  # WebSocket infrastructure (10 files, zero domain imports)
├── playlist/                  # Playlist domain (47 files, depends: common)
├── user/                      # User domain (91 files, depends: common)
└── app/                       # auth, party, admin, bootstrap (218 files, depends: all)
```

Dependency direction: `app → user → common → realtime`, `app → playlist → common`

Build from root: `./gradlew :app:compileJava`, `./gradlew :app:test`

## Naming Conventions

### Entities and Models
- **JPA Entities**: `*Data.java` (e.g., `MemberData`, `PartyroomData`) — unified model with business logic
- **Value Objects**: `*Id`, `*Info`, `*Summary` (e.g., `UserId`, `CrewInfo`)

### Services
- **Application Services**: `*ApplicationService` (e.g., `PartyroomApplicationService`)
- **Domain Services**: `*DomainService` (e.g., `PartyroomDomainService`)
- **Infrastructure Services**: `*Service` (e.g., `JwtService`, `RedisLockService`)

### Controllers
- **REST Controllers**: `*Controller` (e.g., `PartyroomController`)
- **WebSocket STOMP Controllers**: in `adapter/in/stomp/` (e.g., `PartyroomChatController`)

### Exceptions
- **HTTP Exceptions**: `BadRequestException`, `UnauthorizedException`, etc.
- **Domain Exceptions**: `PartyroomException`, `CrewException`, `DjException`, etc.
- **Base Exception**: `DomainException` for all domain-level errors

## Critical Business Concepts

### User Types
- **Member**: OAuth-authenticated users (Google, Twitter)
- **Guest**: Temporary anonymous users
- **Authority Tiers**: FM (Full Member), AM (Associate Member), GT (Guest)

### Party Room Lifecycle
1. Host creates party room
2. Users join as Crew members
3. Crew members join DJ queue with playlists
4. System automatically rotates DJs
5. Real-time playback synchronized across all users

### Crew Grade Hierarchy
- **HOST**: Room owner, full control
- **COMMUNITY_MANAGER**: Moderation powers
- **MODERATOR**: Limited moderation
- **CLUBBER**: Active participant
- **LISTENER**: Basic access

### DJ Queue System
- Users queue up with their playlists
- System tracks order via `orderNumber`
- Automatic progression when track ends
- Crew can be removed/skipped by managers

## Working with This Codebase

### When Adding New Features

1. **Identify the Domain**: Which domain boundary does this belong to?
2. **Start with Data Entity**: Define `*Data.java` in `domain/entity/data/` with business logic
3. **Add Application Service**: Implement use case orchestration in `application/service/`
4. **Expose via Controller**: Create REST endpoints in `adapter/in/web/`
5. **Consider Events**: Should this trigger real-time events via Redis Pub/Sub?
6. **Think Distributed**: Do we need distributed locks?

### When Modifying Existing Code

1. **Read the Data Entity**: Understand business rules in `*Data.java`
2. **Review Application Service**: Understand the use case flow
3. **Check Event Publishing**: Are events being published?
4. **Check Cross-Domain Ports**: Are there port interfaces in `application/port/out/`?
5. **Consider Impact**: Will this affect other instances (distributed)?

### When Debugging

1. **Check Logs**: P6Spy logs all SQL queries
2. **Redis Pub/Sub**: Are events being published/received?
3. **WebSocket Sessions**: Is the session still active?
4. **Distributed Locks**: Check for deadlocks
5. **JWT Tokens**: Verify token validity and claims

## Important Patterns to Follow

### 1. Distributed Lock Pattern
```java
// Always use distributed locks for critical sections
distributedLockExecutor.execute(
    lockKey,
    () -> {
        // Critical section
    }
);
```

### 2. Domain Event Publishing
```java
// Publish events through Redis for cross-instance communication
redisMessagePublisher.publishChatMessage(partyroomId, message);
```

### 3. Transaction Boundaries
```java
// Application services define transaction boundaries
@Transactional
public void someUseCase() {
    // All database operations in one transaction
}
```

### 4. Value Object Usage
```java
// Use value objects for type safety
UserId userId = UserId.from(userIdString);
PartyroomId partyroomId = PartyroomId.from(partyroomIdString);
```

### 5. Exception Handling
```java
// Use domain-specific exceptions
throw new PartyroomException(PartyroomExceptionType.NOT_FOUND);
throw new BadRequestException("Invalid request");
```

## Security Considerations

### Authentication Flow
1. OAuth URL generation with state stored in Redis
2. OAuth callback validates state from Redis
3. User created/updated in database
4. JWT tokens generated (access + refresh)
5. Tokens stored in HTTP-only cookies

### WebSocket Authentication
- JWT validated in handshake interceptor
- Principal extracted from token claims
- Session associated with authenticated user

### Authorization
- `@PreAuthorize` annotations on sensitive endpoints
- Role-based: `ROLE_MEMBER`, `ROLE_GUEST`
- Authority tier-based: FM, AM, GT
- Method-level security enforcement

## Common Pitfalls to Avoid

1. **Don't bypass Port/Adapter boundaries**: Cross-domain access must go through `application/port/out/` interfaces
2. **Don't skip distributed locks**: Critical sections must be protected
3. **Don't forget to publish events**: Real-time features depend on it
4. **Don't create transactions in controllers**: Use application services
5. **Don't hardcode configuration**: Use Spring profiles
6. **Don't bypass security**: Always validate permissions
7. **Don't ignore Redis failures**: Handle gracefully
8. **Don't block WebSocket threads**: Use async for long operations
9. **Don't import domain code in realtime module**: realtime depends only on port interfaces

## Testing Strategy

- **Unit Tests**: Test domain logic in isolation
- **Integration Tests**: Test with test containers (MySQL, Redis)
- **WebSocket Tests**: Use STOMP test clients
- **Security Tests**: Verify authentication and authorization

## Configuration Profiles

- **local**: Local development (localhost database, debug logs)
- **dev**: Development environment (dev database, verbose logs)
- **prod**: Production environment (prod database, minimal logs)

## Reference Documents

For deeper understanding, refer to these detailed documents:

1. **[ARCHITECTURE.md](docs/claude/ARCHITECTURE.md)** - Detailed architecture patterns
2. **[DOMAIN_MODELS.md](docs/claude/DOMAIN_MODELS.md)** - Domain entities and relationships
3. **[BUSINESS_FLOWS.md](docs/claude/BUSINESS_FLOWS.md)** - Core business flows
4. **[COMMON_TASKS.md](docs/claude/COMMON_TASKS.md)** - Step-by-step task guides
5. **[API_CONVENTIONS.md](docs/claude/API_CONVENTIONS.md)** - REST API standards
6. **[WEBSOCKET_EVENTS.md](docs/claude/WEBSOCKET_EVENTS.md)** - Real-time event system
7. **[REDIS_PATTERNS.md](docs/claude/REDIS_PATTERNS.md)** - Redis usage patterns
8. **[SECURITY.md](docs/claude/SECURITY.md)** - Security implementation details

## Quick Reference

### Key Files to Know
- `JwtService.java` - JWT token generation/validation
- `RedisLockService.java` - Distributed locking
- `RedisMessagePublisher.java` - Event publishing
- `GlobalExceptionHandler.java` - Exception handling
- `SecurityConfig.java` - Security configuration
- `WebSocketConfig.java` - WebSocket configuration

### Key Database Tables
- `MEMBER`, `GUEST` - User tables
- `PARTYROOM` - Party room data
- `CREW` - Party room members
- `DJ` - DJ queue entries
- `PLAYBACK` - Current/historical playback
- `PLAYLIST`, `TRACK` - Music data

### Key Redis Patterns
- `oauth:state:{state}` - OAuth state validation
- `session:{sessionId}` - WebSocket sessions
- Lock keys for distributed locking
- Pub/Sub topics for events

---

**Last Updated**: 2026-02-20

This document should be updated when:
- New domain modules are added
- Architecture patterns change
- Major refactoring occurs
- New critical concepts emerge
