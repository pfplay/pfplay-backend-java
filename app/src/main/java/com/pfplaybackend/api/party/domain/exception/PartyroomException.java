package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum PartyroomException implements DomainException {
    NOT_FOUND_ROOM("PTR-001", "Can not find Partyroom", ErrorType.NOT_FOUND),
    ALREADY_TERMINATED("PTR-002", "Already Terminated Partyroom", ErrorType.FORBIDDEN),
    EXCEEDED_LIMIT("PTR-003", "Exceeded Entrance Limit", ErrorType.FORBIDDEN),
    ACTIVE_ANOTHER_ROOM("PTR-004", "Already Active in Another Partyroom", ErrorType.FORBIDDEN),
    CACHE_MISSED_SESSION("PTR-005", "No cached data found for sessionId", ErrorType.NOT_FOUND),
    RESTRICTED_AUTHORITY("PTR-006", "Authority Restriction", ErrorType.FORBIDDEN),
    ALREADY_HOST("PTR-007", "Already Host in Another Partyroom", ErrorType.FORBIDDEN);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    PartyroomException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
