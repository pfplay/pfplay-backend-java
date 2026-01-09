# Redis Patterns

This document describes Redis usage patterns, key structures, and best practices in the PFPlay backend.

## Table of Contents

- [Redis Overview](#redis-overview)
- [Key Naming Conventions](#key-naming-conventions)
- [Distributed Locking](#distributed-locking)
- [Pub/Sub Messaging](#pubsub-messaging)
- [Caching Patterns](#caching-patterns)
- [Session Management](#session-management)
- [OAuth State Management](#oauth-state-management)
- [Key Expiration](#key-expiration)
- [Configuration](#configuration)
- [Best Practices](#best-practices)

## Redis Overview

### Use Cases in PFPlay

1. **Distributed Locks**: Ensure consistency across instances
2. **Pub/Sub Messaging**: Cross-instance event broadcasting
3. **Session Caching**: WebSocket session management
4. **OAuth State Storage**: Temporary OAuth state validation
5. **Application Caching**: Frequently accessed data
6. **Rate Limiting**: API rate limiting (future)

### Redis Data Structures Used

| Structure | Use Case | Example |
|-----------|----------|---------|
| **String** | Simple values, locks | Lock tokens, OAuth state |
| **Hash** | Complex objects | Session data, user cache |
| **Set** | Unique collections | Active user IDs |
| **Sorted Set** | Ordered collections | Leaderboards (future) |
| **Pub/Sub** | Messaging | Event broadcasting |

## Key Naming Conventions

### General Pattern

```
{namespace}:{entity}:{id}[:{sub-entity}]

Examples:
- oauth:state:abc123xyz
- session:ws:session-id-123
- lock:partyroom:room-123:dj-queue
- cache:partyroom:room-123
- cache:user:user-456
```

### Namespace Guidelines

| Namespace | Purpose | Example Keys |
|-----------|---------|--------------|
| `oauth` | OAuth-related data | `oauth:state:{state}` |
| `session` | User sessions | `session:ws:{sessionId}` |
| `lock` | Distributed locks | `lock:{resource}:{id}:{operation}` |
| `cache` | Application cache | `cache:{entity}:{id}` |
| `rate` | Rate limiting | `rate:{endpoint}:{userId}` |
| `temp` | Temporary data | `temp:{purpose}:{id}` |

### Naming Best Practices

1. **Use colons** as separators (Redis convention)
2. **Start with namespace** for easy pattern matching
3. **Be specific** to avoid key collisions
4. **Use lowercase** for consistency
5. **Include ID** for resource-specific keys

## Distributed Locking

### Why Distributed Locks?

In a multi-instance deployment:
- Multiple servers handling concurrent requests
- Race conditions on shared resources
- Need for mutual exclusion across instances

### Lock Implementation

**RedisLockService**:
```java
@Service
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Acquire a distributed lock
     * @param lockKey Unique lock identifier
     * @param duration Lock expiration time
     * @return Lock token (use to unlock), or null if failed
     */
    public String lock(String lockKey, Duration duration) {
        String lockToken = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockToken, duration);

        return Boolean.TRUE.equals(acquired) ? lockToken : null;
    }

    /**
     * Release a distributed lock
     * @param lockKey Lock identifier
     * @param lockToken Token returned from lock()
     * @return true if unlocked successfully
     */
    public boolean unlock(String lockKey, String lockToken) {
        // Lua script ensures atomic compare-and-delete
        String script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """;

        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey),
            lockToken
        );

        return result != null && result == 1;
    }
}
```

### Lock Executor Pattern

**DistributedLockExecutor**:
```java
@Service
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final RedisLockService redisLockService;

    /**
     * Execute operation with distributed lock
     */
    public <T> T execute(String lockKey, Supplier<T> operation) {
        return execute(lockKey, operation, Duration.ofSeconds(10));
    }

    public <T> T execute(String lockKey, Supplier<T> operation, Duration timeout) {
        String lockToken = null;

        try {
            // Try to acquire lock
            lockToken = redisLockService.lock(lockKey, timeout);

            if (lockToken == null) {
                throw new ConcurrentAccessException(
                    "Could not acquire lock: " + lockKey
                );
            }

            // Execute critical section
            return operation.get();

        } finally {
            // Always release lock
            if (lockToken != null) {
                redisLockService.unlock(lockKey, lockToken);
            }
        }
    }

    /**
     * Execute void operation with distributed lock
     */
    public void executeVoid(String lockKey, Runnable operation) {
        execute(lockKey, () -> {
            operation.run();
            return null;
        });
    }
}
```

### Usage Examples

**DJ Queue Modification**:
```java
@Service
public class DjQueueApplicationService {

    public void enqueueDj(PartyroomId partyroomId, UserId userId, PlaylistId playlistId) {
        String lockKey = "lock:partyroom:" + partyroomId.getValue() + ":dj-queue";

        distributedLockExecutor.executeVoid(lockKey, () -> {
            // Critical section: modify DJ queue
            List<Dj> queue = djRepository.findByPartyroom(partyroomId);

            // Calculate next order number
            Integer nextOrder = queue.stream()
                .mapToInt(Dj::getOrderNumber)
                .max()
                .orElse(0) + 1;

            // Add DJ
            Dj dj = Dj.builder()
                .partyroomId(partyroomId)
                .userId(userId)
                .playlistId(playlistId)
                .orderNumber(nextOrder)
                .build();

            djRepository.save(dj);
        });
    }
}
```

**Reaction Counter Update**:
```java
public void addReaction(PlaybackId playbackId, UserId userId, ReactionType type) {
    String lockKey = "lock:playback:" + playbackId.getValue() + ":reaction";

    distributedLockExecutor.executeVoid(lockKey, () -> {
        Playback playback = playbackRepository.findById(playbackId)
            .orElseThrow();

        // Check existing reaction
        Optional<Reaction> existing = reactionRepository
            .findByPlaybackAndUser(playbackId, userId);

        if (existing.isPresent()) {
            // Decrement old count
            decrementCount(playback, existing.get().getType());
            // Update reaction
            existing.get().setType(type);
            reactionRepository.save(existing.get());
        } else {
            // Create new reaction
            reactionRepository.save(new Reaction(playbackId, userId, type));
        }

        // Increment new count
        incrementCount(playback, type);
        playbackRepository.save(playback);
    });
}
```

### Lock Key Examples

| Operation | Lock Key |
|-----------|----------|
| DJ queue modification | `lock:partyroom:{id}:dj-queue` |
| Playback state change | `lock:partyroom:{id}:playback` |
| Reaction update | `lock:playback:{id}:reaction` |
| Crew grade change | `lock:crew:{id}:grade` |
| Playlist modification | `lock:playlist:{id}:tracks` |

## Pub/Sub Messaging

### Topic Naming Convention

```
{resource}:{id}:{event-type}

Examples:
- partyroom:room-123:playback-start
- partyroom:room-123:playback-skip
- chatroom:room-456:message
- user:user-789:profile-update
```

### Publisher Implementation

**RedisMessagePublisher**:
```java
@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishPlaybackStart(String partyroomId, PlaybackStartMessage message) {
        String topic = "partyroom:" + partyroomId + ":playback-start";
        redisTemplate.convertAndSend(topic, message);
    }

    public void publishChatMessage(String chatroomId, GroupChatMessage message) {
        String topic = "chatroom:" + chatroomId + ":message";
        redisTemplate.convertAndSend(topic, message);
    }

    public void publishCrewGradeChange(String partyroomId, CrewGradeChangeMessage message) {
        String topic = "partyroom:" + partyroomId + ":crew-grade";
        redisTemplate.convertAndSend(topic, message);
    }

    // Other publishers...
}
```

### Subscriber Implementation

**Pattern-based Subscription**:
```java
@Component
@RequiredArgsConstructor
public class PlaybackEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ExpirationTaskScheduler taskScheduler;

    @RedisMessageListener(topics = "partyroom:*:playback-start")
    public void handlePlaybackStart(PlaybackStartMessage message) {
        String partyroomId = message.getPartyroomId();

        // Broadcast to WebSocket clients
        messagingTemplate.convertAndSend(
            "/sub/events/" + partyroomId + "/playback-start",
            message
        );

        // Schedule playback end
        taskScheduler.schedulePlaybackEnd(
            partyroomId,
            Duration.ofSeconds(message.getDuration())
        );
    }

    @RedisMessageListener(topics = "partyroom:*:playback-skip")
    public void handlePlaybackSkip(PlaybackSkipMessage message) {
        String partyroomId = message.getPartyroomId();

        // Cancel scheduled task
        taskScheduler.cancelPlaybackEnd(partyroomId);

        // Broadcast to clients
        messagingTemplate.convertAndSend(
            "/sub/events/" + partyroomId + "/playback-skip",
            message
        );
    }
}
```

### Listener Registration

**RedisConfig**:
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ApplicationContext context) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Find all methods annotated with @RedisMessageListener
        Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);

        for (Object bean : beans.values()) {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(RedisMessageListener.class)) {
                    RedisMessageListener annotation =
                        method.getAnnotation(RedisMessageListener.class);

                    MessageListener listener = createMessageListener(bean, method);

                    for (String topicPattern : annotation.topics()) {
                        container.addMessageListener(
                            listener,
                            new PatternTopic(topicPattern)
                        );
                    }
                }
            }
        }

        return container;
    }
}
```

### Message Flow

```
Server 1                    Redis Pub/Sub                Server 2
   │                              │                           │
   │──1. Publish message─────────>│                           │
   │   (topic: partyroom:123:*)   │                           │
   │                              │                           │
   │                              │──2. Broadcast to all─────>│
   │                              │    subscribers            │
   │                              │                           │
   │<─3. Receive message──────────│                           │
   │   (own instance)             │                           │
   │                              │                           │
   │                              │<──4. Receive message──────│
   │                              │    (other instance)       │
   │                              │                           │
   │──5. Broadcast to WebSocket──>│                           │
   │   clients on this server     │                           │
   │                              │                           │
   │                              │──6. Broadcast to WebSocket────>│
   │                              │    clients on other server│
```

## Caching Patterns

### Spring Cache Abstraction

**Configuration**:
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

### Cache Usage

**Cacheable**:
```java
@Service
public class PartyroomQueryService {

    @Cacheable(value = "partyroom", key = "#partyroomId")
    public PartyroomInfoResponse getPartyroomInfo(String partyroomId) {
        // This will be cached
        // Key: cache:partyroom::{partyroomId}
        PartyroomData data = repository.findById(partyroomId)
            .orElseThrow(() -> new NotFoundException("Partyroom not found"));

        return converter.toResponse(data);
    }
}
```

**Cache Eviction**:
```java
@Service
public class PartyroomApplicationService {

    @CacheEvict(value = "partyroom", key = "#partyroomId")
    public void updatePartyroom(String partyroomId, PartyroomUpdateRequest request) {
        // Update logic...
        // Cache will be evicted after method returns
    }

    @CacheEvict(value = "partyroom", allEntries = true)
    public void clearAllPartyroomCache() {
        // Clears all entries in "partyroom" cache
    }
}
```

**Cache Put**:
```java
@CachePut(value = "partyroom", key = "#result.id")
public PartyroomInfoResponse createPartyroom(PartyroomCreateRequest request) {
    // Create partyroom
    PartyroomInfoResponse response = // ...

    // Result will be cached
    return response;
}
```

### Manual Cache Operations

```java
@Service
@RequiredArgsConstructor
public class ManualCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheUser(String userId, UserInfo userInfo) {
        String key = "cache:user:" + userId;
        redisTemplate.opsForValue().set(key, userInfo, Duration.ofHours(1));
    }

    public Optional<UserInfo> getCachedUser(String userId) {
        String key = "cache:user:" + userId;
        UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(userInfo);
    }

    public void evictUser(String userId) {
        String key = "cache:user:" + userId;
        redisTemplate.delete(key);
    }
}
```

### Cache Strategies

| Strategy | Use Case | TTL |
|----------|----------|-----|
| **Short-lived** | Frequently changing data | 5-15 minutes |
| **Medium-lived** | Moderately stable data | 1 hour |
| **Long-lived** | Rarely changing data | 24 hours |
| **No expiration** | Static data | Infinite (manual eviction) |

**Examples**:
- User profile: 1 hour
- Partyroom info: 15 minutes
- Avatar resources: 24 hours
- OAuth state: 10 minutes

## Session Management

### WebSocket Session Caching

**SessionCacheManager**:
```java
@Service
@RequiredArgsConstructor
public class SessionCacheManager {

    private final RedisTemplate<String, SessionInfo> redisTemplate;

    public void saveSession(String sessionId, SessionInfo sessionInfo) {
        String key = "session:ws:" + sessionId;
        redisTemplate.opsForValue().set(key, sessionInfo, Duration.ofHours(24));
    }

    public Optional<SessionInfo> getSession(String sessionId) {
        String key = "session:ws:" + sessionId;
        SessionInfo info = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(info);
    }

    public void removeSession(String sessionId) {
        String key = "session:ws:" + sessionId;
        redisTemplate.delete(key);
    }

    public void updateSessionActivity(String sessionId) {
        String key = "session:ws:" + sessionId;
        redisTemplate.expire(key, Duration.ofHours(24));
    }
}
```

**SessionInfo Structure**:
```java
@Data
@Builder
public class SessionInfo implements Serializable {
    private String sessionId;
    private String userId;
    private String partyroomId;
    private LocalDateTime connectedAt;
    private LocalDateTime lastActivityAt;
}
```

### Session Lifecycle

1. **Connection**: Create session entry
2. **Activity**: Update last activity timestamp
3. **Disconnect**: Remove session entry
4. **Expiration**: Auto-remove after 24h inactivity

## OAuth State Management

### State Storage

**RedisStateStore**:
```java
@Service
@RequiredArgsConstructor
public class RedisStateStore {

    private final RedisTemplate<String, OAuthState> redisTemplate;

    public void saveState(String state, ProviderType provider) {
        String key = "oauth:state:" + state;

        OAuthState oauthState = OAuthState.builder()
            .state(state)
            .provider(provider)
            .createdAt(LocalDateTime.now())
            .build();

        // Expire after 10 minutes
        redisTemplate.opsForValue().set(key, oauthState, Duration.ofMinutes(10));
    }

    public Optional<OAuthState> getState(String state) {
        String key = "oauth:state:" + state;
        OAuthState oauthState = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(oauthState);
    }

    public boolean validateAndRemove(String state) {
        String key = "oauth:state:" + state;
        OAuthState oauthState = redisTemplate.opsForValue().get(key);

        if (oauthState != null) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }
}
```

### OAuth Flow with Redis

```
1. Generate state → Store in Redis (10 min TTL)
2. Redirect to OAuth provider
3. User authorizes
4. OAuth callback → Validate state from Redis
5. If valid, delete state and proceed
6. If invalid or expired, reject
```

## Key Expiration

### TTL Patterns

```java
// Set with expiration
redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));

// Set expiration on existing key
redisTemplate.expire(key, Duration.ofHours(1));

// Get TTL
Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

// Remove expiration
redisTemplate.persist(key);
```

### Expiration Listeners

**Listen for Key Expiration**:
```java
@Component
public class RedisKeyExpirationListener {

    @RedisKeyExpirationListener(pattern = "session:ws:*")
    public void handleSessionExpiration(String key) {
        String sessionId = extractSessionId(key);
        // Clean up resources
        cleanupSession(sessionId);
    }

    @RedisKeyExpirationListener(pattern = "oauth:state:*")
    public void handleStateExpiration(String key) {
        // Log expired OAuth state
        log.info("OAuth state expired: {}", key);
    }
}
```

### Scheduled Cleanup

**Periodic Cleanup Task**:
```java
@Component
public class RedisCleanupScheduler {

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cleanupExpiredSessions() {
        // Find and clean up orphaned sessions
        Set<String> keys = redisTemplate.keys("session:ws:*");

        for (String key : keys) {
            SessionInfo session = redisTemplate.opsForValue().get(key);

            if (session != null && isExpired(session)) {
                redisTemplate.delete(key);
            }
        }
    }
}
```

## Configuration

### Application Properties

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 2000ms

      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

      # Pub/Sub configuration
      listener:
        channel-pattern: "*"
```

### RedisTemplate Configuration

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializer
        Jackson2JsonRedisSerializer<Object> serializer =
            new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

## Best Practices

### 1. Key Naming

✅ **Good**:
```
lock:partyroom:123:dj-queue
cache:user:456
session:ws:abc-def-ghi
oauth:state:xyz123
```

❌ **Bad**:
```
Lock_Partyroom_123_DJQueue  // Use colons, not underscores or mixed case
UserCache456                // Missing namespace
session_abc_def_ghi         // Unclear structure
state-xyz123                // Missing namespace
```

### 2. TTL Management

✅ **Always set TTL** for temporary data:
```java
redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
```

❌ **Don't forget TTL**:
```java
redisTemplate.opsForValue().set(key, value); // Memory leak!
```

### 3. Lock Safety

✅ **Always use try-finally**:
```java
String lockToken = null;
try {
    lockToken = redisLockService.lock(lockKey, Duration.ofSeconds(10));
    // Critical section
} finally {
    if (lockToken != null) {
        redisLockService.unlock(lockKey, lockToken);
    }
}
```

❌ **Don't forget to unlock**:
```java
String lockToken = redisLockService.lock(lockKey, Duration.ofSeconds(10));
// Critical section
// Forgot to unlock! Deadlock risk!
```

### 4. Serialization

✅ **Use proper serialization**:
- Keys: `StringRedisSerializer`
- Values: `Jackson2JsonRedisSerializer` or `GenericJackson2JsonRedisSerializer`

✅ **Make cached objects Serializable**:
```java
@Data
public class CachedObject implements Serializable {
    private static final long serialVersionUID = 1L;
    // Fields...
}
```

### 5. Error Handling

✅ **Handle Redis failures gracefully**:
```java
try {
    Optional<User> cached = getCachedUser(userId);
    if (cached.isPresent()) {
        return cached.get();
    }
} catch (RedisConnectionException e) {
    log.warn("Redis unavailable, falling back to database", e);
}

// Fall back to database
return userRepository.findById(userId);
```

### 6. Avoid N+1 Queries

✅ **Use pipelines or multi-get**:
```java
List<String> keys = userIds.stream()
    .map(id -> "cache:user:" + id)
    .collect(Collectors.toList());

List<UserInfo> users = redisTemplate.opsForValue().multiGet(keys);
```

❌ **Don't query in a loop**:
```java
for (String userId : userIds) {
    UserInfo user = redisTemplate.opsForValue().get("cache:user:" + userId);
    // N queries!
}
```

---

**Related Documents**:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Distributed systems patterns
- [WEBSOCKET_EVENTS.md](WEBSOCKET_EVENTS.md) - Pub/Sub usage
- [COMMON_TASKS.md](COMMON_TASKS.md) - Adding Redis patterns
