package com.pfplaybackend.api.common;

import com.pfplaybackend.api.enums.ExceptionEnum;
import com.pfplaybackend.api.partyroom.exception.PartyRoomAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 공통 handle Exception
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @TODO  custom exception 모듈화 리팩토링 필요
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(NoSuchElementException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.NO_SUCH_ELEMENT.getHttpStatusCode())
                                        .message(ExceptionEnum.NO_SUCH_ELEMENT.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(PartyRoomAccessException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(PartyRoomAccessException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.PARTY_ROOM_BAN.getHttpStatusCode())
                                        .message(ExceptionEnum.PARTY_ROOM_BAN.getMessage())
                                        .build()
                        )
                );
    }

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

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<?> handleRuntimeException(RuntimeException e) {
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

}