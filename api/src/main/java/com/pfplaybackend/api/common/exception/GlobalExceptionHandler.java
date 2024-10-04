package com.pfplaybackend.api.common.exception;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.enums.ExceptionEnum;
import com.pfplaybackend.api.common.exception.http.*;
import com.pfplaybackend.api.playlist.exception.InvalidDeleteRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 공통 handle Exception
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // HTTP Exceptions
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());
        return createExceptionResponse(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage());
        return createExceptionResponse(e);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        log.error(e.getMessage());
        return createExceptionResponse(e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return createExceptionResponse(e);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException e) {
        log.error(e.getMessage());
        return createExceptionResponse(e);
    }

    private ResponseEntity<?> createExceptionResponse(AbstractHTTPException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .status(e.getStatus())
                                        .code(e.getStatus().value())
                                        .message(e.getMessage())
                                        .errorCode(e.getErrorCode())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getHttpStatusCode())
                                        .message(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                        .message(ExceptionEnum.EXCEPTION.getMessage())
                                        .build()
                        )
                );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                        .build()
                        )
                );
    }


    // TODO Delete Method
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(NoSuchElementException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.NO_SUCH_ELEMENT.getHttpStatusCode())
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    // TODO Delete Method
    @ExceptionHandler(InvalidDeleteRequestException.class)
    public ResponseEntity<?> handleInvalidDeleteRequestException(InvalidDeleteRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    // TODO Delete Method
    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.DUPLICATE_KEY.getHttpStatusCode())
                                        .message(ExceptionEnum.DUPLICATE_KEY.getMessage())
                                        .build()
                        )
                );
    }
}