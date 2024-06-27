package com.pfplaybackend.api.partyroom.domain.enums;

import lombok.Getter;

@Getter
public enum PenaltyType {
    CHAT_BAN_30_SECONDS("채팅 금지", 30),
    CHAT_MESSAGE_REMOVAL("채팅 메시지 삭제", 0), // 채팅 메시지 삭제 이벤트
    ONE_TIME_EXPULSION("일회성 강제 퇴장", 0), // 지속 시간이 필요 없으므로 0으로 설정
    PERMANENT_EXPULSION("영구 강제 퇴장", -1); // 영구 강제 퇴장을 -1로 설정

    private final String description;
    private final int durationInSeconds;

    PenaltyType(String description, int durationInSeconds) {
        this.description = description;
        this.durationInSeconds = durationInSeconds;
    }
}
