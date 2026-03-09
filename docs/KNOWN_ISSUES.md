# Known Issues

## 1. WebSocket 재연결 시 세션 캐시 누락

**상태**: 앱 크래시 수정 완료 / 클라이언트 알림 미구현

### 현상

서버 재시작 후 클라이언트가 WebSocket 재연결(STOMP SUBSCRIBE)을 시도하면,
세션 캐시가 저장되지 않아 이후 채팅 등 세션 기반 기능이 동작하지 않는다.

### 원인

1. 서버 재시작 → 기존 Crew 입장 기록 정리됨
2. 클라이언트는 입장 API 없이 WebSocket 재연결만 시도
3. `RedisSessionCacheAdapter.saveSessionCache()` → `getActivePartyroomByUserId()` 결과 없음
4. 세션 캐시 미저장 → 채팅 등 후속 기능 실패

### 수정 이력

- **기존**: `NotFoundException` throw → 이벤트 리스너 스레드 전파 → 애플리케이션 크래시
- **현재**: warn 로그 출력 + 조기 return (앱 크래시 방지)

### 미해결 과제 (프론트엔드 협의 필요)

클라이언트에게 세션이 유효하지 않음을 알려주는 방법 결정:

**A. 구독 destination으로 에러 메시지 전송**
- `/sub/partyrooms/{id}`로 `{ type: "SESSION_INVALID" }` 메시지 push
- 클라이언트가 메시지를 받으면 재입장 또는 로비 이동
- 장점: 연결 유지, 클라이언트가 graceful하게 처리 가능

**B. STOMP ERROR 프레임 전송**
- 클라이언트가 ERROR 수신 → 재연결 로직에서 입장 API부터 다시 호출
- 단점: 연결 자체가 끊어짐

### 관련 파일

- `app/.../adapter/out/persistence/RedisSessionCacheAdapter.java` — 세션 캐시 저장 로직
- `realtime/.../event/SubscriptionEventListener.java` — SUBSCRIBE 이벤트 핸들러
