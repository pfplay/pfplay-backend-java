package com.pfplaybackend.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "공통 에러 응답")
public record ApiErrorResponse(
        @Schema(description = "HTTP 상태 코드") int status,
        @Schema(description = "도메인 에러 코드 (예: PTR-001, DJ-001)") String errorCode,
        @Schema(description = "에러 메시지") String message
) {
    public static ApiErrorResponse of(HttpStatus status, String errorCode, String message) {
        return new ApiErrorResponse(status.value(), errorCode, message);
    }
}
