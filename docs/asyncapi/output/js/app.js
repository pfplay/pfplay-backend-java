
    const schema = {
  "asyncapi": "3.0.0",
  "info": {
    "title": "PFPlay WebSocket API",
    "version": "1.0.0",
    "description": "PFPlay 실시간 음악 파티 플랫폼의 WebSocket 이벤트 규격서입니다.\n\n## 연결 방식\n- **프로토콜**: STOMP over WebSocket\n- **엔드포인트**: `ws://{host}/ws`\n- **인증**: JWT 토큰 (쿠키 기반, 핸드셰이크 시 검증)\n\n## 메시지 흐름\n```\nClient ──→ /pub/...          (클라이언트 → 서버)\nServer ──→ /sub/...          (서버 → 클라이언트, 그룹 브로드캐스트)\nServer ──→ /user/.../sub/... (서버 → 클라이언트, 개인 메시지)\n```\n\n## 공통 규칙\n- 모든 서버→클라이언트 브로드캐스트 메시지에는 `partyroomId`와 `eventType` 필드가 포함됩니다.\n- `eventType`은 해당 메시지의 토픽 이름과 동일합니다 (예: `chat`, `playback_start`).\n- `partyroomId`는 `{ \"id\": <number> }` 형태의 객체입니다.\n",
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
        "ChatMessage": {
          "name": "ChatMessage",
          "title": "채팅 메시지 (수신)",
          "summary": "그룹 채팅 메시지 브로드캐스트",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "그룹 채팅 브로드캐스트 메시지",
            "properties": {
              "partyroomId": {
                "type": "object",
                "description": "파티룸 식별자",
                "properties": {
                  "id": {
                    "type": "integer",
                    "format": "int64",
                    "description": "파티룸 ID",
                    "x-parser-schema-id": "<anonymous-schema-5>"
                  }
                },
                "required": [
                  "id"
                ],
                "x-parser-schema-id": "PartyroomId"
              },
              "eventType": {
                "type": "string",
                "const": "chat",
                "x-parser-schema-id": "<anonymous-schema-6>"
              },
              "crew": {
                "type": "object",
                "description": "채팅 발신자 정보",
                "properties": {
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-7>"
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
                    "x-parser-schema-id": "<anonymous-schema-8>"
                  },
                  "content": {
                    "type": "string",
                    "description": "메시지 텍스트",
                    "x-parser-schema-id": "<anonymous-schema-9>"
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
              "partyroomId",
              "eventType",
              "crew",
              "message"
            ],
            "x-parser-schema-id": "OutgoingGroupChatPayload"
          },
          "examples": [
            {
              "name": "일반 채팅",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "chat",
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
          "x-parser-unique-object-id": "ChatMessage"
        },
        "PartyroomAccessMessage": {
          "name": "PartyroomAccessMessage",
          "title": "입퇴장 알림",
          "summary": "크루 입장(ENTER) 또는 퇴장(EXIT) 브로드캐스트",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 입퇴장 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "partyroom_access",
                "x-parser-schema-id": "<anonymous-schema-10>"
              },
              "accessType": {
                "type": "string",
                "description": "입퇴장 타입",
                "enum": [
                  "ENTER",
                  "EXIT"
                ],
                "x-parser-schema-id": "AccessType"
              },
              "crew": {
                "type": "object",
                "description": "크루 요약 정보 (입퇴장 시 전달)",
                "properties": {
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-11>"
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
                    "x-parser-schema-id": "<anonymous-schema-12>"
                  },
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
                    "x-parser-schema-id": "<anonymous-schema-13>"
                  },
                  "avatarFaceUri": {
                    "type": "string",
                    "nullable": true,
                    "description": "아바타 얼굴 이미지 URI",
                    "x-parser-schema-id": "<anonymous-schema-14>"
                  },
                  "avatarIconUri": {
                    "type": "string",
                    "description": "아바타 아이콘 이미지 URI",
                    "x-parser-schema-id": "<anonymous-schema-15>"
                  },
                  "combinePositionX": {
                    "type": "integer",
                    "description": "얼굴 합성 X 좌표",
                    "x-parser-schema-id": "<anonymous-schema-16>"
                  },
                  "combinePositionY": {
                    "type": "integer",
                    "description": "얼굴 합성 Y 좌표",
                    "x-parser-schema-id": "<anonymous-schema-17>"
                  },
                  "offsetX": {
                    "type": "number",
                    "format": "double",
                    "description": "렌더링 X 오프셋",
                    "x-parser-schema-id": "<anonymous-schema-18>"
                  },
                  "offsetY": {
                    "type": "number",
                    "format": "double",
                    "description": "렌더링 Y 오프셋",
                    "x-parser-schema-id": "<anonymous-schema-19>"
                  },
                  "scale": {
                    "type": "number",
                    "format": "double",
                    "description": "렌더링 스케일",
                    "x-parser-schema-id": "<anonymous-schema-20>"
                  }
                },
                "required": [
                  "crewId",
                  "gradeType",
                  "nickname",
                  "avatarCompositionType",
                  "avatarBodyUri",
                  "avatarIconUri",
                  "combinePositionX",
                  "combinePositionY",
                  "offsetX",
                  "offsetY",
                  "scale"
                ],
                "x-parser-schema-id": "CrewSummary"
              }
            },
            "required": [
              "partyroomId",
              "eventType",
              "accessType",
              "crew"
            ],
            "x-parser-schema-id": "PartyroomAccessPayload"
          },
          "examples": [
            {
              "name": "크루 입장",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "partyroom_access",
                "accessType": "ENTER",
                "crew": {
                  "crewId": 42,
                  "gradeType": "CLUBBER",
                  "nickname": "DJ_파티왕",
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
          ],
          "x-parser-unique-object-id": "PartyroomAccessMessage"
        },
        "PartyroomDeactivationMessage": {
          "name": "PartyroomDeactivationMessage",
          "title": "재생 비활성화",
          "summary": "파티룸 재생이 비활성화됨 (DJ 큐가 비어 더 이상 재생할 곡이 없음)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "재생 비활성화 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "partyroom_deactivation",
                "x-parser-schema-id": "<anonymous-schema-21>"
              }
            },
            "required": [
              "partyroomId",
              "eventType"
            ],
            "x-parser-schema-id": "PartyroomDeactivationPayload"
          },
          "x-parser-unique-object-id": "PartyroomDeactivationMessage"
        },
        "PartyroomClosedMessage": {
          "name": "PartyroomClosedMessage",
          "title": "파티룸 종료",
          "summary": "호스트가 파티룸을 삭제하여 종료됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "파티룸 종료 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "partyroom_closed",
                "x-parser-schema-id": "<anonymous-schema-22>"
              }
            },
            "required": [
              "partyroomId",
              "eventType"
            ],
            "x-parser-schema-id": "PartyroomClosedPayload"
          },
          "x-parser-unique-object-id": "PartyroomClosedMessage"
        },
        "PlaybackStartMessage": {
          "name": "PlaybackStartMessage",
          "title": "재생 시작",
          "summary": "새 트랙 재생이 시작됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "재생 시작 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "playback_start",
                "x-parser-schema-id": "<anonymous-schema-23>"
              },
              "crewId": {
                "type": "integer",
                "format": "int64",
                "description": "현재 DJ의 크루 ID",
                "x-parser-schema-id": "<anonymous-schema-24>"
              },
              "playback": {
                "type": "object",
                "description": "현재 재생 트랙 정보",
                "properties": {
                  "id": {
                    "type": "integer",
                    "format": "int64",
                    "description": "재생 ID",
                    "x-parser-schema-id": "<anonymous-schema-25>"
                  },
                  "linkId": {
                    "type": "string",
                    "description": "음원 플랫폼 트랙 ID (예: YouTube 영상 ID)",
                    "x-parser-schema-id": "<anonymous-schema-26>"
                  },
                  "name": {
                    "type": "string",
                    "description": "트랙 이름",
                    "x-parser-schema-id": "<anonymous-schema-27>"
                  },
                  "duration": {
                    "type": "string",
                    "description": "재생 시간 (예: \"3:33\")",
                    "x-parser-schema-id": "<anonymous-schema-28>"
                  },
                  "thumbnailImage": {
                    "type": "string",
                    "description": "트랙 썸네일 이미지 URL",
                    "x-parser-schema-id": "<anonymous-schema-29>"
                  },
                  "endTime": {
                    "type": "integer",
                    "format": "int64",
                    "description": "재생 종료 예정 시각 (Unix timestamp, milliseconds)",
                    "x-parser-schema-id": "<anonymous-schema-30>"
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
              "partyroomId",
              "eventType",
              "crewId",
              "playback"
            ],
            "x-parser-schema-id": "PlaybackStartPayload"
          },
          "examples": [
            {
              "name": "재생 시작",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "playback_start",
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
          "x-parser-unique-object-id": "PlaybackStartMessage"
        },
        "ReactionMotionMessage": {
          "name": "ReactionMotionMessage",
          "title": "리액션 모션",
          "summary": "크루의 리액션 모션 애니메이션 (좋아요/싫어요/그랩 + 댄스 모션)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "리액션 모션 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "reaction_motion",
                "x-parser-schema-id": "<anonymous-schema-31>"
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
                    "x-parser-schema-id": "<anonymous-schema-32>"
                  }
                },
                "required": [
                  "crewId"
                ],
                "x-parser-schema-id": "CrewMotionInfo"
              }
            },
            "required": [
              "partyroomId",
              "eventType",
              "reactionType",
              "motionType",
              "crew"
            ],
            "x-parser-schema-id": "ReactionMotionPayload"
          },
          "examples": [
            {
              "name": "좋아요 + 댄스",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "reaction_motion",
                "reactionType": "LIKE",
                "motionType": "DANCE_TYPE_1",
                "crew": {
                  "crewId": 42
                }
              }
            }
          ],
          "x-parser-unique-object-id": "ReactionMotionMessage"
        },
        "ReactionAggregationMessage": {
          "name": "ReactionAggregationMessage",
          "title": "리액션 집계",
          "summary": "현재 재생 곡의 리액션 집계 업데이트",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "리액션 집계 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "reaction_aggregation",
                "x-parser-schema-id": "<anonymous-schema-33>"
              },
              "aggregation": {
                "type": "object",
                "description": "리액션 집계",
                "properties": {
                  "likeCount": {
                    "type": "integer",
                    "description": "좋아요 수",
                    "x-parser-schema-id": "<anonymous-schema-34>"
                  },
                  "dislikeCount": {
                    "type": "integer",
                    "description": "싫어요 수",
                    "x-parser-schema-id": "<anonymous-schema-35>"
                  },
                  "grabCount": {
                    "type": "integer",
                    "description": "그랩 수",
                    "x-parser-schema-id": "<anonymous-schema-36>"
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
              "partyroomId",
              "eventType",
              "aggregation"
            ],
            "x-parser-schema-id": "ReactionAggregationPayload"
          },
          "examples": [
            {
              "name": "집계 업데이트",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "reaction_aggregation",
                "aggregation": {
                  "likeCount": 5,
                  "dislikeCount": 1,
                  "grabCount": 2
                }
              }
            }
          ],
          "x-parser-unique-object-id": "ReactionAggregationMessage"
        },
        "DjQueueChangeMessage": {
          "name": "DjQueueChangeMessage",
          "title": "DJ 큐 변경",
          "summary": "DJ 큐 목록 전체가 갱신됨 (등록/해제/순서 변경 시)",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "DJ 큐 변경 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "dj_queue_change",
                "x-parser-schema-id": "<anonymous-schema-37>"
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
                      "x-parser-schema-id": "<anonymous-schema-39>"
                    },
                    "orderNumber": {
                      "type": "integer",
                      "format": "int64",
                      "description": "DJ 큐 순서 번호",
                      "x-parser-schema-id": "<anonymous-schema-40>"
                    },
                    "nickname": {
                      "type": "string",
                      "description": "닉네임",
                      "x-parser-schema-id": "<anonymous-schema-41>"
                    },
                    "avatarIconUri": {
                      "type": "string",
                      "description": "아바타 아이콘 이미지 URI",
                      "x-parser-schema-id": "<anonymous-schema-42>"
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
                "x-parser-schema-id": "<anonymous-schema-38>"
              }
            },
            "required": [
              "partyroomId",
              "eventType",
              "djs"
            ],
            "x-parser-schema-id": "DjQueueChangePayload"
          },
          "examples": [
            {
              "name": "DJ 큐 갱신",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "dj_queue_change",
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
          "x-parser-unique-object-id": "DjQueueChangeMessage"
        },
        "CrewGradeMessage": {
          "name": "CrewGradeMessage",
          "title": "등급 변경",
          "summary": "크루의 파티룸 내 등급이 변경됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 등급 변경 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "crew_grade",
                "x-parser-schema-id": "<anonymous-schema-43>"
              },
              "adjuster": {
                "type": "object",
                "description": "등급 변경 실행자",
                "properties": {
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "실행자 크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-44>"
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
                    "x-parser-schema-id": "<anonymous-schema-45>"
                  },
                  "prevGradeType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew.properties.gradeType",
                  "currGradeType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew.properties.gradeType"
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
              "partyroomId",
              "eventType",
              "adjuster",
              "adjusted"
            ],
            "x-parser-schema-id": "CrewGradePayload"
          },
          "examples": [
            {
              "name": "등급 승격",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "crew_grade",
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
          "x-parser-unique-object-id": "CrewGradeMessage"
        },
        "CrewPenaltyMessage": {
          "name": "CrewPenaltyMessage",
          "title": "페널티",
          "summary": "크루에게 페널티가 부과됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 페널티 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "crew_penalty",
                "x-parser-schema-id": "<anonymous-schema-46>"
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
                "x-parser-schema-id": "<anonymous-schema-47>"
              },
              "punisher": {
                "type": "object",
                "description": "페널티 부과자",
                "properties": {
                  "crewId": {
                    "type": "integer",
                    "format": "int64",
                    "description": "부과자 크루 ID",
                    "x-parser-schema-id": "<anonymous-schema-48>"
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
                    "x-parser-schema-id": "<anonymous-schema-49>"
                  }
                },
                "required": [
                  "crewId"
                ],
                "x-parser-schema-id": "PunishedInfo"
              }
            },
            "required": [
              "partyroomId",
              "eventType",
              "penaltyType",
              "detail",
              "punisher",
              "punished"
            ],
            "x-parser-schema-id": "CrewPenaltyPayload"
          },
          "examples": [
            {
              "name": "채팅 금지",
              "payload": {
                "partyroomId": {
                  "id": 10
                },
                "eventType": "crew_penalty",
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
          "x-parser-unique-object-id": "CrewPenaltyMessage"
        },
        "CrewProfileMessage": {
          "name": "CrewProfileMessage",
          "title": "프로필 변경",
          "summary": "크루의 프로필(닉네임, 아바타)이 변경됨",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "description": "크루 프로필 변경 이벤트",
            "properties": {
              "partyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
              "eventType": {
                "type": "string",
                "const": "crew_profile",
                "x-parser-schema-id": "<anonymous-schema-50>"
              },
              "crewId": {
                "type": "integer",
                "format": "int64",
                "description": "프로필이 변경된 크루 ID",
                "x-parser-schema-id": "<anonymous-schema-51>"
              },
              "nickname": {
                "type": "string",
                "description": "변경된 닉네임",
                "x-parser-schema-id": "<anonymous-schema-52>"
              },
              "avatarCompositionType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew.properties.avatarCompositionType",
              "avatarBodyUri": {
                "type": "string",
                "description": "아바타 바디 이미지 URI",
                "x-parser-schema-id": "<anonymous-schema-53>"
              },
              "avatarFaceUri": {
                "type": "string",
                "nullable": true,
                "description": "아바타 얼굴 이미지 URI (SINGLE_BODY일 경우 null)",
                "x-parser-schema-id": "<anonymous-schema-54>"
              },
              "avatarIconUri": {
                "type": "string",
                "description": "아바타 아이콘 이미지 URI",
                "x-parser-schema-id": "<anonymous-schema-55>"
              },
              "combinePositionX": {
                "type": "integer",
                "description": "얼굴 합성 X 좌표",
                "x-parser-schema-id": "<anonymous-schema-56>"
              },
              "combinePositionY": {
                "type": "integer",
                "description": "얼굴 합성 Y 좌표",
                "x-parser-schema-id": "<anonymous-schema-57>"
              },
              "offsetX": {
                "type": "number",
                "format": "double",
                "description": "렌더링 X 오프셋",
                "x-parser-schema-id": "<anonymous-schema-58>"
              },
              "offsetY": {
                "type": "number",
                "format": "double",
                "description": "렌더링 Y 오프셋",
                "x-parser-schema-id": "<anonymous-schema-59>"
              },
              "scale": {
                "type": "number",
                "format": "double",
                "description": "렌더링 스케일",
                "x-parser-schema-id": "<anonymous-schema-60>"
              }
            },
            "required": [
              "partyroomId",
              "eventType",
              "crewId",
              "nickname",
              "avatarCompositionType",
              "avatarBodyUri",
              "avatarIconUri",
              "combinePositionX",
              "combinePositionY",
              "offsetX",
              "offsetY",
              "scale"
            ],
            "x-parser-schema-id": "CrewProfilePayload"
          },
          "x-parser-unique-object-id": "CrewProfileMessage"
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
            "x-parser-schema-id": "<anonymous-schema-62>"
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
    "receiveChat": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "채팅 메시지 수신",
      "summary": "파티룸 그룹 채팅 메시지를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ChatMessage"
      ],
      "x-parser-unique-object-id": "receiveChat"
    },
    "receivePartyroomAccess": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "입퇴장 이벤트 수신",
      "summary": "크루 입장/퇴장 알림을 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage"
      ],
      "x-parser-unique-object-id": "receivePartyroomAccess"
    },
    "receivePartyroomDeactivation": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "재생 비활성화 수신",
      "summary": "파티룸 재생이 비활성화되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PartyroomDeactivationMessage"
      ],
      "x-parser-unique-object-id": "receivePartyroomDeactivation"
    },
    "receivePartyroomClosed": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "파티룸 종료 수신",
      "summary": "파티룸이 종료(삭제)되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosedMessage"
      ],
      "x-parser-unique-object-id": "receivePartyroomClosed"
    },
    "receivePlaybackStart": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "재생 시작 수신",
      "summary": "새 트랙 재생이 시작되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.PlaybackStartMessage"
      ],
      "x-parser-unique-object-id": "receivePlaybackStart"
    },
    "receiveReactionMotion": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "리액션 모션 수신",
      "summary": "크루의 리액션 모션 애니메이션 이벤트를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage"
      ],
      "x-parser-unique-object-id": "receiveReactionMotion"
    },
    "receiveReactionAggregation": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "리액션 집계 수신",
      "summary": "현재 재생 곡의 리액션 집계(좋아요/싫어요/그랩 카운트) 업데이트를 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationMessage"
      ],
      "x-parser-unique-object-id": "receiveReactionAggregation"
    },
    "receiveDjQueueChange": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "DJ 큐 변경 수신",
      "summary": "DJ 큐 목록이 변경(등록/해제/순서 변경)되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.DjQueueChangeMessage"
      ],
      "x-parser-unique-object-id": "receiveDjQueueChange"
    },
    "receiveCrewGrade": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "등급 변경 수신",
      "summary": "크루의 파티룸 내 등급이 변경되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewGradeMessage"
      ],
      "x-parser-unique-object-id": "receiveCrewGrade"
    },
    "receiveCrewPenalty": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "페널티 수신",
      "summary": "크루에게 페널티가 부과되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage"
      ],
      "x-parser-unique-object-id": "receiveCrewPenalty"
    },
    "receiveCrewProfile": {
      "action": "receive",
      "channel": "$ref:$.channels.partyroomBroadcast",
      "title": "프로필 변경 수신",
      "summary": "크루의 프로필(닉네임, 아바타)이 변경되었을 때 수신합니다.",
      "messages": [
        "$ref:$.channels.partyroomBroadcast.messages.CrewProfileMessage"
      ],
      "x-parser-unique-object-id": "receiveCrewProfile"
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
      "ChatMessage": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage",
      "PartyroomAccessMessage": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage",
      "PartyroomDeactivationMessage": "$ref:$.channels.partyroomBroadcast.messages.PartyroomDeactivationMessage",
      "PartyroomClosedMessage": "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosedMessage",
      "PlaybackStartMessage": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStartMessage",
      "ReactionMotionMessage": "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage",
      "ReactionAggregationMessage": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationMessage",
      "DjQueueChangeMessage": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChangeMessage",
      "CrewGradeMessage": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeMessage",
      "CrewPenaltyMessage": "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage",
      "CrewProfileMessage": "$ref:$.channels.partyroomBroadcast.messages.CrewProfileMessage",
      "HeartbeatResponse": "$ref:$.channels.heartbeatResponse.messages.HeartbeatResponse"
    },
    "schemas": {
      "PartyroomId": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.partyroomId",
      "EventType": {
        "type": "string",
        "description": "이벤트 타입 식별자",
        "enum": [
          "chat",
          "partyroom_access",
          "partyroom_deactivation",
          "partyroom_closed",
          "playback_start",
          "reaction_motion",
          "reaction_aggregation",
          "dj_queue_change",
          "crew_grade",
          "crew_penalty",
          "crew_profile"
        ],
        "x-parser-schema-id": "EventType"
      },
      "IncomingGroupChatPayload": "$ref:$.channels.groupChatSend.messages.IncomingGroupChatMessage.payload",
      "OutgoingGroupChatPayload": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload",
      "PartyroomAccessPayload": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload",
      "PartyroomDeactivationPayload": "$ref:$.channels.partyroomBroadcast.messages.PartyroomDeactivationMessage.payload",
      "PartyroomClosedPayload": "$ref:$.channels.partyroomBroadcast.messages.PartyroomClosedMessage.payload",
      "PlaybackStartPayload": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStartMessage.payload",
      "ReactionMotionPayload": "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage.payload",
      "ReactionAggregationPayload": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationMessage.payload",
      "DjQueueChangePayload": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChangeMessage.payload",
      "CrewGradePayload": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeMessage.payload",
      "CrewPenaltyPayload": "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage.payload",
      "CrewProfilePayload": "$ref:$.channels.partyroomBroadcast.messages.CrewProfileMessage.payload",
      "CrewInfo": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.crew",
      "ChatContent": "$ref:$.channels.partyroomBroadcast.messages.ChatMessage.payload.properties.message",
      "CrewSummary": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew",
      "CrewMotionInfo": "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage.payload.properties.crew",
      "PlaybackSnapshot": "$ref:$.channels.partyroomBroadcast.messages.PlaybackStartMessage.payload.properties.playback",
      "Aggregation": "$ref:$.channels.partyroomBroadcast.messages.ReactionAggregationMessage.payload.properties.aggregation",
      "DjWithProfile": "$ref:$.channels.partyroomBroadcast.messages.DjQueueChangeMessage.payload.properties.djs.items",
      "AdjusterInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeMessage.payload.properties.adjuster",
      "AdjustedInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewGradeMessage.payload.properties.adjusted",
      "PunisherInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage.payload.properties.punisher",
      "PunishedInfo": "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage.payload.properties.punished",
      "AccessType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.accessType",
      "GradeType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew.properties.gradeType",
      "ReactionType": "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage.payload.properties.reactionType",
      "MotionType": "$ref:$.channels.partyroomBroadcast.messages.ReactionMotionMessage.payload.properties.motionType",
      "PenaltyType": "$ref:$.channels.partyroomBroadcast.messages.CrewPenaltyMessage.payload.properties.penaltyType",
      "AvatarCompositionType": "$ref:$.channels.partyroomBroadcast.messages.PartyroomAccessMessage.payload.properties.crew.properties.avatarCompositionType"
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
  