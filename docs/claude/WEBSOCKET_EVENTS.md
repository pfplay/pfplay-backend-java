# WebSocket Events

This document describes the WebSocket implementation and real-time event system in PFPlay.

## Table of Contents

- [WebSocket Architecture](#websocket-architecture)
- [Connection Setup](#connection-setup)
- [STOMP Protocol](#stomp-protocol)
- [Event Types](#event-types)
- [Publishing Events](#publishing-events)
- [Subscribing to Events](#subscribing-to-events)
- [Message Formats](#message-formats)
- [Error Handling](#error-handling)
- [Client Examples](#client-examples)

## WebSocket Architecture

### Overview

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Client A   │         │  Server 1   │         │  Server 2   │
│  (Browser)  │         │             │         │             │
└──────┬──────┘         └──────┬──────┘         └──────┬──────┘
       │                       │                        │
       │ WebSocket (STOMP)     │                        │
       │◄─────────────────────►│                        │
       │                       │                        │
       │                       │      Redis Pub/Sub     │
       │                       │◄──────────────────────►│
       │                       │                        │
       │                       │                        │
       │                       ▼                        ▼
       │                 ┌─────────┐            ┌─────────┐
       │                 │Client A │            │Client B │
       │                 │(via WS) │            │(via WS) │
       └─────────────────►         │            │         │
                         └─────────┘            └─────────┘
```

### Key Components

1. **STOMP over WebSocket**: Protocol for message framing
2. **Redis Pub/Sub**: Cross-instance message distribution
3. **Session Management**: User session tracking via Redis
4. **JWT Authentication**: Secure handshake authentication

### How It Works

1. Client connects to WebSocket endpoint with JWT
2. Server validates JWT and creates session
3. Client subscribes to topics
4. When event occurs:
   - Server publishes message to Redis
   - All server instances receive message
   - Each instance broadcasts to its connected clients
5. Client receives real-time updates

## Connection Setup

### Endpoint

```
ws://localhost:8080/ws
wss://api.pfplay.com/ws  // Production
```

### Handshake Authentication

**HTTP Upgrade Request**:
```
GET /ws HTTP/1.1
Host: localhost:8080
Upgrade: websocket
Connection: Upgrade
Cookie: access_token={JWT_TOKEN}
```

**Server validates**:
- JWT signature
- Token expiration
- User authorities

### Connection Flow

```java
// Server-side handshake interceptor
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // Extract JWT from cookie
        String token = extractTokenFromCookie(request);

        // Validate JWT
        if (!jwtService.validateToken(token)) {
            return false; // Reject connection
        }

        // Extract user info
        UserId userId = jwtService.getUserIdFromToken(token);
        attributes.put("userId", userId);

        return true; // Accept connection
    }
}
```

## STOMP Protocol

### STOMP Commands

| Command | Purpose | Example |
|---------|---------|---------|
| CONNECT | Establish connection | Initial handshake |
| SUBSCRIBE | Subscribe to topic | Listen for events |
| SEND | Send message | Publish message |
| UNSUBSCRIBE | Unsubscribe from topic | Stop listening |
| DISCONNECT | Close connection | Clean disconnect |

### Message Frame Format

```
COMMAND
header1:value1
header2:value2

Body^@
```

## Event Types

### 1. Chat Message Event

**Topic**: `/sub/groups/{chatroomId}`

**Purpose**: Real-time chat messages in party rooms

**Message Format**:
```json
{
  "id": "msg-123",
  "chatroomId": "room-456",
  "senderId": "user-789",
  "senderNickname": "DJ_Cool",
  "content": "Hello everyone!",
  "timestamp": "2024-01-01T12:00:00"
}
```

**Publish**:
```
/pub/groups/{chatroomId}/send
```

---

### 2. Party Room Access Event

**Topic**: `/sub/events/{partyroomId}/partyroom-access`

**Purpose**: User join/leave notifications

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "userId": "user-456",
  "userNickname": "DJ_Cool",
  "action": "ENTER",
  "timestamp": "2024-01-01T12:00:00"
}
```

**Action Types**:
- `ENTER`: User joined
- `EXIT`: User left

---

### 3. Playback Start Event

**Topic**: `/sub/events/{partyroomId}/playback-start`

**Purpose**: New track playback started

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "playbackId": "playback-456",
  "djUserId": "user-789",
  "djNickname": "DJ_Cool",
  "trackName": "Amazing Song",
  "linkId": "youtube-id-123",
  "duration": 180,
  "thumbnailImage": "https://img.youtube.com/vi/youtube-id-123/default.jpg",
  "startTime": "2024-01-01T12:00:00",
  "endTime": "2024-01-01T12:03:00"
}
```

---

### 4. Playback Skip Event

**Topic**: `/sub/events/{partyroomId}/playback-skip`

**Purpose**: Current track was skipped

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "playbackId": "playback-456",
  "skipRequesterId": "user-789",
  "skipRequesterNickname": "Manager1",
  "skipReason": "MANUAL_SKIP",
  "timestamp": "2024-01-01T12:01:30"
}
```

**Skip Reasons**:
- `MANUAL_SKIP`: Manually skipped by manager
- `AUTO_SKIP`: Automatically skipped (track ended)
- `DJ_REMOVED`: DJ was removed from queue

---

### 5. Playback Reaction Event

**Topic**: `/sub/events/{partyroomId}/playback-reaction`

**Purpose**: Reaction count updates

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "playbackId": "playback-456",
  "userId": "user-789",
  "reactionType": "LIKE",
  "likeCount": 42,
  "dislikeCount": 3,
  "grabCount": 15,
  "timestamp": "2024-01-01T12:01:00"
}
```

**Reaction Types**:
- `LIKE`: User liked the track
- `DISLIKE`: User disliked the track
- `GRAB`: User grabbed track to playlist

---

### 6. Playback Reaction Motion Event

**Topic**: `/sub/events/{partyroomId}/playback-reaction-motion`

**Purpose**: Animated reaction display

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "playbackId": "playback-456",
  "userId": "user-789",
  "userNickname": "DJ_Cool",
  "reactionType": "LIKE",
  "timestamp": "2024-01-01T12:01:00"
}
```

**Usage**: Trigger animation in UI (heart floating up, etc.)

---

### 7. Crew Grade Change Event

**Topic**: `/sub/events/{partyroomId}/crew-grade`

**Purpose**: Crew member grade/role changed

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "crewId": "crew-456",
  "userId": "user-789",
  "userNickname": "NewModerator",
  "oldGrade": "CLUBBER",
  "newGrade": "MODERATOR",
  "changerId": "user-001",
  "changerNickname": "HostUser",
  "timestamp": "2024-01-01T12:00:00"
}
```

**Grade Types**:
- `HOST`: Room owner
- `COMMUNITY_MANAGER`: Senior moderator
- `MODERATOR`: Basic moderator
- `CLUBBER`: Active participant
- `LISTENER`: Basic access

---

### 8. Crew Penalty Event

**Topic**: `/sub/events/{partyroomId}/crew-penalty`

**Purpose**: Penalty applied to crew member

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "crewId": "crew-456",
  "userId": "user-789",
  "userNickname": "BadUser",
  "penaltyType": "CHAT_BAN",
  "reason": "Inappropriate language",
  "enforcerId": "user-001",
  "enforcerNickname": "ModeratorUser",
  "duration": 30,
  "timestamp": "2024-01-01T12:00:00"
}
```

**Penalty Types**:
- `CHAT_MESSAGE_REMOVAL`: Specific message removed
- `CHAT_BAN`: Banned from chat (30 seconds)
- `ONE_TIME_EXPULSION`: Kicked from room
- `PERMANENT_BAN`: Cannot rejoin

---

### 9. Profile Update Event

**Topic**: `/sub/events/{partyroomId}/profile-update`

**Purpose**: User profile changed (nickname, avatar)

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "userId": "user-456",
  "oldNickname": "OldName",
  "newNickname": "NewName",
  "avatarBodyUri": "avatar://body/001",
  "avatarFaceUri": "avatar://face/002",
  "avatarIconUri": "avatar://icon/003",
  "timestamp": "2024-01-01T12:00:00"
}
```

---

### 10. Notice Update Event

**Topic**: `/sub/events/{partyroomId}/notice-update`

**Purpose**: Room notice/announcement changed

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "notice": "Welcome to the party! Please be respectful.",
  "updaterId": "user-001",
  "updaterNickname": "HostUser",
  "timestamp": "2024-01-01T12:00:00"
}
```

---

### 11. Party Room Deactivation Event

**Topic**: `/sub/events/{partyroomId}/partyroom-deactivation`

**Purpose**: Room is closing/terminated

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "reason": "HOST_TERMINATED",
  "message": "The host has closed this party room.",
  "timestamp": "2024-01-01T12:00:00"
}
```

**Reason Types**:
- `HOST_TERMINATED`: Host ended the room
- `INACTIVITY`: No activity for extended period
- `VIOLATION`: Room violated terms

---

### 12. DJ Queue Update Event

**Topic**: `/sub/events/{partyroomId}/dj-queue-update`

**Purpose**: DJ queue changed (someone joined/left)

**Message Format**:
```json
{
  "partyroomId": "room-123",
  "action": "ENQUEUE",
  "userId": "user-456",
  "userNickname": "NewDJ",
  "playlistId": "playlist-789",
  "playlistName": "My Awesome Mix",
  "queuePosition": 3,
  "timestamp": "2024-01-01T12:00:00"
}
```

**Actions**:
- `ENQUEUE`: DJ joined queue
- `DEQUEUE`: DJ left queue
- `REORDER`: Queue order changed

---

### 13. Heartbeat

**Topic**: `/sub/heartbeat`

**Purpose**: Keep connection alive

**Send**: `/pub/heartbeat`

**Message Format**:
```json
{
  "timestamp": "2024-01-01T12:00:00"
}
```

## Publishing Events

### From Application Service

```java
@Service
@Transactional
public class PlaybackApplicationService {

    private final RedisMessagePublisher redisMessagePublisher;

    public void startPlayback(PartyroomId partyroomId) {
        // Business logic...

        // Create event message
        PlaybackStartMessage message = PlaybackStartMessage.builder()
            .partyroomId(partyroomId.getValue())
            .playbackId(playback.getId())
            .djUserId(dj.getUserId())
            .djNickname(dj.getNickname())
            .trackName(track.getName())
            .linkId(track.getLinkId())
            .duration(track.getDuration())
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusSeconds(track.getDuration()))
            .build();

        // Publish to Redis
        redisMessagePublisher.publishPlaybackStart(partyroomId.getValue(), message);
    }
}
```

### Redis Publisher Implementation

```java
@Service
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

    // Other event publishers...
}
```

## Subscribing to Events

### Redis Listener Implementation

```java
@Component
@RequiredArgsConstructor
public class PlaybackEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @RedisMessageListener(topics = "partyroom:*:playback-start")
    public void handlePlaybackStart(PlaybackStartMessage message) {
        // This method is called when message is published to Redis

        // Broadcast to all WebSocket clients connected to THIS server instance
        messagingTemplate.convertAndSend(
            "/sub/events/" + message.getPartyroomId() + "/playback-start",
            message
        );

        // Optional: Additional processing
        schedulePlaybackEnd(message);
    }
}
```

### Custom Annotation for Redis Listeners

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisMessageListener {
    String[] topics();
}
```

### Registration in Redis Config

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            List<MessageListener> listeners) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Register all @RedisMessageListener annotated methods
        for (MessageListener listener : listeners) {
            container.addMessageListener(listener, getTopics(listener));
        }

        return container;
    }
}
```

## Message Formats

### Standard Message Structure

All WebSocket messages follow this structure:

```typescript
interface WebSocketMessage<T> {
  type: string;           // Event type
  data: T;                // Event-specific data
  timestamp: string;      // ISO 8601 timestamp
  partyroomId?: string;   // Optional party room context
}
```

### Serialization

**Jackson** is used for JSON serialization:

```java
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper());
        messageConverters.add(converter);
        return false;
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }
}
```

## Error Handling

### Connection Errors

**Authentication Failure**:
```
ERROR
message: Invalid or expired token

^@
```

**Client Action**: Redirect to login, refresh token

### Message Errors

**Invalid Message Format**:
```json
{
  "type": "ERROR",
  "message": "Invalid message format",
  "timestamp": "2024-01-01T12:00:00"
}
```

**Permission Denied**:
```json
{
  "type": "ERROR",
  "message": "You do not have permission to perform this action",
  "timestamp": "2024-01-01T12:00:00"
}
```

### Disconnect Handling

**Server-side**:
```java
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        // Clean up session
        sessionCacheManager.removeSession(sessionId);

        // Update user status
        userPresenceService.markOffline(sessionId);
    }
}
```

## Client Examples

### JavaScript/SockJS Example

```javascript
// Connect
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
  console.log('Connected:', frame);

  // Subscribe to party room events
  const partyroomId = 'room-123';

  // Chat messages
  stompClient.subscribe(`/sub/groups/${partyroomId}`, (message) => {
    const chatMsg = JSON.parse(message.body);
    displayChatMessage(chatMsg);
  });

  // Playback start
  stompClient.subscribe(`/sub/events/${partyroomId}/playback-start`, (message) => {
    const playback = JSON.parse(message.body);
    startPlayback(playback);
  });

  // Reactions
  stompClient.subscribe(`/sub/events/${partyroomId}/playback-reaction`, (message) => {
    const reaction = JSON.parse(message.body);
    updateReactionCounts(reaction);
  });

  // Reaction motions
  stompClient.subscribe(`/sub/events/${partyroomId}/playback-reaction-motion`, (message) => {
    const motion = JSON.parse(message.body);
    animateReaction(motion);
  });

  // Crew changes
  stompClient.subscribe(`/sub/events/${partyroomId}/crew-grade`, (message) => {
    const gradeChange = JSON.parse(message.body);
    updateCrewList(gradeChange);
  });
});

// Send chat message
function sendChatMessage(content) {
  const chatroomId = 'room-123';
  stompClient.send(`/pub/groups/${chatroomId}/send`, {}, JSON.stringify({
    content: content
  }));
}

// Send heartbeat
setInterval(() => {
  stompClient.send('/pub/heartbeat', {}, JSON.stringify({
    timestamp: new Date().toISOString()
  }));
}, 30000); // Every 30 seconds

// Disconnect
function disconnect() {
  if (stompClient) {
    stompClient.disconnect();
  }
}
```

### React Hook Example

```typescript
import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';

function useWebSocket(partyroomId: string) {
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [playback, setPlayback] = useState<Playback | null>(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('Connected');

        // Subscribe to events
        client.subscribe(`/sub/groups/${partyroomId}`, (message: IMessage) => {
          const chatMsg = JSON.parse(message.body);
          setMessages(prev => [...prev, chatMsg]);
        });

        client.subscribe(`/sub/events/${partyroomId}/playback-start`, (message: IMessage) => {
          const pb = JSON.parse(message.body);
          setPlayback(pb);
        });
      },
      onDisconnect: () => {
        console.log('Disconnected');
      }
    });

    client.activate();
    setStompClient(client);

    return () => {
      client.deactivate();
    };
  }, [partyroomId]);

  const sendMessage = (content: string) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: `/pub/groups/${partyroomId}/send`,
        body: JSON.stringify({ content })
      });
    }
  };

  return { messages, playback, sendMessage };
}
```

---

**Related Documents**:
- [REDIS_PATTERNS.md](REDIS_PATTERNS.md) - Redis pub/sub details
- [SECURITY.md](SECURITY.md) - WebSocket authentication
- [BUSINESS_FLOWS.md](BUSINESS_FLOWS.md) - Event publishing flows
