# Security

This document describes the security implementation, authentication, authorization, and security best practices in PFPlay.

## Table of Contents

- [Security Architecture](#security-architecture)
- [OAuth2 Integration](#oauth2-integration)
- [JWT Authentication](#jwt-authentication)
- [Spring Security Configuration](#spring-security-configuration)
- [Authorization](#authorization)
- [WebSocket Security](#websocket-security)
- [CORS Configuration](#cors-configuration)
- [Security Best Practices](#security-best-practices)

## Security Architecture

### Overview

```
┌─────────────────────────────────────────────────────┐
│                   Client                            │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ 1. OAuth Login
                   ▼
┌─────────────────────────────────────────────────────┐
│              OAuth Provider                         │
│            (Google, Twitter)                        │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ 2. Authorization Code
                   ▼
┌─────────────────────────────────────────────────────┐
│              Backend Server                         │
│  ┌──────────────────────────────────────────────┐  │
│  │  3. Exchange code for access token           │  │
│  │  4. Fetch user info                          │  │
│  │  5. Create/update user in database           │  │
│  │  6. Generate JWT (access + refresh tokens)   │  │
│  │  7. Set HTTP-only cookies                    │  │
│  └──────────────────────────────────────────────┘  │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ 8. JWT in cookies
                   ▼
┌─────────────────────────────────────────────────────┐
│         Subsequent API Requests                     │
│  ┌──────────────────────────────────────────────┐  │
│  │  1. Extract JWT from cookie                  │  │
│  │  2. Validate signature & expiration          │  │
│  │  3. Extract user claims                      │  │
│  │  4. Check authorities/roles                  │  │
│  │  5. Execute business logic                   │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### Security Layers

1. **OAuth2 Authentication**: Social login (Google, Twitter)
2. **JWT Tokens**: Stateless authentication
3. **HTTP-only Cookies**: Secure token storage
4. **Spring Security**: Framework-level security
5. **Method-level Authorization**: `@PreAuthorize` annotations
6. **CORS**: Cross-origin request control

## OAuth2 Integration

### Supported Providers

#### Google OAuth2

**Configuration**:
```yaml
oauth:
  google:
    client-id: ${OAUTH_GOOGLE_CLIENT_ID}
    client-secret: ${OAUTH_GOOGLE_CLIENT_SECRET}
    authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
    token-uri: https://oauth2.googleapis.com/token
    user-info-uri: https://www.googleapis.com/oauth2/v2/userinfo
    redirect-uri: ${APP_URL}/api/v1/auth/oauth/callback
    scopes:
      - email
      - profile
```

**User Info Endpoint Response**:
```json
{
  "id": "google-user-id",
  "email": "user@gmail.com",
  "verified_email": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "picture": "https://..."
}
```

#### Twitter OAuth2

**Configuration**:
```yaml
oauth:
  twitter:
    client-id: ${OAUTH_TWITTER_CLIENT_ID}
    client-secret: ${OAUTH_TWITTER_CLIENT_SECRET}
    authorization-uri: https://twitter.com/i/oauth2/authorize
    token-uri: https://api.twitter.com/2/oauth2/token
    user-info-uri: https://api.twitter.com/2/users/me
    redirect-uri: ${APP_URL}/api/v1/auth/oauth/callback
    scopes:
      - users.read
      - tweet.read
```

**User Info Endpoint Response**:
```json
{
  "data": {
    "id": "twitter-user-id",
    "name": "John Doe",
    "username": "johndoe"
  }
}
```

### OAuth Flow Implementation

#### 1. Generate Authorization URL

**Endpoint**: `POST /api/v1/auth/oauth/url`

**Request**:
```json
{
  "provider": "GOOGLE"
}
```

**Implementation**:
```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;
    private final RedisStateStore redisStateStore;

    @PostMapping("/oauth/url")
    public ResponseEntity<OAuthUrlResponse> generateOAuthUrl(
            @RequestBody OAuthUrlRequest request) {

        ProviderType provider = request.getProvider();

        // 1. Generate random state
        String state = UUID.randomUUID().toString();

        // 2. Store state in Redis (expires in 10 minutes)
        redisStateStore.saveState(state, provider);

        // 3. Build authorization URL
        String authUrl = oAuthService.buildAuthorizationUrl(provider, state);

        return ResponseEntity.ok(new OAuthUrlResponse(authUrl));
    }
}
```

**OAuthService**:
```java
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final Map<ProviderType, OAuthClient> oAuthClients;

    public String buildAuthorizationUrl(ProviderType provider, String state) {
        OAuthClient client = oAuthClients.get(provider);

        return UriComponentsBuilder
            .fromUriString(client.getAuthorizationUri())
            .queryParam("client_id", client.getClientId())
            .queryParam("redirect_uri", client.getRedirectUri())
            .queryParam("response_type", "code")
            .queryParam("scope", String.join(" ", client.getScopes()))
            .queryParam("state", state)
            .build()
            .toUriString();
    }
}
```

#### 2. Handle OAuth Callback

**Endpoint**: `POST /api/v1/auth/oauth/callback`

**Query Parameters**:
- `code`: Authorization code
- `state`: State for CSRF protection

**Implementation**:
```java
@PostMapping("/oauth/callback")
public ResponseEntity<Void> handleOAuthCallback(
        @RequestParam String code,
        @RequestParam String state) {

    // 1. Validate state from Redis
    OAuthState oauthState = redisStateStore.getState(state)
        .orElseThrow(() -> new OAuthException(INVALID_STATE));

    ProviderType provider = oauthState.getProvider();

    // 2. Exchange code for access token
    OAuthClient client = oAuthClients.get(provider);
    String accessToken = client.exchangeCodeForToken(code);

    // 3. Fetch user info
    OAuthUserInfo userInfo = client.getUserInfo(accessToken);

    // 4. Create or update member
    Member member = memberService.createOrUpdateMember(userInfo, provider);

    // 5. Generate JWT tokens
    String jwtAccessToken = jwtService.generateAccessToken(member);
    String jwtRefreshToken = jwtService.generateRefreshToken(member);

    // 6. Set HTTP-only cookies
    ResponseCookie accessCookie = createSecureCookie(
        "access_token",
        jwtAccessToken,
        Duration.ofDays(1)
    );

    ResponseCookie refreshCookie = createSecureCookie(
        "refresh_token",
        jwtRefreshToken,
        Duration.ofDays(7)
    );

    // 7. Delete state from Redis
    redisStateStore.deleteState(state);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .build();
}

private ResponseCookie createSecureCookie(String name, String value, Duration maxAge) {
    return ResponseCookie.from(name, value)
        .httpOnly(true)
        .secure(isProduction()) // HTTPS only in production
        .sameSite("Strict")
        .path("/api")
        .maxAge(maxAge)
        .build();
}
```

### OAuth Client Implementation

**GoogleOAuthClient**:
```java
@Component
public class GoogleOAuthClient implements OAuthClient {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.token-uri}")
    private String tokenUri;

    @Value("${oauth.google.user-info-uri}")
    private String userInfoUri;

    private final WebClient webClient;

    @Override
    public String exchangeCodeForToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        TokenResponse response = webClient.post()
            .uri(tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();

        return response.getAccessToken();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        GoogleUserInfo userInfo = webClient.get()
            .uri(userInfoUri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(GoogleUserInfo.class)
            .block();

        return OAuthUserInfo.builder()
            .providerId(userInfo.getId())
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .build();
    }
}
```

## JWT Authentication

### Token Structure

**JWT Claims**:
```json
{
  "sub": "user-id-123",
  "email": "user@example.com",
  "role": "ROLE_MEMBER",
  "authorityTier": "FM",
  "iat": 1704067200,
  "exp": 1704153600,
  "iss": "pfplay"
}
```

### JwtService Implementation

```java
@Service
public class JwtService {

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${jwt.issuer}")
    private String issuer;

    private static final long ACCESS_TOKEN_VALIDITY = 24 * 3600 * 1000; // 24 hours
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 3600 * 1000; // 7 days

    /**
     * Generate access token
     */
    public String generateAccessToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
            .setSubject(member.getUserId().getValue())
            .claim("email", member.getEmail())
            .claim("role", "ROLE_MEMBER")
            .claim("authorityTier", member.getAuthorityTier().name())
            .setIssuedAt(now)
            .setExpiration(expiration)
            .setIssuer(issuer)
            .signWith(SignatureAlgorithm.HS512, accessTokenSecret)
            .compact();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);

        return Jwts.builder()
            .setSubject(member.getUserId().getValue())
            .setIssuedAt(now)
            .setExpiration(expiration)
            .setIssuer(issuer)
            .signWith(SignatureAlgorithm.HS512, refreshTokenSecret)
            .compact();
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract user ID from token
     */
    public UserId getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(accessTokenSecret)
            .parseClaimsJws(token)
            .getBody();

        return UserId.from(claims.getSubject());
    }

    /**
     * Extract all claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(accessTokenSecret)
            .parseClaimsJws(token)
            .getBody();
    }
}
```

### Token Resolution from Cookies

**CookieBearerTokenResolver**:
```java
@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
```

### JWT Authentication Converter

**CustomJwtAuthenticationConverter**:
```java
@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract claims
        String userId = jwt.getSubject();
        String email = jwt.getClaim("email");
        String role = jwt.getClaim("role");
        String authorityTier = jwt.getClaim("authorityTier");

        // Create authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        authorities.add(new SimpleGrantedAuthority(authorityTier));

        // Create principal
        UserPrincipal principal = UserPrincipal.builder()
            .userId(UserId.from(userId))
            .email(email)
            .role(role)
            .authorityTier(AuthorityTier.valueOf(authorityTier))
            .build();

        return new JwtAuthenticationToken(jwt, authorities, principal);
    }
}
```

## Spring Security Configuration

### SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final CookieBearerTokenResolver cookieBearerTokenResolver;
    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (stateless API)
            .csrf(csrf -> csrf.disable())

            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session management (stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/users/members/sign/**").permitAll()
                .requestMatchers("/api/v1/users/guests/sign/**").permitAll()
                .requestMatchers("/api/v1/partyrooms/link/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/spec/api/**").permitAll() // Swagger

                // WebSocket endpoint
                .requestMatchers("/ws/**").permitAll()

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // JWT authentication
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
                .bearerTokenResolver(cookieBearerTokenResolver)
            )

            // Exception handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler(new HttpStatusAccessDeniedHandler(HttpStatus.FORBIDDEN))
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(jwtService.getAccessTokenSecret().getBytes());
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
```

## Authorization

### Authority Tiers

```java
public enum AuthorityTier {
    FM,  // Full Member
    AM,  // Associate Member
    GT   // Guest
}
```

**Hierarchy**: FM > AM > GT

### Role-Based Access

**Roles**:
- `ROLE_MEMBER`: Authenticated member (OAuth)
- `ROLE_GUEST`: Anonymous guest

### Method-Level Security

**Examples**:

**Member Only**:
```java
@PostMapping("/playlists")
@PreAuthorize("hasRole('MEMBER')")
public ResponseEntity<PlaylistResponse> createPlaylist(
        @RequestBody PlaylistCreateRequest request) {
    // Only members can create playlists
}
```

**Full Members Only**:
```java
@PostMapping("/admin/operation")
@PreAuthorize("hasAuthority('FM')")
public ResponseEntity<Void> adminOperation() {
    // Only full members
}
```

**Owner Only**:
```java
@PutMapping("/users/{userId}/profile")
@PreAuthorize("#userId == authentication.principal.userId.value")
public ResponseEntity<ProfileResponse> updateProfile(
        @PathVariable String userId,
        @RequestBody ProfileUpdateRequest request) {
    // User can only update their own profile
}
```

**Complex Expression**:
```java
@DeleteMapping("/partyrooms/{partyroomId}")
@PreAuthorize("hasRole('MEMBER') and @partyroomSecurityService.isHost(#partyroomId, authentication.principal.userId)")
public ResponseEntity<Void> deletePartyroom(@PathVariable String partyroomId) {
    // Only the host can delete the party room
}
```

### Custom Security Service

```java
@Service
public class PartyroomSecurityService {

    private final PartyroomRepository partyroomRepository;

    public boolean isHost(String partyroomId, UserId userId) {
        return partyroomRepository.findById(partyroomId)
            .map(partyroom -> partyroom.getHostId().equals(userId))
            .orElse(false);
    }

    public boolean isCrew(String partyroomId, UserId userId) {
        return crewRepository.existsActiveCrewByPartyroomAndUser(
            PartyroomId.from(partyroomId),
            userId
        );
    }

    public boolean canManageRoom(String partyroomId, UserId userId) {
        return crewRepository.findActiveCrewByPartyroomAndUser(
            PartyroomId.from(partyroomId),
            userId
        ).map(crew -> crew.getGradeType().canManageRoom())
         .orElse(false);
    }
}
```

## WebSocket Security

### Module Separation

WebSocket security is split across two Gradle modules:

| Component | Module | Purpose |
|---|---|---|
| `WebSocketHandshakeInterceptor` | `realtime` | Intercepts handshake, delegates auth to port |
| `WebSocketAuthPort` | `realtime` | Port interface for auth abstraction |
| `JwtWebSocketAuthAdapter` | `api` | Implements WebSocketAuthPort using JwtCookieValidator |
| `SessionCachePort` | `realtime` | Port interface for session management |
| `PartyroomSessionCacheManager` | `api` | Implements SessionCachePort using Redis |

### Handshake Authentication

**WebSocketHandshakeInterceptor** (in `realtime` module):
```java
@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final WebSocketAuthPort webSocketAuthPort;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // Delegate JWT extraction/validation to WebSocketAuthPort
        Optional<String> userId = webSocketAuthPort.extractUserId(request);

        if (userId.isEmpty()) {
            return false; // Reject connection
        }

        attributes.put("userId", userId.get());
        return true;
    }
}
```

**JwtWebSocketAuthAdapter** (in `api` module, implements the port):
```java
@Component
@RequiredArgsConstructor
public class JwtWebSocketAuthAdapter implements WebSocketAuthPort {

    private final JwtCookieValidator jwtCookieValidator;

    @Override
    public Optional<String> extractUserId(ServerHttpRequest request) {
        // Extract JWT from cookie, validate, and return userId
        return jwtCookieValidator.validateAndExtractUserId(request);
    }
}
```

> **Key Design**: The `realtime` module has zero domain imports. JWT/cookie logic stays in `api` module; `realtime` only depends on the port interface.

### STOMP Configuration

**WebSocketConfig** (in `realtime` module):
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .addInterceptors(webSocketHandshakeInterceptor)
            .withSockJS();
    }
}
```

## CORS Configuration

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        if (isProduction()) {
            configuration.setAllowedOrigins(List.of(
                "https://pfplay.com",
                "https://www.pfplay.com"
            ));
        } else {
            configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:4000",
                "http://localhost:8080"
            ));
        }

        // Allowed methods
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies)
        configuration.setAllowCredentials(true);

        // Expose headers
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Set-Cookie"
        ));

        // Max age
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);

        return source;
    }
}
```

## Security Best Practices

### 1. Token Storage

✅ **Use HTTP-only cookies**:
- JavaScript cannot access
- Automatically included in requests
- CSRF protection with SameSite=Strict

❌ **Don't use localStorage**:
- Vulnerable to XSS attacks

### 2. Token Expiration

✅ **Set appropriate TTLs**:
- Access token: 24 hours (short-lived)
- Refresh token: 7 days (longer-lived)

✅ **Implement token refresh**:
```java
@PostMapping("/auth/refresh")
public ResponseEntity<Void> refreshToken(
        @CookieValue("refresh_token") String refreshToken) {

    if (!jwtService.validateRefreshToken(refreshToken)) {
        throw new UnauthorizedException("Invalid refresh token");
    }

    UserId userId = jwtService.getUserIdFromToken(refreshToken);
    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found"));

    String newAccessToken = jwtService.generateAccessToken(member);

    ResponseCookie cookie = createSecureCookie(
        "access_token",
        newAccessToken,
        Duration.ofDays(1)
    );

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
}
```

### 3. Password Security

For future password-based authentication:

✅ **Use BCrypt or Argon2**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Cost factor 12
}
```

✅ **Enforce password policies**:
```java
PasswordValidator validator = new PasswordValidator(Arrays.asList(
    new LengthRule(8, 100),
    new CharacterRule(EnglishCharacterData.UpperCase, 1),
    new CharacterRule(EnglishCharacterData.LowerCase, 1),
    new CharacterRule(EnglishCharacterData.Digit, 1),
    new CharacterRule(EnglishCharacterData.Special, 1),
    new WhitespaceRule()
));
```

### 4. Input Validation

✅ **Validate all inputs**:
```java
@PostMapping
public ResponseEntity<Response> create(
        @Valid @RequestBody CreateRequest request) {
    // @Valid triggers Bean Validation
}
```

✅ **Sanitize user input**:
```java
String sanitized = HtmlUtils.htmlEscape(userInput);
```

### 5. Rate Limiting

Future implementation:

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        String userId = extractUserId(request);
        String key = "rate:" + request.getRequestURI() + ":" + userId;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (count > 100) { // 100 requests per minute
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

        return true;
    }
}
```

### 6. Audit Logging

✅ **Log security events**:
```java
@Aspect
@Component
public class SecurityAuditAspect {

    @AfterReturning("@annotation(preAuthorize)")
    public void logAuthorizedAccess(JoinPoint joinPoint, PreAuthorize preAuthorize) {
        log.info("Authorized access: method={}, user={}, expression={}",
            joinPoint.getSignature(),
            getCurrentUserId(),
            preAuthorize.value());
    }

    @AfterThrowing(pointcut = "@annotation(preAuthorize)", throwing = "ex")
    public void logUnauthorizedAccess(JoinPoint joinPoint, PreAuthorize preAuthorize, Exception ex) {
        log.warn("Unauthorized access attempt: method={}, user={}, expression={}, error={}",
            joinPoint.getSignature(),
            getCurrentUserId(),
            preAuthorize.value(),
            ex.getMessage());
    }
}
```

### 7. Secrets Management

✅ **Use environment variables**:
```yaml
jwt:
  access-token-secret: ${JWT_ACCESS_TOKEN_SECRET}
  refresh-token-secret: ${JWT_REFRESH_TOKEN_SECRET}

oauth:
  google:
    client-secret: ${OAUTH_GOOGLE_CLIENT_SECRET}
```

❌ **Never commit secrets**:
- Add secrets to `.gitignore`
- Use secret management services (AWS Secrets Manager, etc.)

### 8. HTTPS Only (Production)

✅ **Enforce HTTPS**:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    if (isProduction()) {
        http.requiresChannel(channel ->
            channel.anyRequest().requiresSecure()
        );
    }
    // ...
}
```

---

**Related Documents**:
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - Authentication flows
- [API_CONVENTIONS.md](API_CONVENTIONS.md) - Security annotations
- [COMMON_TASKS.md](COMMON_TASKS.md) - Adding OAuth providers
