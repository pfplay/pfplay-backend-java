# Common Tasks

This document provides step-by-step guides for common development tasks in the PFPlay backend.

## Table of Contents

- [Adding a New Domain Module](#adding-a-new-domain-module)
- [Adding a New API Endpoint](#adding-a-new-api-endpoint)
- [Adding a New WebSocket Event](#adding-a-new-websocket-event)
- [Adding a New Redis Message](#adding-a-new-redis-message)
- [Adding a New Database Entity](#adding-a-new-database-entity)
- [Adding Distributed Lock](#adding-distributed-lock)
- [Adding a New OAuth Provider](#adding-a-new-oauth-provider)
- [Writing Tests](#writing-tests)
- [Running the Application](#running-the-application)

## Adding a New Domain Module

When adding a new domain to the system (e.g., `notification`, `payment`, etc.):

### Step 1: Create Hexagonal Package Structure

```
api/src/main/java/com/pfplaybackend/api/{domain}/
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── {Domain}Controller.java
│   │       └── payload/
│   │           ├── request/
│   │           └── response/
│   └── out/
│       └── persistence/
│           ├── {Domain}Repository.java
│           ├── custom/
│           │   └── {Domain}RepositoryCustom.java
│           └── impl/
│               └── {Domain}RepositoryImpl.java
├── application/
│   ├── service/
│   │   └── {Domain}Service.java
│   ├── port/
│   │   └── out/                    # Only if cross-domain deps needed
│   │       └── {Other}QueryPort.java
│   └── dto/
│       └── {Domain}Dto.java
└── domain/
    ├── entity/
    │   └── data/
    │       └── {Domain}Data.java   # JPA entity with business logic
    ├── service/
    │   └── {Domain}DomainService.java
    ├── enums/
    ├── value/
    │   └── {Domain}Id.java
    └── exception/
        └── {Domain}Exception.java
```

### Step 2: Create Value Object

**File**: `domain/value/{Domain}Id.java`

```java
package com.pfplaybackend.api.{domain}.domain.value;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class {Domain}Id {
    private UUID id;

    public {Domain}Id() {
        this.id = UUID.randomUUID();
    }

    // equals, hashCode, getter
}
```

### Step 3: Create Data Entity (with business logic)

**File**: `domain/entity/data/{Domain}Data.java`

```java
package com.pfplaybackend.api.{domain}.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "{DOMAIN}")
@Getter
@Setter
public class {Domain}Data extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // Business logic methods directly on the entity
    public void someBusinessLogic() {
        validateState();
        // Implement business rules
    }

    private void validateState() {
        // Validation logic
    }
}
```

> **Note**: No separate domain model or converter needed. The `*Data.java` entity serves as both the JPA entity and domain model.

### Step 4: Create Repository

**File**: `adapter/out/persistence/{Domain}Repository.java`

```java
package com.pfplaybackend.api.{domain}.adapter.out.persistence;

import com.pfplaybackend.api.{domain}.domain.entity.data.{Domain}Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface {Domain}Repository extends JpaRepository<{Domain}Data, Long> {
    // Query methods
}
```

### Step 5: Create Application Service

**File**: `application/service/{Domain}Service.java`

```java
package com.pfplaybackend.api.{domain}.application.service;

import com.pfplaybackend.api.{domain}.adapter.out.persistence.{Domain}Repository;
import com.pfplaybackend.api.{domain}.domain.entity.data.{Domain}Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class {Domain}Service {

    private final {Domain}Repository repository;

    @Transactional
    public {Domain}Data create(String name) {
        {Domain}Data entity = new {Domain}Data();
        entity.setName(name);
        entity.someBusinessLogic();
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public {Domain}Data getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("{Domain} not found"));
    }
}
```

### Step 6: Create Controller

**File**: `adapter/in/web/{Domain}Controller.java`

```java
package com.pfplaybackend.api.{domain}.adapter.in.web;

import com.pfplaybackend.api.{domain}.application.service.{Domain}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{domains}")
@RequiredArgsConstructor
public class {Domain}Controller {

    private final {Domain}Service service;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> create(@RequestBody CreateRequest request) {
        var result = service.create(request.getName());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var result = service.getById(id);
        return ResponseEntity.ok(result);
    }
}
```

## Adding a New API Endpoint

### Step 1: Define Request DTO

**File**: `adapter/in/web/payload/request/{Operation}Request.java`

```java
package com.pfplaybackend.api.{domain}.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class {Operation}Request {

    @NotBlank(message = "Field cannot be blank")
    private String field;
}
```

### Step 2: Define Response DTO

**File**: `adapter/in/web/payload/response/{Operation}Response.java`

```java
package com.pfplaybackend.api.{domain}.adapter.in.web.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class {Operation}Response {
    private Long id;
    private String field;
}
```

### Step 3: Add Application Service Method

```java
@Service
@RequiredArgsConstructor
public class {Domain}Service {

    @Transactional
    public {Operation}Response {operation}({Operation}Request request) {
        // Implementation
    }
}
```

### Step 4: Add Controller Endpoint

```java
@PostMapping("/{id}/{operation}")
@PreAuthorize("hasRole('MEMBER')")
public ResponseEntity<{Operation}Response> {operation}(
        @PathVariable Long id,
        @RequestBody {Operation}Request request) {

    {Operation}Response response = service.{operation}(request);
    return ResponseEntity.ok(response);
}
```

## Adding a New WebSocket Event

### Step 1: Define Event Message

**File**: `party/adapter/in/listener/message/{Event}Message.java`

```java
package com.pfplaybackend.api.party.adapter.in.listener.message;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class {Event}Message {
    private String partyroomId;
    private String userId;
    private String data;
    private LocalDateTime timestamp;
}
```

### Step 2: Add Publisher Method

**File**: `common/config/redis/RedisMessagePublisher.java`

```java
public void publish{Event}(String partyroomId, {Event}Message message) {
    String topic = "partyroom:" + partyroomId + ":{event}";
    redisTemplate.convertAndSend(topic, message);
}
```

### Step 3: Add Topic Listener

**File**: `party/adapter/in/listener/{Event}TopicListener.java`

```java
package com.pfplaybackend.api.party.adapter.in.listener;

import com.pfplaybackend.realtime.sender.SimpMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class {Event}TopicListener implements MessageListener {

    private final SimpMessageSender simpMessageSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        {Event}Message parsed = deserialize(message);
        simpMessageSender.sendToTopic(
            "/sub/events/" + parsed.getPartyroomId() + "/{event}",
            parsed
        );
    }
}
```

> **Note**: The `SimpMessageSender` is in the `realtime` module (`com.pfplaybackend.realtime.sender.SimpMessageSender`).

### Step 4: Register Listener in RedisConfig

Check `common/config/redis/RedisConfig.java` to ensure your listener and topic pattern are registered.

### Step 5: Subscribe from Client

```javascript
stompClient.subscribe('/sub/events/' + partyroomId + '/{event}',
  (message) => {
    const event = JSON.parse(message.body);
    // Handle event
  }
);
```

## Adding a New Redis Message

### Step 1: Define Topic Pattern

```
{resource}:{id}:{event-type}

Examples:
- partyroom:123:playback-start
- chatroom:456:message
```

### Step 2: Add Publisher Method

```java
@Service
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish{Event}(String resourceId, {Event}Message message) {
        String topic = "{resource}:" + resourceId + ":{event}";
        redisTemplate.convertAndSend(topic, message);
    }
}
```

### Step 3: Add Topic Listener

```java
@Component
@RequiredArgsConstructor
public class {Event}TopicListener implements MessageListener {

    private final SimpMessageSender simpMessageSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Deserialize and broadcast via WebSocket
    }
}
```

### Step 4: Register in RedisConfig

Ensure your listener and topic pattern are registered in `common/config/redis/RedisConfig.java`.

## Adding a New Database Entity

### Step 1: Create JPA Entity

**File**: `domain/entity/data/{Entity}Data.java`

```java
@Entity
@Table(name = "{TABLE_NAME}")
@Getter
@Setter
public class {Entity}Data extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentData parent;

    // Business logic
    public void validate() {
        // ...
    }
}
```

### Step 2: Create Repository

**File**: `adapter/out/persistence/{Entity}Repository.java`

```java
@Repository
public interface {Entity}Repository extends JpaRepository<{Entity}Data, Long> {
    // Query methods
}
```

### Step 3: Add QueryDSL (if needed)

**Custom interface**: `adapter/out/persistence/custom/{Entity}RepositoryCustom.java`
**Implementation**: `adapter/out/persistence/impl/{Entity}RepositoryImpl.java`

```java
@RequiredArgsConstructor
public class {Entity}RepositoryImpl implements {Entity}RepositoryCustom {

    private final JPAQueryFactory queryFactory;  // Bean injected, NOT new JPAQueryFactory(em)

    @Override
    public List<{Entity}Data> findByCustomCriteria(...) {
        Q{Entity}Data q = Q{Entity}Data.{entity}Data;
        return queryFactory
            .selectFrom(q)
            .leftJoin(q.parent).fetchJoin()  // Use fetchJoin to avoid N+1
            .where(q.name.eq(name))
            .fetch();
    }
}
```

## Adding Distributed Lock

### When to Use

Use distributed locks when:
- Modifying shared resources across instances
- Critical sections that must not run concurrently
- Race conditions are possible

### Step 1: Identify Lock Key

```
{resource}:{id}:{operation}

Examples:
- partyroom:123:dj-queue
- partyroom:123:playback
```

### Step 2: Apply Lock in Service

```java
@Service
public class {Domain}Service {

    private final DistributedLockExecutor distributedLockExecutor;

    @Transactional
    public void criticalOperation(Long resourceId) {
        String lockKey = "{resource}:" + resourceId + ":{operation}";

        distributedLockExecutor.execute(lockKey, () -> {
            // Critical section — only one instance at a time
            var entity = repository.findById(resourceId).orElseThrow();
            entity.modify();
            repository.save(entity);
            return null;
        });
    }
}
```

### Best Practices

1. **Keep critical sections short**: Don't hold locks for long operations
2. **Use specific keys**: Avoid locking too broadly
3. **Handle timeouts**: Consider what happens if lock acquisition fails
4. **Avoid nested locks**: Can cause deadlocks

## Adding a New OAuth Provider

### Step 1: Add Configuration

**File**: `application.yml`

```yaml
oauth:
  {provider}:
    client-id: ${OAUTH_{PROVIDER}_CLIENT_ID}
    client-secret: ${OAUTH_{PROVIDER}_CLIENT_SECRET}
    authorization-uri: https://provider.com/oauth/authorize
    token-uri: https://provider.com/oauth/token
    user-info-uri: https://provider.com/api/user
    scopes: email,profile
```

### Step 2: Add Provider Enum

**File**: `auth/domain/enums/OAuthProvider.java`

```java
public enum OAuthProvider {
    GOOGLE,
    TWITTER,
    {PROVIDER}  // Add new provider
}
```

### Step 3: Add OAuth Client Implementation

**File**: `auth/adapter/out/external/{Provider}OAuthClient.java`

```java
@Component
public class {Provider}OAuthClient {

    @Value("${oauth.{provider}.client-id}")
    private String clientId;

    // Implement OAuth flow methods: buildAuthorizationUrl, exchangeCodeForToken, getUserInfo
}
```

### Step 4: Register in OAuthClientService

**File**: `auth/application/service/OAuthClientService.java`

Register the new client alongside existing Google and Twitter clients.

## Writing Tests

### Conventions

- Korean `@DisplayName` for test names
- given/when/then structure
- AssertJ assertions
- `lenient().when()` when needed for Mockito strict mode

### Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class {Domain}ServiceTest {

    @Mock
    private {Domain}Repository repository;

    @InjectMocks
    private {Domain}Service service;

    @Test
    @DisplayName("도메인 생성 시 정상적으로 저장된다")
    void create_ShouldSaveEntity() {
        // given
        {Domain}Data data = new {Domain}Data();
        data.setName("test");
        when(repository.save(any())).thenReturn(data);

        // when
        var result = service.create("test");

        // then
        assertThat(result.getName()).isEqualTo("test");
        verify(repository).save(any());
    }
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class {Domain}IntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.30");

    @Autowired
    private {Domain}Service service;

    @Test
    @DisplayName("도메인 생성 시 데이터베이스에 저장된다")
    void create_ShouldPersistToDatabase() {
        // given & when
        var result = service.create("test");

        // then
        assertThat(result.getId()).isNotNull();
    }
}
```

## Running the Application

### Build Commands (Multi-Module)

```bash
# Set Java home
export JAVA_HOME="C:/Users/Eisen/.jdks/corretto-17.0.11"

# Compile (from project root)
./gradlew :api:compileJava
./gradlew :realtime:compileJava

# Run all tests
./gradlew :api:test

# Run specific domain tests
./gradlew :api:test --tests "com.pfplaybackend.api.party.*"
./gradlew :api:test --tests "com.pfplaybackend.api.playlist.*"

# Clean build
./gradlew clean :api:test

# Build without tests
./gradlew :api:build -x test

# Boot run
./gradlew :api:bootRun --args='--spring.profiles.active=local'
```

### Local Development

```bash
# Start dependencies
docker-compose up -d

# Run application
./gradlew :api:bootRun --args='--spring.profiles.active=local'
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/pfplay \
  pfplay-backend:latest
```

### Access Swagger UI

```
http://localhost:8080/spec/api
```

---

**Last Updated**: 2026-02-20

**Related Documents**:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns
- [DOMAIN_MODELS.md](DOMAIN_MODELS.md) - Domain entities
- [API_CONVENTIONS.md](API_CONVENTIONS.md) - API standards
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - Business processes
