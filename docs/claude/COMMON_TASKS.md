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

### Step 1: Create Package Structure

```
api/src/main/java/com/pfplaybackend/api/{domain}/
├── presentation/
│   ├── {Domain}Controller.java
│   └── dto/
│       ├── request/
│       └── response/
├── application/
│   └── service/
│       ├── {Domain}ApplicationService.java
│       └── converter/
│           └── {Domain}Converter.java
├── domain/
│   ├── domainmodel/
│   │   └── {Domain}.java
│   ├── service/
│   │   └── {Domain}DomainService.java
│   ├── enums/
│   └── valueobject/
│       └── {Domain}Id.java
└── infrastructure/
    └── repository/
        ├── {Domain}Data.java
        └── {Domain}Repository.java
```

### Step 2: Create Value Object

**File**: `domain/valueobject/{Domain}Id.java`

```java
package com.pfplaybackend.api.{domain}.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class {Domain}Id {
    private final String value;

    private {Domain}Id(String value) {
        validateFormat(value);
        this.value = value;
    }

    public static {Domain}Id from(String value) {
        return new {Domain}Id(value);
    }

    public static {Domain}Id generate() {
        return new {Domain}Id(UUID.randomUUID().toString());
    }

    private void validateFormat(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("{Domain}Id cannot be blank");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof {Domain}Id)) return false;
        {Domain}Id that = ({Domain}Id) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

### Step 3: Create Domain Model

**File**: `domain/domainmodel/{Domain}.java`

```java
package com.pfplaybackend.api.{domain}.domain.domainmodel;

import com.pfplaybackend.api.{domain}.domain.valueobject.{Domain}Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class {Domain} {
    private {Domain}Id id;
    private String name;
    // Other fields

    // Business logic methods
    public void someBusinessLogic() {
        validateState();
        // Implement business rules
    }

    private void validateState() {
        // Validation logic
    }
}
```

### Step 4: Create Data Entity

**File**: `infrastructure/repository/{Domain}Data.java`

```java
package com.pfplaybackend.api.{domain}.infrastructure.repository;

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
    @Column(name = "{domain}_id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // Other fields with JPA annotations
}
```

### Step 5: Create Repository

**File**: `infrastructure/repository/{Domain}Repository.java`

```java
package com.pfplaybackend.api.{domain}.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface {Domain}Repository extends JpaRepository<{Domain}Data, String> {

    Optional<{Domain}Data> findByName(String name);

    boolean existsByName(String name);
}
```

### Step 6: Create Converter

**File**: `application/service/converter/{Domain}Converter.java`

```java
package com.pfplaybackend.api.{domain}.application.service.converter;

import com.pfplaybackend.api.{domain}.domain.domainmodel.{Domain};
import com.pfplaybackend.api.{domain}.domain.valueobject.{Domain}Id;
import com.pfplaybackend.api.{domain}.infrastructure.repository.{Domain}Data;
import com.pfplaybackend.api.{domain}.presentation.dto.response.{Domain}Response;
import org.springframework.stereotype.Component;

@Component
public class {Domain}Converter {

    public {Domain} toDomain({Domain}Data data) {
        return {Domain}.builder()
            .id({Domain}Id.from(data.getId()))
            .name(data.getName())
            .build();
    }

    public {Domain}Data toData({Domain} domain) {
        {Domain}Data data = new {Domain}Data();
        data.setId(domain.getId().getValue());
        data.setName(domain.getName());
        return data;
    }

    public {Domain}Response toResponse({Domain}Data data) {
        return {Domain}Response.builder()
            .id(data.getId())
            .name(data.getName())
            .build();
    }
}
```

### Step 7: Create Application Service

**File**: `application/service/{Domain}ApplicationService.java`

```java
package com.pfplaybackend.api.{domain}.application.service;

import com.pfplaybackend.api.{domain}.application.service.converter.{Domain}Converter;
import com.pfplaybackend.api.{domain}.domain.domainmodel.{Domain};
import com.pfplaybackend.api.{domain}.domain.valueobject.{Domain}Id;
import com.pfplaybackend.api.{domain}.infrastructure.repository.{Domain}Data;
import com.pfplaybackend.api.{domain}.infrastructure.repository.{Domain}Repository;
import com.pfplaybackend.api.{domain}.presentation.dto.request.{Domain}CreateRequest;
import com.pfplaybackend.api.{domain}.presentation.dto.response.{Domain}Response;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class {Domain}ApplicationService {

    private final {Domain}Repository repository;
    private final {Domain}Converter converter;

    @Transactional
    public {Domain}Response create({Domain}CreateRequest request) {
        // 1. Create domain model
        {Domain} domain = {Domain}.builder()
            .id({Domain}Id.generate())
            .name(request.getName())
            .build();

        // 2. Business logic
        domain.someBusinessLogic();

        // 3. Save
        {Domain}Data saved = repository.save(converter.toData(domain));

        // 4. Return response
        return converter.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public {Domain}Response getById(String id) {
        {Domain}Data data = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("{Domain} not found"));

        return converter.toResponse(data);
    }
}
```

### Step 8: Create Controller

**File**: `presentation/{Domain}Controller.java`

```java
package com.pfplaybackend.api.{domain}.presentation;

import com.pfplaybackend.api.{domain}.application.service.{Domain}ApplicationService;
import com.pfplaybackend.api.{domain}.presentation.dto.request.{Domain}CreateRequest;
import com.pfplaybackend.api.{domain}.presentation.dto.response.{Domain}Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{domains}")
@RequiredArgsConstructor
public class {Domain}Controller {

    private final {Domain}ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<{Domain}Response> create(@RequestBody {Domain}CreateRequest request) {
        {Domain}Response response = applicationService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<{Domain}Response> getById(@PathVariable String id) {
        {Domain}Response response = applicationService.getById(id);
        return ResponseEntity.ok(response);
    }
}
```

## Adding a New API Endpoint

### Step 1: Define Request DTO

**File**: `presentation/dto/request/{Operation}Request.java`

```java
package com.pfplaybackend.api.{domain}.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class {Operation}Request {

    @NotBlank(message = "Field cannot be blank")
    private String field;

    // Other fields with validation annotations
}
```

### Step 2: Define Response DTO

**File**: `presentation/dto/response/{Operation}Response.java`

```java
package com.pfplaybackend.api.{domain}.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class {Operation}Response {
    private String id;
    private String field;
    // Other fields
}
```

### Step 3: Add Application Service Method

```java
@Service
@RequiredArgsConstructor
public class {Domain}ApplicationService {

    @Transactional
    public {Operation}Response {operation}({Operation}Request request) {
        // Implementation
    }
}
```

### Step 4: Add Controller Endpoint

```java
@RestController
@RequestMapping("/api/v1/{domains}")
@RequiredArgsConstructor
public class {Domain}Controller {

    @PostMapping("/{id}/{operation}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<{Operation}Response> {operation}(
            @PathVariable String id,
            @RequestBody {Operation}Request request) {

        {Operation}Response response = applicationService.{operation}(request);
        return ResponseEntity.ok(response);
    }
}
```

### Step 5: Test the Endpoint

```bash
curl -X POST http://localhost:8080/api/v1/{domains}/{id}/{operation} \
  -H "Content-Type: application/json" \
  -H "Cookie: access_token=YOUR_JWT_TOKEN" \
  -d '{"field": "value"}'
```

## Adding a New WebSocket Event

### Step 1: Define Event Message

**File**: `liveconnect/event/message/{Event}Message.java`

```java
package com.pfplaybackend.api.liveconnect.event.message;

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

**File**: `liveconnect/redis/RedisMessagePublisher.java`

```java
public void publish{Event}(String partyroomId, {Event}Message message) {
    String topic = "partyroom:" + partyroomId + ":{event}";
    redisTemplate.convertAndSend(topic, message);
}
```

### Step 3: Add Listener

**File**: `liveconnect/event/listener/{Event}Listener.java`

```java
package com.pfplaybackend.api.liveconnect.event.listener;

import com.pfplaybackend.api.liveconnect.event.message.{Event}Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class {Event}Listener {

    private final SimpMessagingTemplate messagingTemplate;

    @RedisMessageListener(topics = "partyroom:*:{event}")
    public void handle{Event}({Event}Message message) {
        // Broadcast to WebSocket clients
        messagingTemplate.convertAndSend(
            "/sub/events/" + message.getPartyroomId() + "/{event}",
            message
        );
    }
}
```

### Step 4: Publish Event in Application Service

```java
@Service
@Transactional
public class {Domain}ApplicationService {

    private final RedisMessagePublisher redisMessagePublisher;

    public void someOperation() {
        // Business logic

        // Publish event
        {Event}Message message = {Event}Message.builder()
            .partyroomId(partyroomId)
            .userId(userId)
            .timestamp(LocalDateTime.now())
            .build();

        redisMessagePublisher.publish{Event}(partyroomId, message);
    }
}
```

### Step 5: Subscribe from Client

```javascript
// Client-side JavaScript
stompClient.subscribe('/sub/events/' + partyroomId + '/{event}',
  (message) => {
    const event = JSON.parse(message.body);
    console.log('Event received:', event);
    // Handle event
  }
);
```

## Adding a New Redis Message

### Step 1: Define Topic Pattern

Choose a consistent naming pattern:
```
{resource}:{id}:{event-type}

Examples:
- partyroom:123:playback-start
- chatroom:456:message
- user:789:profile-update
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

### Step 3: Add Listener

```java
@Component
public class {Event}MessageListener {

    @RedisMessageListener(topics = "{resource}:*:{event}")
    public void on{Event}({Event}Message message) {
        // Handle message
    }
}
```

### Step 4: Configure Listener (if needed)

Check `RedisConfig.java` to ensure your listener pattern is registered.

## Adding a New Database Entity

### Step 1: Create Migration Script

**File**: `src/main/resources/db/migration/V{version}__{description}.sql`

```sql
CREATE TABLE {TABLE_NAME} (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Step 2: Create JPA Entity

```java
@Entity
@Table(name = "{TABLE_NAME}")
@Getter
@Setter
public class {Entity}Data extends BaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentData parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ChildData> children = new ArrayList<>();
}
```

### Step 3: Create Repository

```java
@Repository
public interface {Entity}Repository extends JpaRepository<{Entity}Data, String> {
    // Query methods
}
```

### Step 4: Create Domain Model & Converter

Follow steps in "Adding a New Domain Module".

### Step 5: Run Migration

```bash
./gradlew flywayMigrate
```

## Adding Distributed Lock

### When to Use

Use distributed locks when:
- Modifying shared resources across instances
- Critical sections that must not run concurrently
- Race conditions are possible

### Step 1: Identify Lock Key

Choose a unique lock key pattern:
```
{resource}:{id}:{operation}

Examples:
- partyroom:123:dj-queue
- partyroom:123:playback
- user:456:profile-update
```

### Step 2: Apply Lock in Service

```java
@Service
public class {Domain}ApplicationService {

    private final DistributedLockExecutor distributedLockExecutor;

    @Transactional
    public void criticalOperation(String resourceId) {
        String lockKey = "{resource}:" + resourceId + ":{operation}";

        distributedLockExecutor.execute(lockKey, () -> {
            // Critical section
            // This code will only run on one instance at a time

            // 1. Read current state
            Entity entity = repository.findById(resourceId)
                .orElseThrow();

            // 2. Modify state
            entity.modify();

            // 3. Save
            repository.save(entity);

            return null; // or return result
        });
    }
}
```

### Step 3: Set Lock Timeout (Optional)

```java
distributedLockExecutor.execute(
    lockKey,
    () -> { /* critical section */ },
    Duration.ofSeconds(30) // Custom timeout
);
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

### Step 2: Add Provider Type

**File**: `auth/domain/enums/ProviderType.java`

```java
public enum ProviderType {
    GOOGLE,
    TWITTER,
    {PROVIDER}  // Add new provider
}
```

### Step 3: Add OAuth Client Implementation

**File**: `auth/infrastructure/{Provider}OAuthClient.java`

```java
@Component
public class {Provider}OAuthClient implements OAuthClient {

    @Value("${oauth.{provider}.client-id}")
    private String clientId;

    @Value("${oauth.{provider}.client-secret}")
    private String clientSecret;

    // Implement OAuth flow methods
    @Override
    public String buildAuthorizationUrl(String state) {
        // Build auth URL
    }

    @Override
    public String getAccessToken(String code) {
        // Exchange code for token
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        // Fetch user info
    }
}
```

### Step 4: Register in OAuth Service

**File**: `auth/application/service/OAuthService.java`

```java
@Service
public class OAuthService {

    private final Map<ProviderType, OAuthClient> clients;

    public OAuthService(GoogleOAuthClient googleClient,
                       TwitterOAuthClient twitterClient,
                       {Provider}OAuthClient {provider}Client) {
        this.clients = Map.of(
            ProviderType.GOOGLE, googleClient,
            ProviderType.TWITTER, twitterClient,
            ProviderType.{PROVIDER}, {provider}Client
        );
    }
}
```

## Writing Tests

### Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class {Domain}ApplicationServiceTest {

    @Mock
    private {Domain}Repository repository;

    @Mock
    private {Domain}Converter converter;

    @InjectMocks
    private {Domain}ApplicationService service;

    @Test
    void create_ShouldCreateEntity() {
        // Given
        {Domain}CreateRequest request = new {Domain}CreateRequest("test");
        {Domain} domain = {Domain}.builder().id({Domain}Id.generate()).build();
        {Domain}Data data = new {Domain}Data();

        when(converter.toData(any())).thenReturn(data);
        when(repository.save(any())).thenReturn(data);

        // When
        service.create(request);

        // Then
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
    private {Domain}ApplicationService service;

    @Test
    void create_ShouldPersistToDatabase() {
        // Given
        {Domain}CreateRequest request = new {Domain}CreateRequest("test");

        // When
        {Domain}Response response = service.create(request);

        // Then
        assertThat(response.getId()).isNotNull();
    }
}
```

## Running the Application

### Local Development

```bash
# Start dependencies
docker-compose up -d

# Run application
./gradlew bootRun --args='--spring.profiles.active=local'

# Or with specific port
./gradlew bootRun --args='--spring.profiles.active=local --server.port=8081'
```

### Build

```bash
# Clean and build
./gradlew clean build

# Skip tests
./gradlew build -x test

# Build Docker image
docker build -t pfplay-backend:latest .
```

### Run with Docker

```bash
# Run application container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/pfplay \
  pfplay-backend:latest
```

### Access Swagger UI

```
http://localhost:8080/spec/api
```

### Check Health

```bash
curl http://localhost:8080/actuator/health
```

---

**Related Documents**:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns
- [DOMAIN_MODELS.md](DOMAIN_MODELS.md) - Domain entities
- [API_CONVENTIONS.md](API_CONVENTIONS.md) - API standards
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - Business processes
