
    const schema = {
  "asyncapi": "3.0.0",
  "info": {
    "title": "PFPlay WebSocket API",
    "version": "2.0.0",
    "description": "PFPlay 실시간 음악 파티 플랫폼의 WebSocket 이벤트 규격서입니다.\n\n## 연결 방식\n- **프로토콜**: STOMP over WebSocket\n- **엔드포인트**: `ws://{host}/ws`\n- **인증**: JWT 토큰 (쿠키 기반, 핸드셰이크 시 검증)\n\n## 메시지 흐름\n```\nClient ──→ /pub/...          (클라이언트 → 서버)\nServer ──→ /sub/...          (서버 → 클라이언트, 그룹 브로드캐스트)\nServer ──→ /user/.../sub/... (서버 → 클라이언트, 개인 메시지)\n```\n\n## 공통 규칙\n- 모든 서버→클라이언트 브로드캐스트 메시지에는 공통 메타데이터가 포함됩니다:\n  - `partyroomId` (integer) — 파티룸 식별자 (플랫 정수)\n  - `eventType` (string) — 이벤트 종류 (과거형 snake_case)\n  - `id` (string) — 메시지 고유 ID (UUID v4)\n  - `timestamp` (integer) — 메시지 발행 시각 (Unix epoch ms)\n- 이벤트 이름은 **과거형**을 사용합니다 (예: `playback_started`, `crew_entered`).\n",
    "contact": {
      "name": "PFPlay Backend Team"
    },
    "license": {
      "name": "Private"
    }
  },
  "servers": {
    "local": {
      "host": "localhost:8080",
      "pathname": "/ws",
      "protocol": "stomp",
      "description": "로컬 개발 서버"
    },
    "dev": {
      "host": "dev.pfplay.io",
      "pathname": "/ws",
      "protocol": "stomp",
      "description": "개발 서버"
    },
    "prod": {
      "host": "api.pfplay.io",
      "pathname": "/ws",
      "protocol": "stomp",
      "description": "운영 서버"
    }
  },
  "defaultContentType": "application/json",
  "channels": {
    "groupChatSend": {
      "address": "/pub/groups/{chatroomId}/send",
      "title": "그룹 채팅 전송",
      "description": "파티룸 그룹 채팅에 메시지를 전송합니다.",
      "parameters": {
        "chatroomId": {
          "description": "파티룸 ID"
        }
      },
      "messages": {
        "IncomingGroupChatMessage": {
          "name": "IncomingGroupChatMessage",
          "title": "채팅 메시지 (전송)",
          "summary": "그룹 채팅에 전송할 메시지",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "채팅 전송 메시지",
            "properties": {
              "content": {
                "type": "string",
                "description": "채팅 메시지 내용",
                "x-parser-schema-id": "<anonymous-schema-2>"
              }
            },
            "required": [
              "content"
            ],
            "x-parser-schema-id": "IncomingGroupChatPayload"
          },
          "x-parser-unique-object-id": "IncomingGroupChatMessage"
        }
      },
      "x-parser-unique-object-id": "groupChatSend"
    },
    "heartbeat": {
      "address": "/pub/heartbeat",
      "title": "하트비트",
      "description": "클라이언트 연결 유지를 위한 하트비트 요청입니다.",
      "messages": {
        "HeartbeatRequest": {
          "name": "HeartbeatRequest",
          "title": "하트비트 요청",
          "summary": "연결 유지를 위한 빈 메시지",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "x-parser-schema-id": "<anonymous-schema-3>"
          },
          "x-parser-unique-object-id": "HeartbeatRequest"
        }
      },
      "x-parser-unique-object-id": "heartbeat"
    },
    "partyroomBroadcast": {
      "address": "/sub/partyrooms/{partyroomId}",
      "title": "파티룸 브로드캐스트",
      "description": "파티룸 내 모든 이벤트가 이 채널로 브로드캐스트됩니다.\n클라이언트는 `eventType` 필드로 메시지 종류를 구분합니다.\n",
      "parameters": {
        "partyroomId": {
          "description": "파티룸 ID"
        }
      },
      "messages": {
        "ChatMessageSent": {
          "name": "ChatMessageSent",
          "title": "채팅 메시지 (수신)",
          "summary": "그룹 채팅 메시지 브로드캐스트",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "그룹 채팅 브로드캐스트 메시지",
            "allOf": [
              {
                "type": "object",
                "description": "모든 브로드캐스트 메시지의 공통 메타데이터",
                "properties": {
                  "partyroomId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "파티룸 ID (플랫 정수)",
                    "x-parser-schema-id": "<anonymous-schema-5>"
                  },
                  "eventType": {
                    "type": "string",
                    "description": "이벤트 타입 식별자 (과거형)",
                    "enum": [
                      "CHAT_MESSAGE_SENT",
                      "CREW_ENTERED",
                      "CREW_EXITED",
                      "PLAYBACK_DEACTIVATED",
                      "PARTYROOM_CLOSED",
                      "PLAYBACK_STARTED",
                      "REACTION_PERFORMED",
                      "REACTION_AGGREGATION_UPDATED",
                      "DJ_QUEUE_CHANGED",
                      "CREW_GRADE_CHANGED",
                      "CREW_PENALIZED",
                      "CREW_PROFILE_CHANGED"
                    ],
                    "x-parser-schema-id": "EventType"
                  },
                  "id": {
                    "type": "string",
                    "format": "uuid",
                    "description": "메시지 고유 ID (UUID v4)",
                    "x-parser-schema-id": "<anonymous-schema-6>"
                  },
                  "timestamp": {
                    "type": "integer",
                    "format": "int64",
                    "description": "메시지 발행 시각 (Unix epoch milliseconds)",
                    "x-parser-schema-id": "<anonymous-schema-7>"
                  }
                },
                "required": [
                  "partyroomId",
                  "eventType",
                  "id",
                  "timestamp"
                ],
                "x-parser-schema-id": "BroadcastMetadata"
              },
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CHAT_MESSAGE_SENT",
                    "x-parser-schema-id": "<anonymous-schema-9>"
                  },
                  "crew": {
                    "type": "object",
                    "description": "채팅 발신자 정보",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-10>"
                      }
                    },
                    "required": [
                      "crewId"
                    ],
                    "x-parser-schema-id": "CrewInfo"
                  },
                  "message": {
                    "type": "object",
                    "description": "채팅 메시지 내용",
                    "properties": {
                      "messageId": {
                        "type": "string",
                        "description": "메시지 고유 ID",
                        "x-parser-schema-id": "<anonymous-schema-11>"
                      },
                      "content": {
                        "type": "string",
                        "description": "메시지 텍스트",
                        "x-parser-schema-id": "<anonymous-schema-12>"
                      }
                    },
                    "required": [
                      "messageId",
                      "content"
                    ],
                    "x-parser-schema-id": "ChatContent"
                  }
                },
                "required": [
                  "crew",
                  "message"
                ],
                "x-parser-schema-id": "<anonymous-schema-8>"
              }
            ],
            "x-parser-schema-id": "ChatMessageSentPayload"
          },
          "examples": [
            {
              "name": "일반 채팅",
              "payload": {
                "partyroomId": 10,
                "eventType": "CHAT_MESSAGE_SENT",
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "timestamp": 1709964213000,
                "crew": {
                  "crewId": 42
                },
                "message": {
                  "messageId": "msg-abc-123",
                  "content": "좋은 노래네요!"
                }
              }
            }
          ],
          "x-parser-unique-object-id": "ChatMessageSent"
        },
        "CrewEntered": {
          "name": "CrewEntered",
          "title": "크루 입장",
          "summary": "새 크루가 파티룸에 입장함",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 입장 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CREW_ENTERED",
                    "x-parser-schema-id": "<anonymous-schema-14>"
                  },
                  "crew": {
                    "type": "object",
                    "description": "크루 요약 정보 (입장 시 전달)",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-15>"
                      },
                      "gradeType": {
                        "type": "string",
                        "description": "크루 등급 (숫자가 높을수록 상위 권한)\n- HOST(5): 파티룸 소유자\n- COMMUNITY_MANAGER(4): 커뮤니티 매니저\n- MODERATOR(3): 모더레이터\n- CLUBBER(2): 일반 참여자\n- LISTENER(1): 기본 등급\n",
                        "enum": [
                          "HOST",
                          "COMMUNITY_MANAGER",
                          "MODERATOR",
                          "CLUBBER",
                          "LISTENER"
                        ],
                        "x-parser-schema-id": "GradeType"
                      },
                      "nickname": {
                        "type": "string",
                        "description": "닉네임",
                        "x-parser-schema-id": "<anonymous-schema-16>"
                      },
                      "avatar": {
                        "type": "object",
                        "description": "아바타 프로필 정보",
                        "properties": {
                          "avatarCompositionType": {
                            "type": "string",
                            "description": "아바타 합성 타입\n- SINGLE_BODY: 단일 바디 (얼굴 없음)\n- BODY_WITH_FACE: 바디 + 얼굴 합성\n",
                            "enum": [
                              "SINGLE_BODY",
                              "BODY_WITH_FACE"
                            ],
                            "x-parser-schema-id": "AvatarCompositionType"
                          },
                          "avatarBodyUri": {
                            "type": "string",
                            "description": "아바타 바디 이미지 URI",
                            "x-parser-schema-id": "<anonymous-schema-17>"
                          },
                          "avatarFaceUri": {
                            "type": "string",
                            "nullable": true,
                            "description": "아바타 얼굴 이미지 URI (SINGLE_BODY일 경우 null)",
                            "x-parser-schema-id": "<anonymous-schema-18>"
                          },
                          "avatarIconUri": {
                            "type": "string",
                            "description": "아바타 아이콘 이미지 URI",
                            "x-parser-schema-id": "<anonymous-schema-19>"
                          },
                          "combinePositionX": {
                            "type": "integer",
                            "description": "얼굴 합성 X 좌표",
                            "x-parser-schema-id": "<anonymous-schema-20>"
                          },
                          "combinePositionY": {
                            "type": "integer",
                            "description": "얼굴 합성 Y 좌표",
                            "x-parser-schema-id": "<anonymous-schema-21>"
                          },
                          "offsetX": {
                            "type": "number",
                            "format": "double",
                            "description": "렌더링 X 오프셋",
                            "x-parser-schema-id": "<anonymous-schema-22>"
                          },
                          "offsetY": {
                            "type": "number",
                            "format": "double",
                            "description": "렌더링 Y 오프셋",
                            "x-parser-schema-id": "<anonymous-schema-23>"
                          },
                          "scale": {
                            "type": "number",
                            "format": "double",
                            "description": "렌더링 스케일",
                            "x-parser-schema-id": "<anonymous-schema-24>"
                          }
                        },
                        "required": [
                          "avatarCompositionType",
                          "avatarBodyUri",
                          "avatarIconUri",
                          "combinePositionX",
                          "combinePositionY",
                          "offsetX",
                          "offsetY",
                          "scale"
                        ],
                        "x-parser-schema-id": "AvatarProfile"
                      }
                    },
                    "required": [
                      "crewId",
                      "gradeType",
                      "nickname",
                      "avatar"
                    ],
                    "x-parser-schema-id": "CrewSummary"
                  }
                },
                "required": [
                  "crew"
                ],
                "x-parser-schema-id": "<anonymous-schema-13>"
              }
            ],
            "x-parser-schema-id": "CrewEnteredPayload"
          },
          "examples": [
            {
              "name": "크루 입장",
              "payload": {
                "partyroomId": 10,
                "eventType": "CREW_ENTERED",
                "id": "550e8400-e29b-41d4-a716-446655440001",
                "timestamp": 1709964213000,
                "crew": {
                  "crewId": 42,
                  "gradeType": "CLUBBER",
                  "nickname": "DJ_파티왕",
                  "avatar": {
                    "avatarCompositionType": "SINGLE_BODY",
                    "avatarBodyUri": "/avatars/body/1.png",
                    "avatarFaceUri": null,
                    "avatarIconUri": "/avatars/icon/1.png",
                    "combinePositionX": 0,
                    "combinePositionY": 0,
                    "offsetX": 0,
                    "offsetY": 0,
                    "scale": 1
                  }
                }
              }
            }
          ],
          "x-parser-unique-object-id": "CrewEntered"
        },
        "CrewExited": {
          "name": "CrewExited",
          "title": "크루 퇴장",
          "summary": "크루가 파티룸에서 퇴장함",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 퇴장 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CREW_EXITED",
                    "x-parser-schema-id": "<anonymous-schema-26>"
                  },
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "퇴장한 크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-27>"
                  }
                },
                "required": [
                  "crewId"
                ],
                "x-parser-schema-id": "<anonymous-schema-25>"
              }
            ],
            "x-parser-schema-id": "CrewExitedPayload"
          },
          "examples": [
            {
              "name": "크루 퇴장",
              "payload": {
                "partyroomId": 10,
                "eventType": "CREW_EXITED",
                "id": "550e8400-e29b-41d4-a716-446655440002",
                "timestamp": 1709964213000,
                "crewId": 42
              }
            }
          ],
          "x-parser-unique-object-id": "CrewExited"
        },
        "PlaybackDeactivated": {
          "name": "PlaybackDeactivated",
          "title": "재생 비활성화",
          "summary": "파티룸 재생이 비활성화됨 (DJ 큐가 비어 더 이상 재생할 곡이 없음)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "재생 비활성화 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "PLAYBACK_DEACTIVATED",
                    "x-parser-schema-id": "<anonymous-schema-29>"
                  }
                },
                "x-parser-schema-id": "<anonymous-schema-28>"
              }
            ],
            "x-parser-schema-id": "PlaybackDeactivatedPayload"
          },
          "x-parser-unique-object-id": "PlaybackDeactivated"
        },
        "PartyroomClosed": {
          "name": "PartyroomClosed",
          "title": "파티룸 종료",
          "summary": "호스트가 파티룸을 삭제하여 종료됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "파티룸 종료 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "PARTYROOM_CLOSED",
                    "x-parser-schema-id": "<anonymous-schema-31>"
                  }
                },
                "x-parser-schema-id": "<anonymous-schema-30>"
              }
            ],
            "x-parser-schema-id": "PartyroomClosedPayload"
          },
          "x-parser-unique-object-id": "PartyroomClosed"
        },
        "PlaybackStarted": {
          "name": "PlaybackStarted",
          "title": "재생 시작",
          "summary": "새 트랙 재생이 시작됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "재생 시작 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "PLAYBACK_STARTED",
                    "x-parser-schema-id": "<anonymous-schema-33>"
                  },
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "현재 DJ의 크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-34>"
                  },
                  "playback": {
                    "type": "object",
                    "description": "현재 재생 트랙 정보",
                    "properties": {
                      "id": {
                        "type": "integer",
                        "format": "int64",
                        "description": "재생 ID",
                        "x-parser-schema-id": "<anonymous-schema-35>"
                      },
                      "linkId": {
                        "type": "string",
                        "description": "음원 플랫폼 트랙 ID (예: YouTube 영상 ID)",
                        "x-parser-schema-id": "<anonymous-schema-36>"
                      },
                      "name": {
                        "type": "string",
                        "description": "트랙 이름",
                        "x-parser-schema-id": "<anonymous-schema-37>"
                      },
                      "duration": {
                        "type": "string",
                        "description": "재생 시간 (예: \"3:33\")",
                        "x-parser-schema-id": "<anonymous-schema-38>"
                      },
                      "thumbnailImage": {
                        "type": "string",
                        "description": "트랙 썸네일 이미지 URL",
                        "x-parser-schema-id": "<anonymous-schema-39>"
                      },
                      "endTime": {
                        "type": "integer",
                        "format": "int64",
                        "description": "재생 종료 예정 시각 (Unix timestamp, milliseconds)",
                        "x-parser-schema-id": "<anonymous-schema-40>"
                      }
                    },
                    "required": [
                      "id",
                      "linkId",
                      "name",
                      "duration",
                      "thumbnailImage",
                      "endTime"
                    ],
                    "x-parser-schema-id": "PlaybackSnapshot"
                  }
                },
                "required": [
                  "crewId",
                  "playback"
                ],
                "x-parser-schema-id": "<anonymous-schema-32>"
              }
            ],
            "x-parser-schema-id": "PlaybackStartedPayload"
          },
          "examples": [
            {
              "name": "재생 시작",
              "payload": {
                "partyroomId": 10,
                "eventType": "PLAYBACK_STARTED",
                "id": "550e8400-e29b-41d4-a716-446655440003",
                "timestamp": 1709964213000,
                "crewId": 42,
                "playback": {
                  "id": 1,
                  "linkId": "dQw4w9WgXcQ",
                  "name": "Rick Astley - Never Gonna Give You Up",
                  "duration": "3:33",
                  "thumbnailImage": "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg",
                  "endTime": 1709964213000
                }
              }
            }
          ],
          "x-parser-unique-object-id": "PlaybackStarted"
        },
        "ReactionPerformed": {
          "name": "ReactionPerformed",
          "title": "리액션 수행",
          "summary": "크루의 리액션 모션 애니메이션 (좋아요/싫어요/그랩 + 댄스 모션)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "리액션 모션 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "REACTION_PERFORMED",
                    "x-parser-schema-id": "<anonymous-schema-42>"
                  },
                  "reactionType": {
                    "type": "string",
                    "description": "리액션 종류",
                    "enum": [
                      "LIKE",
                      "DISLIKE",
                      "GRAB"
                    ],
                    "x-parser-schema-id": "ReactionType"
                  },
                  "motionType": {
                    "type": "string",
                    "description": "아바타 모션 타입",
                    "enum": [
                      "NONE",
                      "DANCE_TYPE_1",
                      "DANCE_TYPE_2"
                    ],
                    "x-parser-schema-id": "MotionType"
                  },
                  "crew": {
                    "type": "object",
                    "description": "리액션 모션 발신자 정보",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-43>"
                      }
                    },
                    "required": [
                      "crewId"
                    ],
                    "x-parser-schema-id": "CrewMotionInfo"
                  }
                },
                "required": [
                  "reactionType",
                  "motionType",
                  "crew"
                ],
                "x-parser-schema-id": "<anonymous-schema-41>"
              }
            ],
            "x-parser-schema-id": "ReactionPerformedPayload"
          },
          "examples": [
            {
              "name": "좋아요 + 댄스",
              "payload": {
                "partyroomId": 10,
                "eventType": "REACTION_PERFORMED",
                "id": "550e8400-e29b-41d4-a716-446655440004",
                "timestamp": 1709964213000,
                "reactionType": "LIKE",
                "motionType": "DANCE_TYPE_1",
                "crew": {
                  "crewId": 42
                }
              }
            }
          ],
          "x-parser-unique-object-id": "ReactionPerformed"
        },
        "ReactionAggregationUpdated": {
          "name": "ReactionAggregationUpdated",
          "title": "리액션 집계",
          "summary": "현재 재생 곡의 리액션 집계 업데이트",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "리액션 집계 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "REACTION_AGGREGATION_UPDATED",
                    "x-parser-schema-id": "<anonymous-schema-45>"
                  },
                  "aggregation": {
                    "type": "object",
                    "description": "리액션 집계",
                    "properties": {
                      "likeCount": {
                        "type": "integer",
                        "description": "좋아요 수",
                        "x-parser-schema-id": "<anonymous-schema-46>"
                      },
                      "dislikeCount": {
                        "type": "integer",
                        "description": "싫어요 수",
                        "x-parser-schema-id": "<anonymous-schema-47>"
                      },
                      "grabCount": {
                        "type": "integer",
                        "description": "그랩 수",
                        "x-parser-schema-id": "<anonymous-schema-48>"
                      }
                    },
                    "required": [
                      "likeCount",
                      "dislikeCount",
                      "grabCount"
                    ],
                    "x-parser-schema-id": "Aggregation"
                  }
                },
                "required": [
                  "aggregation"
                ],
                "x-parser-schema-id": "<anonymous-schema-44>"
              }
            ],
            "x-parser-schema-id": "ReactionAggregationUpdatedPayload"
          },
          "examples": [
            {
              "name": "집계 업데이트",
              "payload": {
                "partyroomId": 10,
                "eventType": "REACTION_AGGREGATION_UPDATED",
                "id": "550e8400-e29b-41d4-a716-446655440005",
                "timestamp": 1709964213000,
                "aggregation": {
                  "likeCount": 5,
                  "dislikeCount": 1,
                  "grabCount": 2
                }
              }
            }
          ],
          "x-parser-unique-object-id": "ReactionAggregationUpdated"
        },
        "DjQueueChanged": {
          "name": "DjQueueChanged",
          "title": "DJ 큐 변경",
          "summary": "DJ 큐 목록 전체가 갱신됨 (등록/해제/순서 변경 시)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "DJ 큐 변경 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "DJ_QUEUE_CHANGED",
                    "x-parser-schema-id": "<anonymous-schema-50>"
                  },
                  "djs": {
                    "type": "array",
                    "description": "현재 DJ 큐 전체 목록",
                    "items": {
                      "type": "object",
                      "description": "DJ 큐 항목 (프로필 포함)",
                      "properties": {
                        "crewId": {
                          "type": "integer",
                          "format": "int64",
                          "description": "크루 ID",
                          "x-parser-schema-id": "<anonymous-schema-52>"
                        },
                        "orderNumber": {
                          "type": "integer",
                          "format": "int64",
                          "description": "DJ 큐 순서 번호",
                          "x-parser-schema-id": "<anonymous-schema-53>"
                        },
                        "nickname": {
                          "type": "string",
                          "description": "닉네임",
                          "x-parser-schema-id": "<anonymous-schema-54>"
                        },
                        "avatarIconUri": {
                          "type": "string",
                          "description": "아바타 아이콘 이미지 URI",
                          "x-parser-schema-id": "<anonymous-schema-55>"
                        }
                      },
                      "required": [
                        "crewId",
                        "orderNumber",
                        "nickname",
                        "avatarIconUri"
                      ],
                      "x-parser-schema-id": "DjWithProfile"
                    },
                    "x-parser-schema-id": "<anonymous-schema-51>"
                  }
                },
                "required": [
                  "djs"
                ],
                "x-parser-schema-id": "<anonymous-schema-49>"
              }
            ],
            "x-parser-schema-id": "DjQueueChangedPayload"
          },
          "examples": [
            {
              "name": "DJ 큐 갱신",
              "payload": {
                "partyroomId": 10,
                "eventType": "DJ_QUEUE_CHANGED",
                "id": "550e8400-e29b-41d4-a716-446655440006",
                "timestamp": 1709964213000,
                "djs": [
                  {
                    "crewId": 42,
                    "orderNumber": 1,
                    "nickname": "DJ_파티왕",
                    "avatarIconUri": "/avatars/icon/1.png"
                  },
                  {
                    "crewId": 55,
                    "orderNumber": 2,
                    "nickname": "음악매니아",
                    "avatarIconUri": "/avatars/icon/2.png"
                  }
                ]
              }
            }
          ],
          "x-parser-unique-object-id": "DjQueueChanged"
        },
        "CrewGradeChanged": {
          "name": "CrewGradeChanged",
          "title": "등급 변경",
          "summary": "크루의 파티룸 내 등급이 변경됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 등급 변경 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CREW_GRADE_CHANGED",
                    "x-parser-schema-id": "<anonymous-schema-57>"
                  },
                  "adjuster": {
                    "type": "object",
                    "description": "등급 변경 실행자",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "실행자 크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-58>"
                      }
                    },
                    "required": [
                      "crewId"
                    ],
                    "x-parser-schema-id": "AdjusterInfo"
                  },
                  "adjusted": {
                    "type": "object",
                    "description": "등급 변경 대상자",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "대상자 크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-59>"
                      },
                      "prevGradeType": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.gradeType",
                      "currGradeType": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.gradeType"
                    },
                    "required": [
                      "crewId",
                      "prevGradeType",
                      "currGradeType"
                    ],
                    "x-parser-schema-id": "AdjustedInfo"
                  }
                },
                "required": [
                  "adjuster",
                  "adjusted"
                ],
                "x-parser-schema-id": "<anonymous-schema-56>"
              }
            ],
            "x-parser-schema-id": "CrewGradeChangedPayload"
          },
          "examples": [
            {
              "name": "등급 승격",
              "payload": {
                "partyroomId": 10,
                "eventType": "CREW_GRADE_CHANGED",
                "id": "550e8400-e29b-41d4-a716-446655440007",
                "timestamp": 1709964213000,
                "adjuster": {
                  "crewId": 1
                },
                "adjusted": {
                  "crewId": 42,
                  "prevGradeType": "CLUBBER",
                  "currGradeType": "MODERATOR"
                }
              }
            }
          ],
          "x-parser-unique-object-id": "CrewGradeChanged"
        },
        "CrewPenalized": {
          "name": "CrewPenalized",
          "title": "페널티",
          "summary": "크루에게 페널티가 부과됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 페널티 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CREW_PENALIZED",
                    "x-parser-schema-id": "<anonymous-schema-61>"
                  },
                  "penaltyType": {
                    "type": "string",
                    "description": "페널티 종류\n- CHAT_MESSAGE_REMOVAL: 채팅 메시지 삭제\n- CHAT_BAN_30_SECONDS: 30초 채팅 금지\n- ONE_TIME_EXPULSION: 1회 퇴장\n- PERMANENT_EXPULSION: 영구 추방\n",
                    "enum": [
                      "CHAT_MESSAGE_REMOVAL",
                      "CHAT_BAN_30_SECONDS",
                      "ONE_TIME_EXPULSION",
                      "PERMANENT_EXPULSION"
                    ],
                    "x-parser-schema-id": "PenaltyType"
                  },
                  "detail": {
                    "type": "string",
                    "description": "페널티 사유",
                    "x-parser-schema-id": "<anonymous-schema-62>"
                  },
                  "punisher": {
                    "type": "object",
                    "description": "페널티 부과자",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "부과자 크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-63>"
                      }
                    },
                    "required": [
                      "crewId"
                    ],
                    "x-parser-schema-id": "PunisherInfo"
                  },
                  "punished": {
                    "type": "object",
                    "description": "페널티 대상자",
                    "properties": {
                      "crewId": {
                        "type": "integer",
                        "format": "int64",
                        "description": "대상자 크루 ID",
                        "x-parser-schema-id": "<anonymous-schema-64>"
                      }
                    },
                    "required": [
                      "crewId"
                    ],
                    "x-parser-schema-id": "PunishedInfo"
                  }
                },
                "required": [
                  "penaltyType",
                  "detail",
                  "punisher",
                  "punished"
                ],
                "x-parser-schema-id": "<anonymous-schema-60>"
              }
            ],
            "x-parser-schema-id": "CrewPenalizedPayload"
          },
          "examples": [
            {
              "name": "채팅 금지",
              "payload": {
                "partyroomId": 10,
                "eventType": "CREW_PENALIZED",
                "id": "550e8400-e29b-41d4-a716-446655440008",
                "timestamp": 1709964213000,
                "penaltyType": "CHAT_BAN_30_SECONDS",
                "detail": "부적절한 채팅",
                "punisher": {
                  "crewId": 1
                },
                "punished": {
                  "crewId": 42
                }
              }
            }
          ],
          "x-parser-unique-object-id": "CrewPenalized"
        },
        "CrewProfileChanged": {
          "name": "CrewProfileChanged",
          "title": "프로필 변경",
          "summary": "크루의 프로필(닉네임, 아바타)이 변경됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 프로필 변경 이벤트",
            "allOf": [
              "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
              {
                "type": "object",
                "properties": {
                  "eventType": {
                    "type": "string",
                    "const": "CREW_PROFILE_CHANGED",
                    "x-parser-schema-id": "<anonymous-schema-66>"
                  },
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "프로필이 변경된 크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-67>"
                  },
                  "nickname": {
                    "type": "string",
                    "description": "변경된 닉네임",
                    "x-parser-schema-id": "<anonymous-schema-68>"
                  },
                  "avatar": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.avatar"
                },
                "required": [
                  "crewId",
                  "nickname",
                  "avatar"
                ],
                "x-parser-schema-id": "<anonymous-schema-65>"
              }
            ],
            "x-parser-schema-id": "CrewProfileChangedPayload"
          },
          "examples": [
            {
              "name": "프로필 변경",
              "payload": {
                "partyroomId": 10,
                "eventType": "CREW_PROFILE_CHANGED",
                "id": "550e8400-e29b-41d4-a716-446655440009",
                "timestamp": 1709964213000,
                "crewId": 42,
                "nickname": "새닉네임",
                "avatar": {
                  "avatarCompositionType": "BODY_WITH_FACE",
                  "avatarBodyUri": "/avatars/body/2.png",
                  "avatarFaceUri": "/avatars/face/1.png",
                  "avatarIconUri": "/avatars/icon/2.png",
                  "combinePositionX": 10,
                  "combinePositionY": 20,
                  "offsetX": 0.5,
                  "offsetY": 0.3,
                  "scale": 1.2
                }
              }
            }
          ],
          "x-parser-unique-object-id": "CrewProfileChanged"
        }
      },
      "x-parser-unique-object-id": "partyroomBroadcast"
    },
    "heartbeatResponse": {
      "address": "/user/{userId}/sub/heartbeat",
      "title": "하트비트 응답",
      "description": "개인 하트비트 응답입니다.",
      "parameters": {
        "userId": {
          "description": "사용자 ID (Principal)"
        }
      },
      "messages": {
        "HeartbeatResponse": {
          "name": "HeartbeatResponse",
          "title": "하트비트 응답",
          "summary": "서버에서 보내는 PONG 응답",
          "contentType": "text/plain",
          "payload": {
            "type": "string",
            "const": "PONG",
            "x-parser-schema-id": "<anonymous-schema-70>"
          },
          "x-parser-unique-object-id": "HeartbeatResponse"
        }
      },
      "x-parser-unique-object-id": "heartbeatResponse"
    }
  },
  "operations": {
    "sendGroupChat": {
      "action": "send",
      "channel": "$ref:$.channels.groupChatSend",
      "title": "채팅 메시지 전송",
      "summary": "파티룸 그룹 채팅에 메시지를 전송합니다.",
      "messages": [
        "$ref:$.channels.groupChatSend.messages.IncomingGroupChatMessage"
      ],
      "x-parser-unique-object-id": "sendGroupChat"
    },
    "sendHeartbeat": {
      "action": "send",
      "channel": "$ref:$.channels.heartbeat",
      "title": "하트비트 전송",
      "summary": "연결 유지를 위한 하트비트를 전송합니다.",
      "messages": [
        "$ref:$.channels.heartbeat.messages.HeartbeatRequest"
      ],
      "x-parser-unique-object-id": "sendHeartbeat"
    },
    "receiveChatMessageSent": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "채팅 메시지 수신",
      "summary": "파티룸 그룹 채팅 메시지를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent"
      ],
      "x-parser-unique-object-id": "receiveChatMessageSent"
    },
    "receiveCrewEntered": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "크루 입장 수신",
      "summary": "새 크루가 파티룸에 입장했을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewEntered"
      ],
      "x-parser-unique-object-id": "receiveCrewEntered"
    },
    "receiveCrewExited": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "크루 퇴장 수신",
      "summary": "크루가 파티룸에서 퇴장했을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewExited"
      ],
      "x-parser-unique-object-id": "receiveCrewExited"
    },
    "receivePlaybackDeactivated": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "재생 비활성화 수신",
      "summary": "파티룸 재생이 비활성화되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PlaybackDeactivated"
      ],
      "x-parser-unique-object-id": "receivePlaybackDeactivated"
    },
    "receivePartyroomClosed": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "파티룸 종료 수신",
      "summary": "파티룸이 종료(삭제)되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosed"
      ],
      "x-parser-unique-object-id": "receivePartyroomClosed"
    },
    "receivePlaybackStarted": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "재생 시작 수신",
      "summary": "새 트랙 재생이 시작되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PlaybackStarted"
      ],
      "x-parser-unique-object-id": "receivePlaybackStarted"
    },
    "receiveReactionPerformed": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "리액션 모션 수신",
      "summary": "크루의 리액션 모션 애니메이션 이벤트를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed"
      ],
      "x-parser-unique-object-id": "receiveReactionPerformed"
    },
    "receiveReactionAggregationUpdated": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "리액션 집계 수신",
      "summary": "현재 재생 곡의 리액션 집계(좋아요/싫어요/그랩 카운트) 업데이트를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationUpdated"
      ],
      "x-parser-unique-object-id": "receiveReactionAggregationUpdated"
    },
    "receiveDjQueueChanged": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "DJ 큐 변경 수신",
      "summary": "DJ 큐 목록이 변경(등록/해제/순서 변경)되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.DjQueueChanged"
      ],
      "x-parser-unique-object-id": "receiveDjQueueChanged"
    },
    "receiveCrewGradeChanged": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "등급 변경 수신",
      "summary": "크루의 파티룸 내 등급이 변경되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewGradeChanged"
      ],
      "x-parser-unique-object-id": "receiveCrewGradeChanged"
    },
    "receiveCrewPenalized": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "페널티 수신",
      "summary": "크루에게 페널티가 부과되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized"
      ],
      "x-parser-unique-object-id": "receiveCrewPenalized"
    },
    "receiveCrewProfileChanged": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "프로필 변경 수신",
      "summary": "크루의 프로필(닉네임, 아바타)이 변경되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewProfileChanged"
      ],
      "x-parser-unique-object-id": "receiveCrewProfileChanged"
    },
    "receiveHeartbeat": {
      "action": "receive",
      "channel": "$ref:$.channels.heartbeatResponse",
      "title": "하트비트 응답 수신",
      "summary": "서버로부터 하트비트 응답을 수신합니다.",
      "messages": [
        "$ref:$.channels.heartbeatResponse.messages.HeartbeatResponse"
      ],
      "x-parser-unique-object-id": "receiveHeartbeat"
    }
  },
  "components": {
    "messages": {
      "IncomingGroupChatMessage": "$ref:$.channels.groupChatSend.messages.IncomingGroupChatMessage",
      "HeartbeatRequest": "$ref:$.channels.heartbeat.messages.HeartbeatRequest",
      "ChatMessageSent": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent",
      "CrewEntered": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered",
      "CrewExited": "$ref:$.channels.partyroomBroadcast.messages.CrewExited",
      "PlaybackDeactivated": "$ref:$.channels.partyroomBroadcast.messages.PlaybackDeactivated",
      "PartyroomClosed": "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosed",
      "PlaybackStarted": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStarted",
      "ReactionPerformed": "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed",
      "ReactionAggregationUpdated": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationUpdated",
      "DjQueueChanged": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChanged",
      "CrewGradeChanged": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeChanged",
      "CrewPenalized": "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized",
      "CrewProfileChanged": "$ref:$.channels.partyroomBroadcast.messages.CrewProfileChanged",
      "HeartbeatResponse": "$ref:$.channels.heartbeatResponse.messages.HeartbeatResponse"
    },
    "schemas": {
      "BroadcastMetadata": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0]",
      "EventType": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[0].properties.eventType",
      "IncomingGroupChatPayload": "$ref:$.channels.groupChatSend.messages.IncomingGroupChatMessage.payload",
      "ChatMessageSentPayload": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload",
      "CrewEnteredPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload",
      "CrewExitedPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewExited.payload",
      "PlaybackDeactivatedPayload": "$ref:$.channels.partyroomBroadcast.messages.PlaybackDeactivated.payload",
      "PartyroomClosedPayload": "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosed.payload",
      "PlaybackStartedPayload": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStarted.payload",
      "ReactionPerformedPayload": "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed.payload",
      "ReactionAggregationUpdatedPayload": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationUpdated.payload",
      "DjQueueChangedPayload": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChanged.payload",
      "CrewGradeChangedPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeChanged.payload",
      "CrewPenalizedPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized.payload",
      "CrewProfileChangedPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewProfileChanged.payload",
      "CrewInfo": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[1].properties.crew",
      "ChatContent": "$ref:$.channels.partyroomBroadcast.messages.ChatMessageSent.payload.allOf[1].properties.message",
      "CrewSummary": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew",
      "AvatarProfile": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.avatar",
      "CrewMotionInfo": "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed.payload.allOf[1].properties.crew",
      "PlaybackSnapshot": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStarted.payload.allOf[1].properties.playback",
      "Aggregation": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationUpdated.payload.allOf[1].properties.aggregation",
      "DjWithProfile": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChanged.payload.allOf[1].properties.djs.items",
      "AdjusterInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeChanged.payload.allOf[1].properties.adjuster",
      "AdjustedInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeChanged.payload.allOf[1].properties.adjusted",
      "PunisherInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized.payload.allOf[1].properties.punisher",
      "PunishedInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized.payload.allOf[1].properties.punished",
      "GradeType": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.gradeType",
      "ReactionType": "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed.payload.allOf[1].properties.reactionType",
      "MotionType": "$ref:$.channels.partyroomBroadcast.messages.ReactionPerformed.payload.allOf[1].properties.motionType",
      "PenaltyType": "$ref:$.channels.partyroomBroadcast.messages.CrewPenalized.payload.allOf[1].properties.penaltyType",
      "AvatarCompositionType": "$ref:$.channels.partyroomBroadcast.messages.CrewEntered.payload.allOf[1].properties.crew.properties.avatar.properties.avatarCompositionType"
    }
  },
  "x-parser-spec-parsed": true,
  "x-parser-api-version": 3,
  "x-parser-spec-stringified": true
};
    const config = {"show":{"sidebar":true},"sidebar":{"showOperations":"byDefault"}};
    const appRoot = document.getElementById('root');
    AsyncApiStandalone.render(
        { schema, config, }, appRoot
    );
  