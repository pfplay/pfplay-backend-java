# User Module — User Management Context

## Bounded Context

사용자 관리 도메인. 회원(Member)/게스트(Guest) 등록, 프로필, 아바타, 지갑, 활동 추적을 담당한다.
OAuth 인증 후 사용자 생성과 프로필 관리가 핵심 책임이다.

## 책임

- 회원(OAuth)/게스트 등록 및 관리
- 사용자 프로필 (닉네임, 바이오, 아바타 선택)
- 아바타 리소스 관리 (Body, Face, Icon)
- 지갑 및 활동 추적
- 닉네임 유효성 검증 (Passay)

## 핵심 엔티티

| 엔티티 | 비즈니스 로직 |
|--------|-------------|
| `MemberData` | OAuth 회원, 프로필/활동 생성, 바이오 수정 |
| `GuestData` | 임시 익명 사용자 |
| `UserAccountData` | 계정 정보 (이메일, OAuth provider) |
| `ProfileData` | 프로필 (닉네임, 아바타 설정, 바이오) |
| `ActivityData` | 사용자 활동 추적 |
| `AvatarBodyResourceData` | 아바타 몸체 리소스 |
| `AvatarFaceResourceData` | 아바타 얼굴 리소스 |
| `AvatarIconResourceData` | 아바타 아이콘 리소스 |

## 제공하는 Port 인터페이스 (Cross-Domain)

| Port | 위치 | 용도 | 구현체 위치 |
|------|------|------|------------|
| `PlaylistSetupPort` | `application/port/out/` | 회원가입 시 기본 플레이리스트 생성 | `app/bootstrap` — `PlaylistSetupAdapter` |
| `OAuth2RedirectPort` | `application/port/out/` | OAuth2 리다이렉트 URI 생성 | `app/bootstrap` — `OAuth2RedirectAdapter` |

## 소비하는 외부 Port

없음 (Port 인터페이스만 정의하고, 구현체는 `app` 모듈이 제공)

## Application Service

| Service | 역할 |
|---------|------|
| `MemberSignService` | OAuth 회원 등록/로그인 (프로필, 활동, 플레이리스트 초기화) |
| `GuestSignService` | 게스트 생성 |
| `UserProfileService` | 프로필 조회/수정 |
| `UserAvatarService` | 아바타 선택/변경 |
| `UserBioService` | 바이오 수정 |
| `UserInfoService` | 사용자 정보 조회 |
| `UserWalletService` | 지갑 관리 |
| `UserActivityService` | 활동 추적 |
| `AvatarResourceService` | 아바타 리소스 CRUD |

## Domain Service

| Service | 역할 |
|---------|------|
| `UserAvatarDomainService` | 아바타 조합 유효성 검증 |
| `WalletDomainService` | 지갑 비즈니스 규칙 |

## 의존 방향

```
user → common (Shared Kernel)
```

## Authority Tier

- **FM** (Full Member): OAuth 인증 완료 회원
- **AM** (Associate Member): 제한된 회원
- **GT** (Guest): 임시 게스트
