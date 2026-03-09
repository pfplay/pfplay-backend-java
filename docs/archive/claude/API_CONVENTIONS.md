# API Conventions

This document describes the RESTful API design conventions and standards used in the PFPlay backend.

## Table of Contents

- [API Design Principles](#api-design-principles)
- [URL Structure](#url-structure)
- [HTTP Methods](#http-methods)
- [Request DTOs](#request-dtos)
- [Response DTOs](#response-dtos)
- [HTTP Status Codes](#http-status-codes)
- [Error Responses](#error-responses)
- [Pagination](#pagination)
- [Filtering and Sorting](#filtering-and-sorting)
- [Versioning](#versioning)
- [Authentication](#authentication)
- [Security Annotations](#security-annotations)

## API Design Principles

### RESTful Design

1. **Resource-Based**: URLs represent resources, not actions
2. **HTTP Methods**: Use appropriate HTTP methods (GET, POST, PUT, DELETE, PATCH)
3. **Stateless**: Each request contains all necessary information
4. **JSON Format**: Use JSON for request and response bodies
5. **Consistent Naming**: Follow consistent naming conventions

### Naming Conventions

1. **Use Lowercase**: All URLs in lowercase
2. **Use Hyphens**: Separate words with hyphens (kebab-case)
3. **Plural Nouns**: Use plural nouns for collections (`/users`, `/playlists`)
4. **Avoid Verbs**: Don't use verbs in URLs (except for actions that don't fit CRUD)

## URL Structure

### Base Structure

```
https://api.pfplay.com/api/v{version}/{resource}[/{id}][/{sub-resource}][/{id}][/{action}]
```

### Examples

**Good Examples**:
```
GET    /api/v1/partyrooms                    # List party rooms
GET    /api/v1/partyrooms/{id}               # Get party room
POST   /api/v1/partyrooms                    # Create party room
PUT    /api/v1/partyrooms/{id}               # Update party room
DELETE /api/v1/partyrooms/{id}               # Delete party room

POST   /api/v1/partyrooms/{id}/access        # Join party room (action)
POST   /api/v1/partyrooms/{id}/exit          # Leave party room (action)

GET    /api/v1/playlists/{id}/tracks         # Get tracks in playlist
POST   /api/v1/playlists/{id}/tracks         # Add track to playlist
```

**Bad Examples**:
```
❌ GET  /api/v1/getPartyrooms               # Don't use verbs
❌ POST /api/v1/partyroom/create            # Use POST /partyrooms
❌ GET  /api/v1/partyrooms/list             # Use GET /partyrooms
❌ GET  /api/v1/party_rooms                 # Use hyphens, not underscores
```

### Resource Hierarchy

When resources are hierarchically related:

```
/api/v1/partyrooms/{partyroomId}/crews/{crewId}
/api/v1/partyrooms/{partyroomId}/dj-queue
/api/v1/partyrooms/{partyroomId}/playback
```

### Action Endpoints

For actions that don't fit CRUD (use sparingly):

```
POST /api/v1/partyrooms/{id}/access         # Join room
POST /api/v1/partyrooms/{id}/exit           # Leave room
POST /api/v1/partyrooms/{id}/playback/start # Start playback
POST /api/v1/partyrooms/{id}/playback/skip  # Skip track
```

## HTTP Methods

### GET - Retrieve Resources

**Use for**: Reading data, no side effects

```java
@GetMapping("/{id}")
public ResponseEntity<PartyroomResponse> getPartyroom(@PathVariable String id) {
    // Implementation
}
```

**Characteristics**:
- Idempotent: Multiple identical requests have same effect as single request
- Cacheable
- Should not modify server state

### POST - Create Resources

**Use for**: Creating new resources, non-idempotent actions

```java
@PostMapping
public ResponseEntity<PartyroomResponse> createPartyroom(
        @RequestBody PartyroomCreateRequest request) {
    // Implementation
}
```

**Return**: `201 Created` with `Location` header pointing to new resource

### PUT - Update/Replace Resources

**Use for**: Full replacement of a resource

```java
@PutMapping("/{id}")
public ResponseEntity<PartyroomResponse> updatePartyroom(
        @PathVariable String id,
        @RequestBody PartyroomUpdateRequest request) {
    // Implementation
}
```

**Characteristics**:
- Idempotent: Multiple identical requests have same effect
- Replaces entire resource

### PATCH - Partial Update

**Use for**: Partial updates to a resource

```java
@PatchMapping("/{id}")
public ResponseEntity<PlaylistResponse> renamePlaylist(
        @PathVariable String id,
        @RequestBody PlaylistRenameRequest request) {
    // Implementation
}
```

**Characteristics**:
- May not be idempotent (depends on operation)
- Updates only specified fields

### DELETE - Remove Resources

**Use for**: Deleting resources

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePartyroom(@PathVariable String id) {
    // Implementation
}
```

**Return**: `204 No Content` or `200 OK` with response body

## Request DTOs

### Naming Convention

```
{Resource}{Action}Request

Examples:
- PartyroomCreateRequest
- PartyroomUpdateRequest
- CrewGradeChangeRequest
- PlaybackReactionRequest
```

### Structure

```java
package com.pfplaybackend.api.{domain}.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyroomCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @Size(max = 500, message = "Introduction must be less than 500 characters")
    private String introduction;

    @NotNull(message = "Stage type is required")
    private StageType stageType;

    @Pattern(regexp = "^[a-z0-9-]+$", message = "Link domain must contain only lowercase letters, numbers, and hyphens")
    @Size(min = 3, max = 50, message = "Link domain must be between 3 and 50 characters")
    private String linkDomain;

    @Min(value = 30, message = "Playback time limit must be at least 30 seconds")
    @Max(value = 3600, message = "Playback time limit must be at most 1 hour")
    private Integer playbackTimeLimit;
}
```

### Validation Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@NotNull` | Field cannot be null | `@NotNull String stageType` |
| `@NotBlank` | String cannot be null, empty, or whitespace | `@NotBlank String title` |
| `@NotEmpty` | Collection/array cannot be empty | `@NotEmpty List<String> ids` |
| `@Size` | String/collection size constraints | `@Size(min=3, max=50)` |
| `@Min` / `@Max` | Numeric value constraints | `@Min(1) Integer count` |
| `@Pattern` | Regex pattern matching | `@Pattern(regexp="^[a-z]+$")` |
| `@Email` | Valid email format | `@Email String email` |
| `@Past` / `@Future` | Date/time constraints | `@Past LocalDate birthDate` |

### Controller @Valid Requirement

All `@RequestBody` parameters **must** have `@Valid` to activate bean validation:

```java
@PostMapping
public ResponseEntity<ApiCommonResponse<CreatePartyroomResponse>> createPartyroom(
        @Valid @RequestBody CreatePartyroomRequest request) {
    // Without @Valid, validation annotations on the DTO are ignored
}
```

### Nested Validation

```java
@Getter
public class PartyroomWithCrewRequest {

    @Valid
    @NotNull
    private PartyroomCreateRequest partyroom;

    @Valid
    @NotEmpty
    private List<CrewAddRequest> crews;
}
```

## Response DTOs

### Naming Convention

```
{Resource}Response        # Single resource
{Resource}SummaryResponse # Brief info
{Resource}InfoResponse    # Detailed info
{Resource}ListResponse    # Collection wrapper

Examples:
- PartyroomResponse
- PartyroomSummaryResponse
- CrewInfoResponse
- PlaylistListResponse
```

### Structure

```java
package com.pfplaybackend.api.{domain}.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyroomInfoResponse {

    private String id;
    private String hostId;
    private String title;
    private String introduction;
    private StageType stageType;
    private String linkDomain;
    private Integer playbackTimeLimit;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Nested objects
    private CrewInfoResponse host;
    private List<CrewSummaryResponse> crews;
}
```

### JSON Field Naming

Use **camelCase** for JSON fields:

```json
{
  "id": "123",
  "hostId": "456",
  "isPlaybackActivated": true,
  "createdAt": "2024-01-01T12:00:00"
}
```

### Date/Time Format

**ISO 8601 format**: `yyyy-MM-dd'T'HH:mm:ss`

```java
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime timestamp;
```

**Output**:
```json
{
  "timestamp": "2024-01-01T12:30:45"
}
```

### Null Handling

**Include null fields** in responses (don't use `@JsonInclude(Include.NON_NULL)`):

```json
{
  "id": "123",
  "introduction": null,  // Explicitly show null
  "notice": null
}
```

This makes it clear that fields exist but have no value.

## HTTP Status Codes

### Success Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| **200 OK** | Success | GET, PUT, PATCH requests |
| **201 Created** | Resource created | POST request created new resource |
| **204 No Content** | Success, no body | DELETE request, PUT with no response |

### Client Error Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| **400 Bad Request** | Invalid input | Validation errors, malformed request |
| **401 Unauthorized** | Not authenticated | Missing or invalid JWT token |
| **403 Forbidden** | No permission | Authenticated but insufficient permissions |
| **404 Not Found** | Resource not found | Resource doesn't exist |
| **409 Conflict** | Resource conflict | Duplicate entry, state conflict |

### Server Error Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| **500 Internal Server Error** | Server error | Unexpected server-side error |
| **503 Service Unavailable** | Service down | Database unavailable, maintenance |

### Usage in Controllers

```java
@PostMapping
public ResponseEntity<PartyroomResponse> createPartyroom(
        @Valid @RequestBody PartyroomCreateRequest request) {

    PartyroomResponse response = service.createPartyroom(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, "/api/v1/partyrooms/" + response.getId())
        .body(response);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePartyroom(@PathVariable String id) {
    service.deletePartyroom(id);
    return ResponseEntity.noContent().build();
}

@GetMapping("/{id}")
public ResponseEntity<PartyroomResponse> getPartyroom(@PathVariable String id) {
    PartyroomResponse response = service.getPartyroom(id);
    return ResponseEntity.ok(response);
}
```

## Error Responses

### Standard Error Format

```java
@Getter
@Builder
public class ErrorResponse {
    private String error;           // Error type/code
    private String message;         // Human-readable message
    private int status;             // HTTP status code
    private String path;            // Request path
    private LocalDateTime timestamp;
    private List<FieldError> fieldErrors; // Validation errors (optional)
}
```

### Example Error Responses

**400 Bad Request - Validation Error**:
```json
{
  "error": "BAD_REQUEST",
  "message": "Validation failed",
  "status": 400,
  "path": "/api/v1/partyrooms",
  "timestamp": "2024-01-01T12:00:00",
  "fieldErrors": [
    {
      "field": "title",
      "message": "Title is required"
    },
    {
      "field": "linkDomain",
      "message": "Link domain must contain only lowercase letters"
    }
  ]
}
```

**404 Not Found**:
```json
{
  "error": "NOT_FOUND",
  "message": "Partyroom not found",
  "status": 404,
  "path": "/api/v1/partyrooms/123",
  "timestamp": "2024-01-01T12:00:00"
}
```

**409 Conflict**:
```json
{
  "error": "CONFLICT",
  "message": "Link domain already exists",
  "status": 409,
  "path": "/api/v1/partyrooms",
  "timestamp": "2024-01-01T12:00:00"
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
            .error("BAD_REQUEST")
            .message("Validation failed")
            .status(400)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .status(404)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
```

## Pagination

### Request Parameters

```java
@GetMapping
public ResponseEntity<PageResponse<PartyroomSummaryResponse>> listPartyrooms(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir) {
    // Implementation
}
```

### Response Format

```java
@Getter
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
```

**Example Response**:
```json
{
  "content": [
    { "id": "1", "title": "Party 1" },
    { "id": "2", "title": "Party 2" }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 50,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### Using Spring Data Pagination

```java
@Service
public class PartyroomQueryService {

    public PageResponse<PartyroomSummaryResponse> listPartyrooms(
            int page, int size, String sortBy, String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PartyroomData> pagedResult = repository.findAll(pageable);

        return PageResponse.<PartyroomSummaryResponse>builder()
            .content(pagedResult.getContent().stream()
                .map(converter::toSummaryResponse)
                .collect(Collectors.toList()))
            .page(pagedResult.getNumber())
            .size(pagedResult.getSize())
            .totalElements(pagedResult.getTotalElements())
            .totalPages(pagedResult.getTotalPages())
            .first(pagedResult.isFirst())
            .last(pagedResult.isLast())
            .build();
    }
}
```

## Filtering and Sorting

### Query Parameters

```
GET /api/v1/partyrooms?stageType=MAIN&isTerminated=false&sortBy=createdAt&sortDir=DESC
```

### Implementation

```java
@GetMapping
public ResponseEntity<List<PartyroomSummaryResponse>> listPartyrooms(
        @RequestParam(required = false) StageType stageType,
        @RequestParam(required = false) Boolean isTerminated,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {

    List<PartyroomSummaryResponse> rooms = service.listPartyrooms(
        stageType, isTerminated, sortBy, sortDir);

    return ResponseEntity.ok(rooms);
}
```

### Using QueryDSL for Complex Filters

```java
public List<PartyroomData> findByFilters(
        StageType stageType, Boolean isTerminated) {

    QPartyroomData partyroom = QPartyroomData.partyroomData;

    BooleanBuilder builder = new BooleanBuilder();

    if (stageType != null) {
        builder.and(partyroom.stageType.eq(stageType));
    }

    if (isTerminated != null) {
        builder.and(partyroom.isTerminated.eq(isTerminated));
    }

    return queryFactory
        .selectFrom(partyroom)
        .where(builder)
        .fetch();
}
```

## Versioning

### URL Path Versioning

**Current approach**: Version in URL path

```
/api/v1/partyrooms
/api/v2/partyrooms
```

### Version Changes

When making breaking changes:
1. Increment version number
2. Maintain old version for compatibility
3. Deprecate old version with timeline
4. Document migration path

## Authentication

### JWT in Cookies

**Access Token Cookie**:
```
Cookie: access_token={JWT_TOKEN}
```

**Cookie Attributes**:
- `HttpOnly`: Prevents JavaScript access
- `Secure`: HTTPS only (production)
- `SameSite=Strict`: CSRF protection
- `Path=/api`: Scoped to API endpoints

### Example Request

```bash
curl -X GET http://localhost:8080/api/v1/partyrooms/123 \
  -H "Cookie: access_token=eyJhbGciOiJIUzI1NiIs..."
```

### Automatic Cookie Handling

Spring Security automatically:
1. Extracts JWT from cookie
2. Validates signature and expiration
3. Loads user authorities
4. Sets authentication in SecurityContext

## Security Annotations

### @PreAuthorize

**Require Member role**:
```java
@PostMapping
@PreAuthorize("hasRole('MEMBER')")
public ResponseEntity<PlaylistResponse> createPlaylist(
        @RequestBody PlaylistCreateRequest request) {
    // Only members can create playlists
}
```

**Require specific authority tier**:
```java
@PreAuthorize("hasAuthority('FM')")
public ResponseEntity<Void> adminOperation() {
    // Only full members
}
```

**Complex expressions**:
```java
@PreAuthorize("hasRole('MEMBER') and #userId == authentication.principal.userId.value")
public ResponseEntity<Void> updateOwnProfile(
        @PathVariable String userId,
        @RequestBody ProfileUpdateRequest request) {
    // Member can only update their own profile
}
```

### Common Patterns

| Use Case | Annotation |
|----------|------------|
| Public endpoint | None (configure in SecurityConfig) |
| Any authenticated user | `@PreAuthorize("isAuthenticated()")` |
| Members only | `@PreAuthorize("hasRole('MEMBER')")` |
| Specific authority | `@PreAuthorize("hasAuthority('FM')")` |
| Multiple roles | `@PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")` |
| Owner only | `@PreAuthorize("#userId == authentication.principal.userId.value")` |

---

**Related Documents**:
- [SECURITY.md](SECURITY.md) - Authentication details
- [COMMON_TASKS.md](COMMON_TASKS.md) - Adding new endpoints
- [ARCHITECTURE.md](ARCHITECTURE.md) - Layered architecture
