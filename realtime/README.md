# Realtime Module — WebSocket Infrastructure

## Bounded Context

WebSocket(STOMP) 통신 인프라를 제공하는 기술 모듈.
도메인 코드에 대한 의존이 **전혀 없으며**, Port 인터페이스만 정의하여
다른 모듈이 구현체를 제공하도록 한다.

## 책임

- WebSocket STOMP 설정 및 메시지 라우팅
- 핸드셰이크 시 인증 인터셉터
- 세션 연결/해제 이벤트 처리
- 메시지 발송 유틸리티 (`SimpMessageSender`)

## 제공하는 Port 인터페이스

| Port | 용도 | 구현체 위치 |
|------|------|------------|
| `WebSocketAuthPort` | WebSocket 핸드셰이크 JWT 인증 | `common` — `JwtWebSocketAuthAdapter` |
| `SessionCachePort` | WebSocket 세션 캐시 관리 | `app` — `PartyroomSessionCacheManager` |

## 소비하는 외부 Port

없음 (zero domain imports)

## 핵심 구성 요소

| 파일 | 역할 |
|------|------|
| `WebSocketConfig.java` | STOMP 엔드포인트, 메시지 브로커 설정 |
| `HttpHandshakeInterceptor.java` | 핸드셰이크 시 `WebSocketAuthPort`로 인증 |
| `WebSocketEventListener.java` | 세션 연결/해제 → `SessionCachePort` 위임 |
| `SimpMessageSender.java` | STOMP 메시지 발송 유틸리티 |
| `StompErrorHandler.java` | STOMP 에러 핸들링 |

## 의존 방향

```
realtime → (없음 — 최하위 모듈)
```

## 설계 원칙

- **Zero Domain Import**: `com.pfplaybackend.api.*` 패키지를 일절 import하지 않음
- 인증/세션 로직은 Port 인터페이스로 역전 → 구현체는 상위 모듈이 제공
- `spring-boot-starter-websocket` + `spring-security-web`만 의존
