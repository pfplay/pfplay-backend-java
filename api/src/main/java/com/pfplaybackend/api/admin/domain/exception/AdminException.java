package com.pfplaybackend.api.admin.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum AdminException implements DomainException {
    NON_VIRTUAL_MEMBER_AVATAR_UPDATE("ADM-001", "Cannot update avatar of non-virtual member", ErrorType.FORBIDDEN),
    NON_VIRTUAL_MEMBER_DELETE("ADM-002", "Cannot delete non-virtual member", ErrorType.FORBIDDEN),
    NOT_VIRTUAL_MEMBER("ADM-003", "Not a virtual member", ErrorType.FORBIDDEN),
    MEMBER_NOT_FOUND("ADM-004", "Member not found", ErrorType.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    AdminException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
