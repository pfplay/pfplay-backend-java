package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum GradeException implements DomainException {
    MANAGER_GRADE_REQUIRED("GRD-001", "이 작업을 수행하려면 관리자 등급이 필요합니다", ErrorType.FORBIDDEN),
    UNABLE_TO_SET_HOST("GRD-002", "호스트 등급을 설정할 수 없습니다", ErrorType.FORBIDDEN),
    GRADE_INSUFFICIENT_FOR_OPERATION("GRD-003", "현재 등급으로는 해당 작업을 수행할 수 없습니다", ErrorType.FORBIDDEN),
    GRADE_EXCEEDS_ALLOWED_THRESHOLD("GRD-004", "지정한 등급이 허용 범위를 초과합니다", ErrorType.FORBIDDEN),
    GUEST_ONLY_POSSIBLE_LISTENER("GRD-005", "게스트는 리스너 등급만 가능합니다", ErrorType.FORBIDDEN);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    GradeException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
