package com.pfplaybackend.api.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {

    // 도메인 특색이 드러난 예외에 대한 정보는 이곳에 존재할 수 없다.
    NO_SUCH_ELEMENT(HttpStatus.NOT_FOUND, "해당하는 값을 찾을 수 없습니다."),
    DUPLICATE_KEY(HttpStatus.CONFLICT, "이미 존재하는 값입니다."),
    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러입니다.", "E1"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 올바르지 않습니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    PARTY_ROOM_BAN(HttpStatus.NOT_ACCEPTABLE, "입장할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
    private String code;

    ExceptionEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    ExceptionEnum(HttpStatus status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public int getHttpStatusCode() {
        return this.status.value();
    }
}
