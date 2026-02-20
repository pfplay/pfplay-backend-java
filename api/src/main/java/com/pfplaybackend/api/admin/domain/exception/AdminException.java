package com.pfplaybackend.api.admin.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum AdminException implements DomainException {
    NON_VIRTUAL_MEMBER_AVATAR_UPDATE("ADM-001", "Cannot update avatar of non-virtual member", ErrorType.FORBIDDEN),
    NON_VIRTUAL_MEMBER_DELETE("ADM-002", "Cannot delete non-virtual member", ErrorType.FORBIDDEN),
    NOT_VIRTUAL_MEMBER("ADM-003", "Not a virtual member", ErrorType.FORBIDDEN),
    MEMBER_NOT_FOUND("ADM-004", "Member not found", ErrorType.NOT_FOUND),
    INVALID_USER_ID_FORMAT("ADM-005", "Invalid user ID format", ErrorType.BAD_REQUEST),
    PARTYROOM_NOT_FOUND("ADM-006", "Partyroom not found", ErrorType.NOT_FOUND),
    NO_ACTIVE_PLAYBACK("ADM-007", "No active playback in partyroom", ErrorType.NOT_FOUND),
    NO_AVAILABLE_CREW("ADM-008", "No crew members available", ErrorType.NOT_FOUND),
    REACTION_SIMULATION_FAILED("ADM-009", "Reaction simulation failed", ErrorType.BAD_REQUEST),
    AVATAR_RESOURCES_NOT_INITIALIZED("ADM-010", "Avatar resources not initialized", ErrorType.BAD_REQUEST),
    MAIN_STAGE_NOT_FOUND("ADM-011", "Main stage not found", ErrorType.NOT_FOUND),
    NO_CREW_MEMBERS("ADM-012", "No crew members in partyroom", ErrorType.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    AdminException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
